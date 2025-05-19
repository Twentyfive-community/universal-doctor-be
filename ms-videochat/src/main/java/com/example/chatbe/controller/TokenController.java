package com.example.chatbe.controller;

import com.example.chatbe.config.AgoraConfig;
import io.agora.chat.ChatTokenBuilder2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


// src/main/java/com/example/agora/controller/TokenController.java

import com.example.chatbe.services.AgoraTokenService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/token")
public class TokenController {
    private final AgoraTokenService tokenService;
    private final AgoraConfig config;

    public TokenController(AgoraTokenService tokenService, AgoraConfig config) {
        this.tokenService = tokenService;
        this.config = config;
    }

    @PostMapping(
            value = "/generate",
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public String generateToken(
            @RequestParam String userId,
            @RequestParam int expireSeconds,
            @RequestParam(defaultValue = "app") String tokenType
    ) throws Exception {
        if ("app".equalsIgnoreCase(tokenType)) {
            return tokenService.buildAppToken(config.getAppId(), config.getAppCertificate(),3600);
        } else if ("user".equalsIgnoreCase(tokenType)) {
            return tokenService.buildUserToken(userId, expireSeconds);
        } else {
            throw new IllegalArgumentException("Invalid tokenType. Must be 'user' or 'app'.");
        }
    }
}

