package dasniko.resourceserver;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class SecurityContainerRequestFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) {
		// TODO implement me!
	}

}
