package dasniko.resourceserver;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;

import java.util.Map;

@Slf4j
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class ApiResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getData() {
		return Response.ok(Map.of ("quote", new Faker().yoda().quote())).build();
	}

}
