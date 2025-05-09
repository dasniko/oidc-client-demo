package dasniko.client.auth;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class SecurityContainerRequestFilter implements ContainerRequestFilter {

	@Inject
	SessionState session;
	@Inject
	AuthManager authManager;
	@Inject
	UriInfo uriInfo;

	@Override
	public void filter(ContainerRequestContext requestContext) {
		MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();

		Response response = null;
		if (queryParameters.containsKey("code") && queryParameters.containsKey("iss")) {
			response = authManager.codeToToken(queryParameters.getFirst("code"));
		} else if (session.getIdentity() == null) {
			response = authManager.redirectToAuthorizationEndpoint();
		}

		if (response != null) {
			requestContext.abortWith(response);
		}
	}

}
