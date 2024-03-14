package dasniko.client;

import jakarta.enterprise.context.SessionScoped;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@SessionScoped
public class SessionState implements Serializable {
	private Identity identity;
	private String idToken;
	private String accessToken;
	private String refreshToken;
	private String codeVerifier;
	private String codeChallenge;
}
