package dasniko.client;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
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

	@Override
	public void filter(ContainerRequestContext requestContext) {
		// TODO implement me!

		Response response = null;
		if (session.getIdentity() == null) {
			response = redirectToAuthorizationEndpoint();
		}

		if (response != null) {
			requestContext.abortWith(response);
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
