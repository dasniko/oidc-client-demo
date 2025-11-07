package dasniko.client.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Base64;
import java.util.Map;

@Slf4j
@RequestScoped
public class JwtService {

	@Inject
	ObjectMapper objectMapper;

	JsonWebToken verify(String tokenString) throws Exception {
		throw new IllegalStateException("Not yet implemented");

		// TODO find proper key

		// TODO create public key for verification

		// TODO return token if valid
	}

	// this is only for demo purposes, DON'T DO THIS AT HOME!
	@SneakyThrows
	public String getParsedPayload(String tokenString) {
		String[] parts = tokenString.split("\\.");
		String payloadString = new String(Base64.getDecoder().decode(parts[1]));
		Map<String, Object> payload = objectMapper.readValue(payloadString, new TypeReference<>() {});
		return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload);
	}

}
