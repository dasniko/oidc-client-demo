package dasniko.client.auth;

import dasniko.client.Config;
import dasniko.client.api.IdpClient;
import dasniko.client.model.Identity;
import dasniko.client.model.TokenForm;
import dasniko.client.model.TokenResponse;
import dasniko.client.model.UserInfoResponse;
import io.quarkus.qute.Template;
import io.quarkus.security.UnauthorizedException;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.net.URI;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@RequestScoped
public class AuthManager {

	@Inject
	ApplicationState app;
	@Inject
	Config config;
	@Inject
	UriInfo uriInfo;
	@Inject
	IdpClient	idpClient;
	@Inject
	JwtService jwtService;
	@Inject
	SessionState session;
	@Inject
	Template error;

	Response redirectToAuthorizationEndpoint() {
		preparePkce();
		String authzUrl = app.getOpenidConfig().get("authorization_endpoint").toString();
		URI authzEndpoint = UriBuilder.fromUri(authzUrl)
			.queryParam("response_type", "code")
			.queryParam("scope", "openid profile email")
			.queryParam("client_id", config.clientId())
			.queryParam("redirect_uri", uriInfo.getBaseUri())
			.queryParam("state", UUID.randomUUID().toString())
			.queryParam("code_challenge_method", "S256")
			.queryParam("code_challenge", session.getCodeChallenge())
			.build();
		return Response.status(Response.Status.FOUND)
			.location(authzEndpoint).build();
	}

	private void preparePkce() {
		String codeVerifier = RandomStringUtils.random(128, 0 , 0, true, true, null, new SecureRandom());
		byte[] sha256 = DigestUtils.sha256(codeVerifier);
		String codeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(sha256);
		session.setCodeVerifier(codeVerifier);
		session.setCodeChallenge(codeChallenge);
	}

	Response codeToToken(String code) {
		try {
			TokenForm form = new TokenForm();
			form.setGrantType("authorization_code");
			form.setCode(code);
			form.setClientId(config.clientId());
			form.setClientSecret(config.clientSecret());
			form.setRedirectUri(uriInfo.getBaseUri().toString());
			form.setCodeVerifier(session.getCodeVerifier());

			TokenResponse tokenResponse = idpClient.getToken(form);

			String idTokenString = tokenResponse.idToken();
			JsonWebToken idToken = jwtService.verify(idTokenString);
			if (!idToken.getIssuer().equals(app.getOpenidConfig().get("issuer"))) {
				throw new IllegalStateException("Issuer of ID token does not match the issuer of the OpenID configuration.");
			}
			if (!idToken.getAudience().contains(config.clientId())) {
				throw new IllegalStateException("Audience of ID token does not match the client ID.");
			}

			session.setIdentity(Identity.fromIdToken(idToken));
			session.setIdToken(idTokenString);
			session.setAccessToken(tokenResponse.accessToken());
			session.setRefreshToken(tokenResponse.refreshToken());

			callUserInfoEndpoint();

			return Response.status(Response.Status.FOUND)
				.location(uriInfo.getBaseUri()).build();
		} catch (WebApplicationException e) {
			String errorString = e.getResponse().getStatusInfo().getReasonPhrase();
			return Response.ok(error.data("error", errorString).render())
				.type("text/html").build();
		} catch (ParseException e) {
			return redirectToAuthorizationEndpoint();
		} catch (Exception e) {
			return Response.ok(error.data("error", e.getMessage()).render())
				.type("text/html").build();
		}
	}

	private void callUserInfoEndpoint() {
		// call userinfo endpoint and join info to identity
		try {
			UserInfoResponse userinfo = idpClient.getUserInfo(session.getAccessToken());
			if (userinfo != null) {
				session.getIdentity().addUserinfo(userinfo);
			}
		} catch (Exception e) {
			// noop
		}
	}

	public void refreshToken() {
		TokenForm form = new TokenForm();
		form.setGrantType("refresh_token");
		form.setClientId(config.clientId());
		form.setClientSecret(config.clientSecret());
		form.setRefreshToken(session.getRefreshToken());

		try {
			TokenResponse tokenResponse = idpClient.getToken(form);
			session.setIdToken(tokenResponse.idToken());
			session.setAccessToken(tokenResponse.accessToken());
			session.setRefreshToken(tokenResponse.refreshToken());
		} catch (WebApplicationException e) {
			if (e.getResponse().getStatus() == 401) {
				throw new UnauthorizedException();
			} else {
				throw e;
			}
		}
	}
}
