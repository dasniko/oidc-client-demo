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

	@SneakyThrows
	void onStart(@Observes StartupEvent event) {
		URL openidConfigUrl = new URL(config.baseUrl() + "/.well-known/openid-configuration");
		openidConfig = objectMapper.readValue(openidConfigUrl, new TypeReference<>() {});
		log.debug("Received OpenID configuration: {}", openidConfig);
	}

}
