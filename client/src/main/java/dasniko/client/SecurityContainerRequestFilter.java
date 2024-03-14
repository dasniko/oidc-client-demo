package dasniko.client;

import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.security.auth.login.CredentialExpiredException;
import java.net.URI;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Provider
public class SecurityContainerRequestFilter implements ContainerRequestFilter {

	@Inject
	SessionState session;
	@Inject
	ApplicationState app;
	@Inject
	jakarta.inject.Provider<Config> config;
	@Inject
	UriInfo info;
	@Inject
	@RestClient
	jakarta.inject.Provider<IdpService> idpService;
	@Inject
	JwtService jwtService;
	@Inject
	Template error;

	@Override
	public void filter(ContainerRequestContext requestContext) {

		Response response = null;
		MultivaluedMap<String, String> queryParameters = info.getQueryParameters();
		if (queryParameters.containsKey("iss") && queryParameters.containsKey("code")) {
			String iss = queryParameters.getFirst("iss");
			String code = queryParameters.getFirst("code");
			response = codeToToken(iss, code);
		} else if (session.getIdentity() == null) {
			response = redirectToAuthorizationEndpoint();
		}

		if (response != null) {
			requestContext.abortWith(response);
		}
	}

	private Response codeToToken(String iss, String code) {
		try {
			TokenForm form = new TokenForm();
			form.setGrantType("authorization_code");
			form.setCode(code);
			form.setClientId(config.get().clientId());
			form.setClientSecret(config.get().clientSecret());
			form.setRedirectUri(String.valueOf(info.getBaseUri()));
			form.setCodeVerifier(session.getCodeVerifier());

			String tokenPath = app.getEndpointPathFromConfig("token_endpoint");
			Map<String, Object> tokenResponse = idpService.get().getToken(tokenPath, form);
			log.debug("Received token response: {}", tokenResponse);

			String idTokenString = (String) tokenResponse.get("id_token");
			JsonWebToken idToken = jwtService.verify(idTokenString);
			if (!idToken.getIssuer().equals(iss) || !idToken.getIssuer().equals(app.getOpenidConfig().get("issuer"))) {
				throw new RuntimeException("invalid issuer");
			}

			session.setIdentity(Identity.fromIdToken(idToken));
			session.setIdToken(idTokenString);
			session.setAccessToken((String) tokenResponse.get("access_token"));
			session.setRefreshToken((String) tokenResponse.get("refresh_token"));

			callUserInfoEndpoint();

			return Response.status(Response.Status.FOUND).location(info.getBaseUri()).build();
		} catch (WebApplicationException e) {
			String reasonPhrase = e.getResponse().getStatusInfo().getReasonPhrase();
			return Response.ok(error.data("error", reasonPhrase).render()).type(MediaType.TEXT_HTML).build();
		} catch (CredentialExpiredException e) {
			return redirectToAuthorizationEndpoint();
		} catch (Exception e) {
			return Response.ok(error.data("error", e.getMessage()).render()).type(MediaType.TEXT_HTML).build();
		}
	}

	private void callUserInfoEndpoint() {
		String userInfoPath = app.getEndpointPathFromConfig("userinfo_endpoint");
		Identity userInfo = idpService.get().getUserInfo(userInfoPath, session.getAccessToken());
		log.debug("Received UserInfo: {}", userInfo);
		if (userInfo != null) {
			session.getIdentity().addUserinfo(userInfo);
		}
	}

	private Response redirectToAuthorizationEndpoint() {
		preparePkce();
		String authorizationEndpointString = (String) app.getOpenidConfig().get("authorization_endpoint");
		URI authorizationEndpoint = UriBuilder.fromUri(authorizationEndpointString)
			.queryParam("response_type", "code")
			.queryParam("state", UUID.randomUUID())
			.queryParam("client_id", config.get().clientId())
			.queryParam("redirect_uri", info.getBaseUri())
			.queryParam("scope", "openid")
			.queryParam("code_challenge_method", "S256")
			.queryParam("code_challenge", session.getCodeChallenge())
			.build();
		return Response.status(Response.Status.FOUND).location(authorizationEndpoint).build();
	}

	private void preparePkce() {
		String codeVerifier = RandomStringUtils.random(64, 0, 0, true, true, null, new SecureRandom());
		byte[] sha256 = DigestUtils.sha256(codeVerifier);
		String codeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(sha256);
		session.setCodeVerifier(codeVerifier);
		session.setCodeChallenge(codeChallenge);
	}

}
