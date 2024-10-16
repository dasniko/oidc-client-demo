package dasniko.client.model;

import lombok.Data;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Data
public class Identity {
	private String id;
	private String name;
	private String firstName;
	private String lastName;
	private String username;
	private String email;
	private boolean emailVerified;
	private List<String> groups;

	public static Identity fromIdToken(JsonWebToken idToken) {
		Identity identity = new Identity();
		identity.setId(idToken.getSubject());
		identity.setUsername(idToken.getClaim("preferred_username"));
		identity.setName(idToken.getClaim("name"));
		identity.setFirstName(idToken.getClaim("given_name"));
		identity.setLastName(idToken.getClaim("family_name"));
		identity.setEmail(idToken.getClaim("email"));
		identity.setEmailVerified(idToken.getClaim("email_verified"));
		return identity;
	}

	public void addUserinfo(UserInfoResponse userinfo) {
		this.setId(userinfo.sub());
		this.setName(userinfo.name());
		this.setFirstName(userinfo.givenName());
		this.setLastName(userinfo.familyName());
		this.setUsername(userinfo.preferredUsername());
		this.setEmail(userinfo.email());
		this.setEmailVerified(userinfo.emailVerified());
		this.setGroups(userinfo.groups());
	}
}
