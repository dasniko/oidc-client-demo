package dasniko.resourceserver;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Slf4j
@ApplicationScoped
public class JwtService {

	JsonWebToken verify(String tokenString) throws Exception {
		throw new IllegalStateException("Not implemented yet");
	}

}
