package dasniko.client;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
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
	public TemplateInstance getQuote() {
		String apiQuote;
		try {
			// TODO get valid access token
			String accessToken = "????";
			Map<String, String> response = apiService.getData("Bearer " + accessToken);
			apiQuote = response.get("quote");
		} catch (WebApplicationException e) {
			apiQuote = e.getResponse().getStatusInfo().getReasonPhrase();
		} catch (Exception e) {
			apiQuote = e.getMessage();
		}

		Map<String, Object> data = Map.of(
			"identity", "???",
			"quote", apiQuote
		);
		return index.data(data);
	}

}
