package org.universaldoctor.msuser.controller;

import dto.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.universaldoctor.msuser.service.KeycloakService;
import request.keycloak.AddMsUserReq;
import request.keycloak.TokenRequest;
import response.ResponseWrapper;


@RestController
@RequestMapping("/keycloak")
public class KeycloakController extends BaseController {
    @Autowired
    private KeycloakService keycloakService;

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<String>> login(@RequestBody TokenRequest tokenRequest) {
        return ok(keycloakService.getToken(tokenRequest), "Login successful");

    }

    @PostMapping("/register")
    public ResponseEntity<ResponseWrapper<Boolean>> addUser(@RequestBody AddMsUserReq msUser) {
        return ok(keycloakService.addMsUser(msUser), "Register successful");
    }


}
