package org.universaldoctor.msorchestrator.routes.msuser;

import com.fasterxml.jackson.databind.ObjectMapper;
import exception.TokenRetrievalException;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
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
                .process(exchange -> {
                    HttpOperationFailedException cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, HttpOperationFailedException.class);
                    String body = cause.getResponseBody();

                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> map = mapper.readValue(body, Map.class);
                    String message = (String) map.get("message");
                    int code = (Integer) map.get("code");

                    if (code == 401) {
                        throw new TokenRetrievalException(message);
                    } else {
                        throw new RuntimeException("Generic backend error: " + message);
                    }
                })
                .log(LoggingLevel.ERROR, "Keycloak token retrieve failed")
                .handled(true);


        from("direct:login")
                .routeId("msuser-login")
                .log(LoggingLevel.INFO, "Keycloak token retrieving")
                .marshal().json()
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to(url+"/login")
                .log(LoggingLevel.INFO, "Keycloak token retrieved");

        from("direct:refreshToken")
                .routeId("refresh-token-route")
                .log(LoggingLevel.INFO,"Calling refresh token with this request = ${body}")
                .marshal().json()
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .toD(url + "/refresh-token")
                .convertBodyTo(String.class);

        from("direct:register")
                .routeId("msuser-register")
                .log(LoggingLevel.INFO, "Keycloak registering with ${body}")
                .marshal().json()
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to(url+"/register");

        from("direct:resetPassword")
                .routeId("reset-password-route")
                .log(LoggingLevel.INFO,"Calling reset-password with email=${body}")
                .setHeader(Exchange.HTTP_METHOD, constant("PUT"))
                .removeHeader(Exchange.HTTP_PATH)
                .toD(url + "/reset-password?email=${body}")
                .convertBodyTo(String.class);

        from("direct:updateUser")
                .routeId("update-user-route")
                .log(LoggingLevel.INFO,"Calling update with this request = ${body}")
                .marshal().json()
                .setHeader(Exchange.HTTP_METHOD, constant("PUT"))
                .toD(url + "/update")
                .convertBodyTo(String.class);

        from("direct:acceptDoctor")
                .routeId("accept-doctor-route")
                .log(LoggingLevel.INFO,"Calling accept doctor with this request = ${body}")
                .setHeader(Exchange.HTTP_METHOD, constant("PUT"))
                .toD(url + "/accept-doctor?email=${body}")
                .convertBodyTo(String.class);

    }
}
