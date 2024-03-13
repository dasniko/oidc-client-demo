# OIDC Client Demo

Demo repository for implementing an OIDC client from scratch.

For development purposes during @dasniko's Training [OAuth2 & OIDC for Java developers](https://www.n-k.de/trainings/oauth21-und-oidc-fuer-entwickler/) only.

Please prepare yourself upfront of the training with the following:

## Technical Prerequisites

* Notebook/Laptop
* Java-IDE of your choice _(e.g. IntelliJ IDEA, Eclipse, VS Code, Netbeans, ...)_
* JDK 17+
* Docker and Docker Compose V2 installed and ready (possibly check for local admin rights)
* Access to internet (possibly check proxy/firewall/VPN configurations)
* separate HTTP-Client, if your IDE doesn't come with an embedded one _(e.g. Insomnia, Postman, REST-Client for VS Code Plugin, etc.)_
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
