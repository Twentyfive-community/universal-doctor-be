package org.universaldoctor.msuser.controller;

import dto.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.universaldoctor.msuser.service.KeycloakService;
import request.keycloak.AddMsUserReq;
import request.keycloak.TokenRequest;
import response.ResponseWrapper;

@RestController
@RequestMapping("/keycloak")
public class KeycloakController extends BaseController {

    private final KeycloakService keycloakService;

    public KeycloakController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<String>> login(@RequestBody TokenRequest tokenRequest) {
        return ok(keycloakService.getToken(tokenRequest), "Login successful");
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseWrapper<Void>> addUser(@RequestBody AddMsUserReq msUser) {
        keycloakService.addMsUser(msUser);
        return created("User Created Succesfully");
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ResponseWrapper<Void>> resetPasswordFromEmail(@RequestParam("email") String email) {
        keycloakService.resetPasswordFromEmail(email);
        return noContent("Email Reset Password Sent!");
    }

}
