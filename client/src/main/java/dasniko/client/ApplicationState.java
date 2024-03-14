package dasniko.client;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Map;

@Slf4j
@ApplicationScoped
public class ApplicationState {

	@Inject
	Config config;
	@Inject
	@RestClient
	IdpService idpService;

	@Getter
	Map<String, Object> openidConfig;
	@Getter
	Map<String, Object> jsonWebKeySet;

	void onStart(@Observes StartupEvent event) {
		log.info("Starting application, loading OIDC configuration...");
		openidConfig = idpService.getOpenidConfiguration();
		log.debug("Received OIDC config: {}", openidConfig);

		String jwksUri = getEndpointPathFromConfig("jwks_uri");
		jsonWebKeySet = idpService.getJsonWebKeySet(jwksUri);
		log.debug("Received JWKS: {}", jsonWebKeySet);
	}

	String getEndpointPathFromConfig(String key) {
		String endpoint = (String) openidConfig.get(key);
		return endpoint.replace(config.baseUrl(), "");
	}

}
