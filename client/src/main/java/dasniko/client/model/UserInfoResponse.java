package dasniko.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record UserInfoResponse(
	String sub,
	String name,
	@JsonProperty("given_name") String givenName,
	@JsonProperty("family_name") String familyName,
	@JsonProperty("preferred_username") String preferredUsername,
	String email,
	@JsonProperty("email_verified") boolean emailVerified,
	List<String> groups
) { }
