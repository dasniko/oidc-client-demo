package dasniko.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.Map;


@RegisterRestClient(configKey = "api")
public interface ApiService {

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	Map<String, String> getData(@HeaderParam("X-Authorization") String authorizationHeader);

}
