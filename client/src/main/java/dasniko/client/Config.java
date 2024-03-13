package dasniko.client;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "idp")
public interface Config {
	String baseUrl();
	String clientId();
	String clientSecret();
}
