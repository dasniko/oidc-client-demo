package dasniko.client.model;

import jakarta.ws.rs.core.Form;
import lombok.Data;

@Data
public class TokenForm {
	private String grantType;
	private String clientId;
	private String clientSecret;
	private String code;
	private String redirectUri;
	private String refreshToken;
	private String codeVerifier;

	public Form getFormEntity() {
		return new Form()
			.param("grant_type", grantType)
			.param("client_id", clientId)
			.param("client_secret", clientSecret)
			.param("code", code)
			.param("redirect_uri", redirectUri)
			.param("refresh_token", refreshToken)
			.param("code_verifier", codeVerifier);
	}
}
