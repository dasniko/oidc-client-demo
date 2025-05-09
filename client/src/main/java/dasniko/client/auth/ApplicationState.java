package dasniko.client.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import dasniko.client.Config;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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

	void onStart(@Observes StartupEvent event) {
		// TODO implement me!
	}

}
