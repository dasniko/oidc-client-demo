package dasniko.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Data
public class Identity {
	private String sub;
	private String name;
	@JsonProperty("given_name")
	private String givenName;
	@JsonProperty("family_name")
	private String familyName;
	@JsonProperty("preferred_username")
	private String preferredUsername;
	private String email;
	@JsonProperty("email_verified")
	private boolean emailVerified;

	static Identity fromIdToken(JsonWebToken idToken) {
		Identity identity = new Identity();
		identity.setSub(idToken.getSubject());
		identity.setPreferredUsername(idToken.getClaim("preferred_username"));
		identity.setName(idToken.getClaim("name"));
		identity.setGivenName(idToken.getClaim("given_name"));
		identity.setFamilyName(idToken.getClaim("family_name"));
		identity.setEmail(idToken.getClaim("email"));
		identity.setEmailVerified(idToken.getClaim("email_verified"));
		return identity;
	}

	void addUserinfo(Identity userinfo) {
		this.setSub(userinfo.getSub());
		this.setName(userinfo.getName());
		this.setGivenName(userinfo.getGivenName());
		this.setFamilyName(userinfo.getFamilyName());
		this.setPreferredUsername(userinfo.getPreferredUsername());
		this.setEmail(userinfo.getEmail());
		this.setEmailVerified(userinfo.isEmailVerified());
	}
}
