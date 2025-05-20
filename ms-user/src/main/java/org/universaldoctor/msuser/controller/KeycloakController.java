package org.universaldoctor.msuser.controller;

import dto.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.universaldoctor.msuser.service.KeycloakService;
import request.keycloak.*;
import response.ResponseWrapper;
import response.keycloak.LoginRes;


@RestController
@RequestMapping("/keycloak")
public class KeycloakController extends BaseController {
    @Autowired
    private KeycloakService keycloakService;


    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<LoginRes>> login(@RequestBody LoginMsUserReq loginMsUserReq) {
        return ok(keycloakService.getToken(loginMsUserReq), "Login successful");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseWrapper<LoginRes>> refreshToken(@RequestBody RefreshTokenReq refreshTokenReq) {
        return ok(keycloakService.refreshToken(refreshTokenReq), "Refresh token successful");
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseWrapper<Void>> addUser(@RequestBody AddMsUserReq msUser) {
        keycloakService.addMsUser(msUser);
        return created("User Created Succesfully");
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ResponseWrapper<Void>> resetPasswordFromEmail(@RequestParam("email") String email){
        keycloakService.resetPasswordFromEmail(email);
        return noContent("Email Reset Password Sent!");
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseWrapper<Void>> updateUser(@RequestBody UpdateMsUserReq msUser){
        keycloakService.updateUser(msUser);
        return noContent("User Updated Succesfully");
    }

    @PutMapping("/accept-doctor")
    public ResponseEntity<ResponseWrapper<Void>> acceptDoctor(@RequestParam("email") String email){
        keycloakService.acceptDoctor(email);
        return noContent("Doctor Accepted Succesfully");
    }

}
