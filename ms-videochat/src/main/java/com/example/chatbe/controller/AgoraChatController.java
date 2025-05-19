package com.example.chatbe.controller;

import com.example.chatbe.config.AgoraConfig;
import com.example.chatbe.services.AgoraTokenService;
import io.agora.chat.ChatTokenBuilder2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Collections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/chat")
public class AgoraChatController {

    @Value("${agora.appid}")
    private String appId;
    @Value("${agora.appCertificate}")
    private String appCert;
    @Value("${agora.appKey}")  // in formato OrgName#AppName
    private String appKey;
    @Value("${agora.domain}")  // REST API domain, es. a41.chat.agora.io
    private String domain;
    @Value("${agora.groupId}")
    private String groupId;      // e.g., "group_chat_room_1"

    private final RestTemplate restTemplate = new RestTemplate();


    @PostMapping("/register-login")
    public ResponseEntity<?> registerAndLogin(@RequestBody Map<String,String> body) {


        String userId = body.get("userId");
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body("userId mancante");
        }
        try {
            // 1. Ottieni o registra l'utente e ricava il suo UUID interno
            String chatUserUuid = getUserUuid(userId);
            if (chatUserUuid == null) {
                chatUserUuid = registerUser(userId);
            }
            // 2. Genera token utente con scadenza 3600 sec (1h)
            ChatTokenBuilder2 builder = new ChatTokenBuilder2();
            String userToken = builder.buildUserToken(appId, appCert, chatUserUuid, 3600);
            // 3. Aggiungi l'utente al gruppo specificato
            //addUserToGroup(userId);
            // 4. Rispondi con token e gruppo
            Map<String,String> response = new HashMap<>();
            response.put("token", userToken);
            response.put("groupId", groupId);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore: " + ex.getMessage());
        }
    }

    // Recupera l'UUID interno di Agora per un dato username (se esiste)
    private String getUserUuid(String userId) {
        String[] parts = appKey.split("#");
        String url = "https://" + domain + "/" + parts[0] + "/" + parts[1] + "/users/" + userId;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("007eJxTYLDarSlod+z59Y5uw8uZbusjFvoWSMc+zTXlenTw3cxXwnoKDAZGKQYGyUYmyWkmBiampmaWJkapyaZGRhYplmkWKckmd7YrZjQEMjLIr1zBzMjAysDIwMQA4jMwAABxRh1V");  // App token per autorizzazione
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            List<Map<String,Object>> entities = (List<Map<String,Object>>) resp.getBody().get("entities");
            if (entities != null && !entities.isEmpty()) {
                return (String) entities.get(0).get("uuid");
            }
        } catch (Exception e) {
            // 404 Not Found se l'utente non esiste
        }
        return null;
    }

    // Registra un nuovo utente con password fissa "123" e restituisce il suo UUID interno
    private String registerUser(String userId) {
        String[] parts = appKey.split("#");
        String url = "https://" + domain + "/" + parts[0] + "/" + parts[1] + "/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getAppAccessToken());
        Map<String,String> body = new HashMap<>();
        body.put("username", userId);
        body.put("password", "123");
        HttpEntity<Map<String,String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        List<Map<String,Object>> entities = (List<Map<String,Object>>) resp.getBody().get("entities");
        return (String) entities.get(0).get("uuid");
    }

    // Metodo separato per eliminare l'utente se è temporaneo (user_12345)
    @PostMapping("/delete-user")
    private void deleteIfTemporaryUser(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        if (userId != null && userId.matches("^user_\\d+$")) {
            String[] parts = appKey.split("#");
            String deleteUrl = "https://" + domain + "/" + parts[0] + "/" + parts[1] + "/users/" + userId;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(getAppAccessToken());

            restTemplate.exchange(deleteUrl, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
            System.out.println("Utente temporaneo " + userId + " eliminato.");
        }
    }


    private String getAppAccessToken() {
        ChatTokenBuilder2 builder = new ChatTokenBuilder2();

        // 1. Genera App Token (valido per 10 minuti, ad esempio)
        String appToken = builder.buildAppToken(appId, appCert, 600);

        // 2. Costruzione dell'URL REST di Agora (usa il dominio corretto)
        String[] parts = appKey.split("#");  // ["711340750", "1545482"]
        String url = "https://a71.chat.agora.io/" + parts[0] + "/" + parts[1] + "/token";

        // 3. Headers e body
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(appToken);  // ⚠️ Usa appToken qui
        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "agora");

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        // 4. Fai POST e ottieni access_token
        ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        return (String) resp.getBody().get("access_token");  // Questo è l'App Access Token da usare
    }


    // Aggiungi l'utente al gruppo esistente (utilizza l'App Access Token)
    private void addUserToGroup(String userId) {
        String[] parts = appKey.split("#");
        String url = "https://" + domain + "/" + parts[0] + "/" + parts[1] +
                "/chatgroups/" + groupId + "/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getAppAccessToken());
        Map<String,Object> body = new HashMap<>();
        body.put("usernames", Collections.singletonList(userId));
        HttpEntity<Map<String,Object>> entity = new HttpEntity<>(body, headers);

        try {
            // Aggiungi un log per vedere la risposta completa
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            System.out.println("Response: " + response.getBody());
        } catch (Exception e) {
            System.out.println("Errore durante l'aggiunta al gruppo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
