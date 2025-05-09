package dasniko.client.auth;

import dasniko.client.Config;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

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
}
