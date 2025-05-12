package org.universaldoctor.msorchestrator.routes.msuser;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProfessionRoute extends RouteBuilder {

    @Value("${be.url}")
    private String dbUrl;

    private final String ENDPOINT = "profession";

    @Override
    public void configure() throws Exception {
        String url = dbUrl + ENDPOINT+"/add";
        from("direct:add")
                .marshal().json()
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to(url);
    }
}
