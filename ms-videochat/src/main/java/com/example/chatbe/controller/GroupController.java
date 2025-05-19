package com.example.chatbe.controller;

import com.example.chatbe.config.AgoraConfig;
import com.example.chatbe.services.AgoraTokenService;

import io.agora.chat.ChatTokenBuilder2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/group")
public class GroupController {

    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    private final AgoraConfig config;
    private final AgoraTokenService tokenService;
    private final WebClient webClient;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${agora.domain}")  // REST API domain, es. a41.chat.agora.io
    private String domain;

    @Value("${agora.appid}")
    private String appId;

    @Value("${agora.appCertificate}")
    private String appCert;

    @Value("${agora.appKey}") // es: "711340750#1545482"
    private String appKey;

    public GroupController(AgoraConfig config,
                           AgoraTokenService tokenService,
                           WebClient.Builder webClientBuilder) {
        this.config = config;
        this.tokenService = tokenService;
        this.webClient = webClientBuilder
                .baseUrl("https://a71.chat.agora.io/v3")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }



    private final Map<String, String> callIdToGroupId = new ConcurrentHashMap<>();

    @PostMapping("/create")
    public Mono<ResponseEntity<Map<String, Object>>> createOrJoinGroup(@RequestBody CreateGroupRequest req) {
        logger.info("Received request for callId: {}", req.getCallId());

        // 1. Verifica se esiste già un gruppo associato a questa chiamata
        if (callIdToGroupId.containsKey(req.getCallId())) {
            String existingGroupId = callIdToGroupId.get(req.getCallId());
            logger.info("Gruppo esistente trovato per callId {}: {}", req.getCallId(), existingGroupId);
            return Mono.just(ResponseEntity.ok(Map.of("groupId", existingGroupId)));
        }

        // 2. Altrimenti, creane uno nuovo come prima
        return createGroup(req).flatMap(response -> {
            // Verifica che la risposta sia di successo e contenga il body
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String newGroupId = (String) response.getBody().get("groupId");
                callIdToGroupId.put(req.getCallId(), newGroupId); // Salva il mapping
            }
            return Mono.just(response);
        });
    }

     /*

    @PostMapping("/create")
    public Mono<ResponseEntity<Map<String, Object>>> createOrJoinGroup(@RequestBody CreateGroupRequest req) {
        String callId = req.getCallId();
        logger.info("Received request for callId: {}", callId);

        return checkIfGroupExists(callId).flatMap(exists -> {
            if (exists) {
                logger.info("Gruppo esistente trovato per callId {}", callId);
                return Mono.just(ResponseEntity.ok(Map.of("groupId", callId)));
            } else {
                return createGroup(req).flatMap(response -> {
                    if (response.getStatusCode().is2xxSuccessful()) {
                        logger.info("Creato nuovo gruppo con callId come nome: {}", callId);
                    }
                    return Mono.just(response);
                });
            }
        });
    }


    private Mono<Boolean> checkIfGroupExists(String groupName) {
        // Implementa la chiamata GET al tuo backend/chat service
        String url = String.format("https://api.agora.io/groups/%s", groupName);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(GroupInfo.class)
                .map(group -> true)
                .onErrorResume(e -> Mono.just(false)); // se 404, il gruppo non esiste
    }

      */




    @DeleteMapping("/delete-group")
    public ResponseEntity<String> deleteGroup(@RequestBody Map<String, String> body) {
        String groupName = body.get("groupName");
        if (groupName == null || groupName.isBlank()) {
            return ResponseEntity.badRequest().body("Missing or empty groupName");
        }

        String[] parts = appKey.split("#");
        String deleteUrl = "https://" + domain + "/" + parts[0] + "/" + parts[1] + "/chatgroups/" + groupName;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAppAccessToken());

        try {
            restTemplate.exchange(deleteUrl, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
            logger.info("Gruppo {} eliminato con successo.", groupName);
            return ResponseEntity.ok("Group deleted: " + groupName);
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Il gruppo {} non esiste o è già stato eliminato.", groupName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found: " + groupName);
        } catch (Exception e) {
            logger.error("Errore durante l'eliminazione del gruppo {}: {}", groupName, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'eliminazione del gruppo: " + e.getMessage());
        }
    }


    public Mono<ResponseEntity<Map<String, Object>>> createGroup(@RequestBody CreateGroupRequest req) {
        logger.info("Received createGroup request: {}", req);

        // Step 1: Ottieni access token (REST)
        String accessToken = getAppAccessToken();

        // Step 2: Costruisci il body della richiesta
        Map<String, Object> body = Map.of(
                "groupname", req.getName(),
                "desc", req.getDescription(),
                "maxusers", req.getMaxUsers(),
                "public", true,
                "membersonly", false,
                "allowinvites", true,
                "owner", req.getOwner()
        );

        // Step 3: Costruisci URL per /chatgroups
        String[] parts = appKey.split("#");
        String orgName = parts[0];
        String appName = parts[1];
        String url = String.format("https://%s/%s/%s/chatgroups", config.getDomain(), orgName, appName);

        // Step 4: Costruisci header con Bearer token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        // Step 5: Invia POST con RestTemplate
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        return Mono.fromCallable(() -> {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            // Step 6: Analizza risposta
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Object dataObj = responseBody.get("data");

                if (dataObj instanceof Map<?, ?> data && data.containsKey("groupid")) {
                    String groupId = (String) data.get("groupid");
                    logger.info("Group created with ID: {}", groupId);
                    return ResponseEntity.ok(Map.of("groupId", groupId));
                } else {
                    logger.error("GroupId non trovato nella risposta: {}", responseBody);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", "groupId non trovato nella risposta"));
                }
            } else {
                logger.error("Errore durante la creazione del gruppo: {} - {}", response.getStatusCode(), response.getBody());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Errore creazione gruppo", "details", response.getBody()));
            }
        });
    }




    /** Ottiene access_token da Agora REST API usando appToken JWT firmato */
    private String getAppAccessToken() {
        try {
            ChatTokenBuilder2 builder = new ChatTokenBuilder2();
            String appToken = builder.buildAppToken(appId, appCert, 600);  // validità 10 min

            String[] parts = appKey.split("#");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid appKey format, expected 'appid#appname'");
            }
            String orgName = parts[0];
            String appName = parts[1];
            String url = String.format("https://a71.chat.agora.io/%s/%s/token", orgName, appName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(appToken);
            Map<String, String> body = Map.of("grant_type", "agora");

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object tokenObj = response.getBody().get("access_token");
                if (tokenObj instanceof String tokenStr) {
                    logger.info("Obtained Agora REST access_token");
                    return tokenStr;
                } else {
                    throw new IllegalStateException("access_token missing or not a string");
                }
            } else {
                throw new IllegalStateException("Failed to retrieve Agora access_token: " + response);
            }
        } catch (Exception ex) {
            logger.error("Errore durante l'ottenimento del REST access_token da Agora", ex);
            throw new RuntimeException("Errore nella generazione del token", ex);
        }
    }

    private Mono<ResponseEntity<Map<String, Object>>> handleResponse(ClientResponse response, String fallbackName) {
        HttpStatusCode status = response.statusCode();

        if (status.is2xxSuccessful()) {
            return response.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .map(body -> {
                        logger.info("Agora API response body: {}", body);
                        if (body == null || body.isEmpty()) {
                            logger.error("La risposta di Agora è vuota!");
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body(Map.of("error", "La risposta di Agora è vuota"));
                        }

                        String groupId = fallbackName;

                        Object dataObj = body.get("data");
                        if (dataObj instanceof Map) {
                            Map<?, ?> dataMap = (Map<?, ?>) dataObj;
                            if (dataMap.containsKey("groupid")) {
                                Object id = dataMap.get("groupid");
                                if (id instanceof String) {
                                    groupId = (String) id;
                                }
                            }
                        }

                        if (groupId == null || groupId.isEmpty()) {
                            logger.error("groupId non trovato nella risposta di Agora.");
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body(Map.of("error", "groupId non trovato nella risposta"));
                        }

                        logger.info("Group created with ID: {}", groupId);
                        return ResponseEntity.ok(Map.of("groupId", groupId));
                    });
        } else {
            return response.bodyToMono(String.class)
                    .defaultIfEmpty("")
                    .flatMap(errorBody -> {
                        logger.error("Agora API error: status {} body {}", status.value(), errorBody);
                        if (status.value() == HttpStatus.CONFLICT.value()) {
                            return Mono.just(ResponseEntity.ok(Map.of("groupId", fallbackName)));
                        }
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", "Failed to create group", "details", errorBody)));
                    });
        }
    }

    public static class CreateGroupRequest {
        private String callId;
        private String name;
        private String description;
        private Integer maxUsers;
        private String owner;

        public String getCallId() {return callId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public void setCallId(String callId){this.callId=callId;}
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getMaxUsers() { return maxUsers; }
        public void setMaxUsers(Integer maxUsers) { this.maxUsers = maxUsers; }
        public String getOwner() { return owner; }
        public void setOwner(String owner) { this.owner = owner; }

        @Override
        public String toString() {
            return String.format("CreateGroupRequest{name='%s', description='%s', maxUsers=%d, owner='%s'}",
                    name, description, maxUsers, owner);
        }
    }

    public class GroupInfo {
        private String groupId;
        private String name;
        private String owner;
        private List<String> members;

        // Getters e Setters
        public String getGroupId() { return groupId; }
        public void setGroupId(String groupId) { this.groupId = groupId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getOwner() { return owner; }
        public void setOwner(String owner) { this.owner = owner; }

        public List<String> getMembers() { return members; }
        public void setMembers(List<String> members) { this.members = members; }
    }

}




