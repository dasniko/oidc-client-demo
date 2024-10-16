package dasniko.client.api;

import dasniko.client.model.ApiResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;


@RegisterRestClient(configKey = "api")
public interface ApiService {

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	ApiResponse getData(@HeaderParam("X-Authorization") String authorizationHeader);

}
