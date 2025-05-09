package dasniko.client.auth;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class SecurityContainerRequestFilter implements ContainerRequestFilter {

	@Inject
	SessionState session;
	@Inject
	AuthManager authManager;

	@Override
	public void filter(ContainerRequestContext requestContext) {
		Response response = null;
		if (session.getIdentity() == null) {
			response = authManager.redirectToAuthorizationEndpoint();
		}

		if (response != null) {
			requestContext.abortWith(response);
		}
	}

}
