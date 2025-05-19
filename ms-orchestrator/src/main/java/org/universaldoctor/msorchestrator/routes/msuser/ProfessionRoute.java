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
public class ProfessionRoute extends RouteBuilder {

    @Value("${be.url}")
    private String dbUrl;

    private final String ENDPOINT = "profession";

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
                .handled(true);

        from("direct:save")
                .routeId("profession-save-route")
                .log(LoggingLevel.INFO,"Calling profession save with name = ${body}")
                .marshal().json()
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to(url+"/save")
                .log(LoggingLevel.INFO, "Profession saved successfully with name = ${body}");

        from("direct:getByName")
                .routeId("get-by-name-profession-route")
                .log(LoggingLevel.INFO,"Calling profession with name = ${body}")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .toD(url+"/get-by-name?name=${body}")
                .log(LoggingLevel.INFO, "Profession retrieved successfully");

        from("direct:getAll")
                .routeId("get-all-profession-route")
                .log(LoggingLevel.INFO,"Calling getAll profession with enabled = ${body}")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .toD(url+"/get-all?enabled=${body}")
                .log(LoggingLevel.INFO, "Professions retrieved successfully");

        from("direct:update")
                .routeId("update-profession-route")
                .log(LoggingLevel.INFO,"Calling update with this request = ${body}")
                .marshal().json()
                .setHeader(Exchange.HTTP_METHOD, constant("PUT"))
                .toD(url + "/update")
                .convertBodyTo(String.class);

        from("direct:toggleStatus")
                .routeId("toggle-profession-route")
                .log(LoggingLevel.INFO, "Calling toggle-status with this request = ${body}")
                .setHeader(Exchange.HTTP_METHOD, constant("PUT"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/x-www-form-urlencoded"))
                .removeHeader(Exchange.HTTP_PATH)
                .toD(url + "/toggle-status?professionName=${body}")
                .convertBodyTo(String.class);
    }
}
