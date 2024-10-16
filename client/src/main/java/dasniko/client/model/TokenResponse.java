package dasniko.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
	@JsonProperty("access_token") String accessToken,
	@JsonProperty("id_token") String idToken,
	@JsonProperty("refresh_token") String refreshToken
) { }
