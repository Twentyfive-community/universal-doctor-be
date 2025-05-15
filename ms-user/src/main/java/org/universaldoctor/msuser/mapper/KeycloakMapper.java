package org.universaldoctor.msuser.mapper;

import dto.KeycloakUser;
import model.MsUser;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
public class KeycloakMapper {

    private KeycloakMapper(){}

    public static KeycloakUser createOrUpdateMsUserToRealm(MsUser msUser) {
        KeycloakUser user = new KeycloakUser();
        user.setEmail(msUser.getEmail());
        user.setFirstName(msUser.getFirstName());
        user.setLastName(msUser.getLastName());
        user.setEnabled(true);
        return user;
    }

    public static String getKeycloakIdFromResponse(ResponseEntity<Object> response) {
        String[] stringArray = response.getHeaders().get("location").get(0).split("/");
        return stringArray[stringArray.length - 1];
    }

    public static RoleRepresentation convertToRoleRepresentation(LinkedHashMap<String, String> rawRole) {
        RoleRepresentation role = new RoleRepresentation();
        role.setName(rawRole.get("name"));
        role.setId(rawRole.get("id"));
        return role;
    }
}
