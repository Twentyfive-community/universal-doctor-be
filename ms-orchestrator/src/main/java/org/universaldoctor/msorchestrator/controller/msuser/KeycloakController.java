package org.universaldoctor.msorchestrator.controller.msuser;

import dto.BaseController;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import request.keycloak.AddMsUserReq;
import request.keycloak.TokenRequest;
import response.ResponseWrapper;

@RestController
@RequestMapping("/keycloak")
public class KeycloakController extends BaseController {
    @Autowired
    private ProducerTemplate producerTemplate;

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<String>> login(@RequestBody TokenRequest tokenRequest){
        return ok(producerTemplate.requestBody("direct:login", tokenRequest, String.class),"User authenticated correctly");

    }
    @PostMapping("/register")
    public String register(@RequestBody AddMsUserReq addMsUserReq){
        return producerTemplate.requestBody("direct:register", addMsUserReq, String.class);
    }
}
