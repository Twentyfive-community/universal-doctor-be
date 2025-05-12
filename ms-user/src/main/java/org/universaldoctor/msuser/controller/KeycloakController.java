package org.universaldoctor.msuser.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.universaldoctor.msuser.service.KeycloakService;
import request.keycloak.TokenRequest;


@RestController
@RequestMapping("/keycloak")
public class KeycloakController {
    @Autowired
    private KeycloakService keycloakService;

    @PostMapping("/login")
    public String login(@RequestBody TokenRequest tokenRequest) {
        return keycloakService.getToken(tokenRequest);
    }
}
