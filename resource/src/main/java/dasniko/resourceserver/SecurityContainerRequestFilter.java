package dasniko.resourceserver;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Map;

@Slf4j
@Provider
public class SecurityContainerRequestFilter implements ContainerRequestFilter {

	@Inject
	JwtService jwtService;
	@ConfigProperty(name = "idp.iss")
	String iss;

	@Override
	public void filter(ContainerRequestContext requestContext) {
		Response.ResponseBuilder response = null;

		String authorization = requestContext.getHeaderString("X-Authorization");
		if (authorization == null || !"bearer ".equalsIgnoreCase(authorization.substring(0, 7))) {
			response = Response.status(Response.Status.UNAUTHORIZED);
		} else {

			try {
				String tokenString = authorization.substring(7);
				JsonWebToken token = jwtService.verify(tokenString);
				if (!iss.equals(token.getIssuer())) {
					response = Response.status(Response.Status.UNAUTHORIZED)
						.entity(Map.of("error", "wrong issuer"));
				}
			} catch (Exception e) {
				response = Response.status(Response.Status.UNAUTHORIZED)
					.entity(Map.of("error", e.getMessage()));
			}
		}

		if (response != null) {
			requestContext.abortWith(response.type("application/json").build());
		}
	}

}
