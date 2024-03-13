package dasniko.client;

import jakarta.ws.rs.FormParam;
import lombok.Data;

@Data
public class TokenForm {
	@FormParam("grant_type")
	private String grantType;
	@FormParam("client_id")
	private String clientId;
	@FormParam("client_secret")
	private String clientSecret;
	@FormParam("code")
	private String code;
	@FormParam("redirect_uri")
	private String redirectUri;
	@FormParam("refresh_token")
	private String refreshToken;
	@FormParam("code_verifier")
	private String codeVerifier;
}
