package dasniko.client.auth;

import io.quarkus.security.UnauthorizedException;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UnauthorizedExceptionMapper implements ExceptionMapper<UnauthorizedException> {

	@Inject
	AuthManager authManager;

	@Override
	public Response toResponse(UnauthorizedException exception) {
		return authManager.redirectToAuthorizationEndpoint();
	}
}
