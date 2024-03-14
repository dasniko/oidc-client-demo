package dasniko.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.security.auth.login.CredentialExpiredException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
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

		List<Map<String, Object>> keys = (List<Map<String, Object>>) app.getJsonWebKeySet().get("keys");
		Map<String, Object> key = keys.stream().filter(k -> k.get("kid").equals(kid)).findFirst().orElseThrow();

		// create public key for verification
		BigInteger n = new BigInteger(1, Base64.getUrlDecoder().decode((String) key.get("n")));
		BigInteger e = new BigInteger(1, Base64.getUrlDecoder().decode((String) key.get("e")));
		RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(n, e);
		PublicKey publicKey = KeyFactory.getInstance((String) key.get("kty")).generatePublic(rsaPublicKeySpec);

		// return token if valid
		JsonWebToken token = jwtParser.verify(tokenString, publicKey);
		if (token.getExpirationTime() < (System.currentTimeMillis() / 1000)) {
			throw new CredentialExpiredException("token is expired");
		}
		return token;
	}

	// this is only for demo purposes, don't do this at home!
	@SneakyThrows
	String getParsedPayload(String tokenString) {
		String[] parts = tokenString.split("\\.");
		String payloadString = new String(Base64.getDecoder().decode(parts[1]));
		Map<String, Object> payload = objectMapper.readValue(payloadString, new TypeReference<>() {});
		return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload);
	}

}
