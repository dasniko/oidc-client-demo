package dasniko.resourceserver;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Map;

@Slf4j
@ApplicationScoped
public class ApplicationState {

	@ConfigProperty(name = "idp.jwks-uri")
	String jwksUri;
	@Inject
	ObjectMapper objectMapper;

	@Getter
	Map<String, Object> jsonWebKeySet;

	void onStart(@Observes StartupEvent event) {
		log.info("Starting resource server, loading JSON Web Key Set from IdP...");
		try {
			jsonWebKeySet = objectMapper.readValue(new URL(jwksUri), new TypeReference<>() {});
			log.debug("Received JSON Web Key Set: {}", jsonWebKeySet);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
