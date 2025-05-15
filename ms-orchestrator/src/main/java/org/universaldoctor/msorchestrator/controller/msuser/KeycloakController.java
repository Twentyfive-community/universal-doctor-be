package org.universaldoctor.msorchestrator.controller.msuser;

import dto.BaseController;
import org.apache.camel.ProducerTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import request.keycloak.AddMsUserReq;
import request.keycloak.TokenRequest;

@RestController
@RequestMapping("/keycloak")
public class KeycloakController extends BaseController {

    private final ProducerTemplate producerTemplate;

    public KeycloakController(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody TokenRequest tokenRequest){
        return ResponseEntity.ok(producerTemplate.requestBody("direct:login", tokenRequest, String.class));

    }
    @PostMapping("/register")
    public String register(@RequestBody AddMsUserReq addMsUserReq){
        return producerTemplate.requestBody("direct:register", addMsUserReq, String.class);
    }
}
