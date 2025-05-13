package org.universaldoctor.msorchestrator.routes.msuser;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeycloakRoute extends RouteBuilder {
    @Value("${be.url}")
    private String dbUrl;

    private final String ENDPOINT = "keycloak";

    @Override
    public void configure() throws Exception {
        String url = dbUrl + ENDPOINT;

        from("direct:login")
                .marshal().json()
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to(url+"/login");

        from("direct:register")
                .marshal().json()
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to(url+"/register");

    }
}
