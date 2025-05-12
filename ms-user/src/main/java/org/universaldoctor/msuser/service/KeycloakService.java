package org.universaldoctor.msuser.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.universaldoctor.msuser.client.KeycloakClient;
import request.keycloak.TokenRequest;

import java.util.Map;

@Service
public class KeycloakService {
    @Autowired
    private KeycloakClient keycloakClient;

    @Value("${keycloak.clientId}")
    protected String clientId;
    @Value("${keycloak.credentials.secret}")
    protected String clientSecret;
    @Value("${keycloak.username}")
    protected String username;
    @Value("${keycloak.password}")
    protected String password;
    @Value("${keycloak.password}")
    protected String grantType;

    public String getBearerToken() {
        TokenRequest tokenRequest = new TokenRequest(clientId, clientSecret, grantType, username, password);
        return "Bearer " + getToken(tokenRequest);
    }

    public String getToken(TokenRequest tokenRequest) {
        Object response = keycloakClient.getToken(tokenRequest);

        ObjectMapper objectMapper = new ObjectMapper();
        Map responseMap = objectMapper.convertValue(response, Map.class);
        return (String) responseMap.get("access_token");
    }


}
