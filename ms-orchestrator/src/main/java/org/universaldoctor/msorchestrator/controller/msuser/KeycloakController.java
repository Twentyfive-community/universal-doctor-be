package org.universaldoctor.msorchestrator.controller.msuser;

import dto.BaseController;
import org.apache.camel.ProducerTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import request.keycloak.*;
import response.ResponseWrapper;

@RestController
@RequestMapping("/keycloak")
public class KeycloakController extends BaseController {

    private final ProducerTemplate producerTemplate;

    public KeycloakController(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginMsUserReq loginMsUserReq){
        return ResponseEntity.ok(producerTemplate.requestBody("direct:login", loginMsUserReq, String.class));

    }

    @PostMapping("/refresh-token")
    public ResponseEntity<String> refreshToken(@RequestBody RefreshTokenReq refreshTokenReq){
        return ResponseEntity.ok(producerTemplate.requestBody("direct:refreshToken", refreshTokenReq, String.class));

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
    @PutMapping("/update")
    public ResponseEntity<ResponseWrapper<Void>> updateUser(@RequestBody UpdateMsUserReq msUser){
        producerTemplate.requestBody("direct:updateUser",msUser,String.class);
        return noContent("User Updated Succesfully");
    }
}
