package dasniko.client.auth;

import dasniko.client.Config;
import dasniko.client.api.IdpClient;
import dasniko.client.model.Identity;
import dasniko.client.model.TokenForm;
import dasniko.client.model.TokenResponse;
import io.quarkus.qute.Template;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.net.URI;
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
		String authzUrl = app.getOpenidConfig().get("authorization_endpoint").toString();
		URI authzEndpoint = UriBuilder.fromUri(authzUrl)
			.queryParam("response_type", "code")
			.queryParam("scope", "openid profile email")
			.queryParam("client_id", config.clientId())
			.queryParam("redirect_uri", uriInfo.getBaseUri())
			.queryParam("state", UUID.randomUUID().toString())
			.build();
		return Response.status(Response.Status.FOUND)
			.location(authzEndpoint).build();
	}

	Response codeToToken(String code) {
		try {
			TokenForm form = new TokenForm();
			form.setGrantType("authorization_code");
			form.setCode(code);
			form.setClientId(config.clientId());
			form.setClientSecret(config.clientSecret());
			form.setRedirectUri(uriInfo.getBaseUri().toString());

			TokenResponse tokenResponse = idpClient.getToken(form);

			String idTokenString = tokenResponse.idToken();
			JsonWebToken idToken = jwtService.verify(idTokenString);
			if (!idToken.getIssuer().equals(app.getOpenidConfig().get("issuer"))) {
				throw new IllegalStateException("Issuer of ID token does not match the issuer of the OpenID configuration.");
			}
			if (!idToken.getAudience().contains(config.clientId())) {
				throw new IllegalStateException("Audience of ID token does not match the client ID.");
			}

			// call userinfo endpoint

			session.setIdentity(Identity.fromIdToken(idToken));
			session.setIdToken(idTokenString);
			session.setAccessToken(tokenResponse.accessToken());
			session.setRefreshToken(tokenResponse.refreshToken());

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
}
