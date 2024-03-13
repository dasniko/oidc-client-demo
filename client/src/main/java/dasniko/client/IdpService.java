package dasniko.client;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.Map;


@RegisterRestClient(configKey = "idp")
public interface IdpService {

	@GET
	@Path("/.well-known/openid-configuration")
	Map<String, Object> getOpenidConfiguration();

	@GET
	@Path("{jwks-uri}")
	Map<String, Object> getJsonWebKeySet(@PathParam("jwks-uri") String jwksUri);

	@POST
	@Path("{tokenPath}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	Map<String, Object> getToken(@PathParam("tokenPath") String tokenPath, @BeanParam TokenForm tokenForm);

	@POST
	@Path("{userinfoPath}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	Identity getUserInfo(@PathParam("userinfoPath") String userinfoPath, @FormParam("access_token") String accessToken);

}
