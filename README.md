# OIDC Client Demo

Demo repository for implementing an OIDC client from scratch.

For development purposes during @dasniko's Training [OAuth2 & OIDC for Java developers](https://www.n-k.de/trainings/oauth21-und-oidc-fuer-entwickler/) only.

Please prepare yourself upfront of the training with the following:

## Technical Prerequisites

* Notebook/Laptop
* Java-IDE of your choice _(e.g. IntelliJ IDEA, Eclipse, VS Code, Netbeans, ...)_
* JDK 17+
* Access to internet (possibly check proxy/firewall/VPN configurations)
* Browser

## Preparations

* _Clone_ this repository to your local machine or download it [here](https://github.com/dasniko/oidc-client-demo/archive/refs/heads/main.zip).
* Pull the official Keycloak image `quay.io/keycloak/keycloak:24.0`

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

## Links

* OAuth 2.1: https://oauth.net/2.1/
* BFF:
 * https://miro.medium.com/v2/resize:fit:4800/format:webp/1*2BaGJecjJNBk0gGCUQJO2w.png
 * https://www.baeldung.com/spring-cloud-gateway-bff-oauth2
 * https://auth0.com/blog/backend-for-frontend-pattern-with-auth0-and-dotnet/
* OAuth2, OIDC & JWT Basics slides:
	https://speakerdeck.com/dasniko/oauth2-oidc-and-jwt-important-basics

## Your trainer

* Niko's YouTube Channel:
	https://www.youtube.com/@dasniko
* Niko's GitHub Profile:
	https://github.com/dasniko
* Keycloak Extensions Demos:
	https://github.com/dasniko/keycloak-extensions-demo
* Testcontainer-Keycloak Project:
	https://github.com/dasniko/testcontainers-keycloak
* Moderator @ Keycloak Discourse Forum:
	https://keycloak.discourse.group/
