package org.universaldoctor.msorchestrator.routes.msuser;

import com.fasterxml.jackson.databind.ObjectMapper;
import exception.TokenRetrievalException;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KeycloakRoute extends RouteBuilder {
    @Value("${be.url}")
    private String dbUrl;

    private final String ENDPOINT = "keycloak";

    @Override
    public void configure() throws Exception {
        String url = dbUrl + ENDPOINT;

        onException(HttpOperationFailedException.class)
                .handled(true)
                .process(exchange -> {
                    HttpOperationFailedException ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, HttpOperationFailedException.class);
                    String response = ex.getResponseBody();
                    String message;
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        Map<String, Object> body = mapper.readValue(response, Map.class);
                        message = (String) body.getOrDefault("message", "Errore autenticazione");
                    } catch (Exception parseEx) {
                        message = "Errore backend: risposta non leggibile";
                    }
                    throw new TokenRetrievalException(message);
                });

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
