package org.universaldoctor.msorchestrator.controller.msuser;

import dto.BaseController;
import org.apache.camel.ProducerTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import request.keycloak.AddMsUserReq;
import request.keycloak.TokenRequest;
import response.ResponseWrapper;

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
    @PutMapping("/reset-password")
    public ResponseEntity<ResponseWrapper<Void>> resetPassword(@RequestParam("email") String email){
        producerTemplate.requestBody("direct:resetPassword",email,String.class);
        return noContent("email reset password sent successfully");
    }
}
