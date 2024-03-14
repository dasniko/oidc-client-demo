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
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.security.auth.login.CredentialExpiredException;
import java.net.URI;
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
		// TODO implement me!

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

	private Response redirectToAuthorizationEndpoint() {
		String authorizationEndpointString = (String) app.getOpenidConfig().get("authorization_endpoint");
		URI authorizationEndpoint = UriBuilder.fromUri(authorizationEndpointString)
			.queryParam("response_type", "code")
			.queryParam("state", UUID.randomUUID())
			.queryParam("client_id", config.get().clientId())
			.queryParam("redirect_uri", info.getBaseUri())
			.queryParam("scope", "openid")
			.build();
		return Response.status(Response.Status.FOUND).location(authorizationEndpoint).build();
	}

}
