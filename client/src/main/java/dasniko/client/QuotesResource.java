package dasniko.client;

import dasniko.client.api.ApiService;
import dasniko.client.model.ApiResponse;
import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Map;

@Slf4j
@Path("/")
@Produces(MediaType.TEXT_HTML)
public class QuotesResource {

	@Inject
	Template index;
	@Inject
	@RestClient
	ApiService apiService;

	@GET
	public Response getQuote() {
		String apiQuote;
		try {
			// TODO get valid access token
			String accessToken = "????";
			ApiResponse response = apiService.getData("Bearer " + accessToken);
			apiQuote = response.quote();
		} catch (WebApplicationException e) {
			log.error("WebApplicationException occurred while requesting quote from resource server.", e);
			apiQuote = e.getResponse().getStatusInfo().getReasonPhrase();
		} catch (Exception e) {
			log.error("Exception occurred while trying to get a valid access_token", e);
			apiQuote = e.getMessage();
		}

		Map<String, Object> data = Map.of(
			"identity", "???",
			"quote", apiQuote
		);
		return Response.ok(index.data(data).render()).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void dummy() {
		// no-op
	}

}
