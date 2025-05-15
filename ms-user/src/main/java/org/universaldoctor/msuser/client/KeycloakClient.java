package org.universaldoctor.msuser.client;

import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import request.keycloak.AddRealmRoleReq;
import request.keycloak.TokenRequest;

import java.util.LinkedHashMap;
import java.util.List;

@FeignClient(name = "KeycloakController", url = "${keycloak.url}")
public interface KeycloakClient {

    @PostMapping(value = "/realms/${realm}/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    Object getToken(@RequestBody TokenRequest params);

    @PostMapping(value = "/admin/realms/${realm}/users", produces = "application/json")
    ResponseEntity<Object> add(@RequestHeader("Authorization") String accessToken, @RequestBody UserRepresentation user);

    @PutMapping(value = "/admin/realms/${realm}/users/{id}")
    ResponseEntity<UserRepresentation> update(@RequestHeader("Authorization") String accessToken, @PathVariable("id") String id, @RequestBody UserRepresentation user);

    @PutMapping(value = "/admin/realms/${realm}/users/{userId}/execute-actions-email", produces = "application/json")
    ResponseEntity<Object> resetPassword(@RequestHeader("Authorization") String accessToken, @PathVariable("userId") String userId, @RequestBody List<String> actions);

    @GetMapping(value = "/admin/realms/${realm}/roles")
    List<LinkedHashMap<String, String>> getRoles(@RequestHeader("Authorization") String accessToken);

    @PostMapping(value = "/admin/realms/${realm}/users/{id}/role-mappings/realm", produces = "application/json")
    ResponseEntity<Object> addRoleToUser(@RequestHeader("Authorization") String accessToken, @PathVariable("id") String id, @RequestBody List<RoleRepresentation> roles);

    @DeleteMapping(value = "/admin/realms/${realm}/users/{id}/role-mappings/realm", produces = "application/json")
    ResponseEntity<Object> removeRoleFromUser(@RequestHeader("Authorization") String accessToken, @PathVariable("id") String id, @RequestBody List<RoleRepresentation> roles);

    @PostMapping(value = "/admin/realms/${realm}/roles", produces = "application/json")
    void addRealmRole(@RequestHeader("Authorization") String accessToken, @RequestBody AddRealmRoleReq request);

}