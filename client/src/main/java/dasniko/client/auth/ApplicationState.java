package dasniko.client.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dasniko.client.Config;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.List;
import java.util.Map;

@Slf4j
@ApplicationScoped
public class ApplicationState {

	@Inject
	Config config;
	@Inject
	ObjectMapper objectMapper;

	@Getter
	Map<String, Object> openidConfig;

	private Map<String, Object> jsonWebKeySet = Map.of();

	@SneakyThrows
	void onStart(@Observes StartupEvent event) {
		URL openidConfigUrl = new URL(config.baseUrl() + "/.well-known/openid-configuration");
		openidConfig = objectMapper.readValue(openidConfigUrl, new TypeReference<>() {});
		log.debug("Received OpenID configuration: {}", openidConfig);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getJsonWebKey(String kid) {
		return ((List<Map<String, Object>>) getJsonWebKeySet().get("keys"))
			.stream().filter(k -> k.get("kid").equals(kid)).findFirst().orElseThrow();
	}

	@SneakyThrows
	private Map<String, Object> getJsonWebKeySet() {
		if (jsonWebKeySet.isEmpty()) {
			URL jwksUri = new URL((String) openidConfig.get("jwks_uri"));
			jsonWebKeySet = objectMapper.readValue(jwksUri, new TypeReference<>() {});
			log.debug("Received JSON Web Key Set: {}", jsonWebKeySet);
		}
		return jsonWebKeySet;
	}

}
