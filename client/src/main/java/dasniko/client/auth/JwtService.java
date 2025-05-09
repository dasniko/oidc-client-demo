package dasniko.client.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

@Slf4j
@RequestScoped
public class JwtService {

	@Inject
	ObjectMapper objectMapper;
	@Inject
	ApplicationState app;
	@Inject
	JWTParser jwtParser;

	JsonWebToken verify(String tokenString) throws Exception {
		// find proper key
		String[] parts = tokenString.split("\\.");
		String headerString = new String(Base64.getDecoder().decode(parts[0]));
		Map<String, String> header = objectMapper.readValue(headerString, new TypeReference<>() {});
		String kid = header.get("kid");

		Map<String, Object> key = app.getJsonWebKey(kid);

		// create public key for verification
		BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode((String) key.get("n")));
		BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode((String) key.get("e")));
		RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, exponent);
		PublicKey publicKey = KeyFactory.getInstance("RSA")
			.generatePublic(publicKeySpec);

		// return token if valid
		return jwtParser.verify(tokenString, publicKey);
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
