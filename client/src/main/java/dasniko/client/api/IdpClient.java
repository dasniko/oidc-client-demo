package dasniko.client.api;

import dasniko.client.auth.ApplicationState;
import dasniko.client.model.TokenForm;
import dasniko.client.model.TokenResponse;
import dasniko.client.model.UserInfoResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

@ApplicationScoped
public class IdpClient {

	@Inject
	ApplicationState app;

	public TokenResponse getToken(@BeanParam TokenForm tokenForm) {
		// TODO implement me!
		return null;
	}

	public UserInfoResponse getUserInfo(String accessToken) {
		// TODO implement me!
		return null;
	}

	private WebTarget getEndpointFromConfig(String endpoint) {
		return ClientBuilder.newClient().target((String) app.getOpenidConfig().get(endpoint));
	}
}
