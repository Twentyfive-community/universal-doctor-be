package org.universaldoctor.msorchestrator.controller.msuser;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import request.keycloak.TokenRequest;

@RestController
@RequestMapping("/keycloak")
public class KeycloakController {
    @Autowired
    private ProducerTemplate producerTemplate;

    @PostMapping("/login")
    public String login(@RequestBody TokenRequest tokenRequest){
        return producerTemplate.requestBody("direct:login", tokenRequest, String.class);

    }
}
