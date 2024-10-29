package dasniko.client.api;

import dasniko.client.auth.ApplicationState;
import dasniko.client.model.TokenForm;
import dasniko.client.model.TokenResponse;
import dasniko.client.model.UserInfoResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;

@ApplicationScoped
public class IdpClient {

	@Inject
	ApplicationState app;

	public TokenResponse getToken(@BeanParam TokenForm tokenForm) {
		return getResponseFromEndpoint("token_endpoint", tokenForm.getFormEntity(), TokenResponse.class);
	}

	public UserInfoResponse getUserInfo(String accessToken) {
		return getResponseFromEndpoint("userinfo_endpoint", new Form("access_token", accessToken), UserInfoResponse.class);
	}

	private <T> T getResponseFromEndpoint(String endpoint, Form form, Class<T> targetClass) {
		return ClientBuilder.newClient().target((String) app.getOpenidConfig().get(endpoint))
			.request(MediaType.APPLICATION_JSON)
			.post(Entity.form(form))
			.readEntity(targetClass);
	}
}
