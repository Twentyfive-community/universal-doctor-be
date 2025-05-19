package com.example.chatbe.controller;

// src/main/java/com/example/chatbe/controller/VideoTokenController.java

import io.agora.media.AccessToken2;
import io.agora.media.AccessToken2.ServiceRtc;
import io.agora.media.AccessToken2.PrivilegeRtc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/rtc-token")
@CrossOrigin(origins = "*")
public class VideoTokenController {

    @Value("${agora.appid}")
    private String appId;

    @Value("${agora.appCertificate}")
    private String appCertificate;

    /**
     * Restituisce un token RTC Agora per un dato canale e utente
     * @param channelName Nome del canale (es: callId)
     * @param userId Identificativo univoco dell'utente
     * @return Token RTC Agora firmato
     */
    @GetMapping
    public ResponseEntity<Map<String, String>> getRtcToken(
            @RequestParam String channelName,
            @RequestParam String userId) throws Exception {

        int expireSeconds = 3600;
        int currentTs = (int) Instant.now().getEpochSecond();
        int expireTs = currentTs + expireSeconds;

        // Inizializza token
        AccessToken2 token = new AccessToken2(appId, appCertificate, expireTs);

        // Aggiungi permessi RTC
        ServiceRtc serviceRtc = new ServiceRtc(channelName, userId);
        serviceRtc.addPrivilegeRtc(PrivilegeRtc.PRIVILEGE_JOIN_CHANNEL, expireTs);
        serviceRtc.addPrivilegeRtc(PrivilegeRtc.PRIVILEGE_PUBLISH_AUDIO_STREAM, expireTs);
        serviceRtc.addPrivilegeRtc(PrivilegeRtc.PRIVILEGE_PUBLISH_VIDEO_STREAM, expireTs);

        token.addService(serviceRtc);

        // Costruisci e restituisci il token
        String rtcToken = token.build();
        return ResponseEntity.ok(Map.of("token", rtcToken));
    }
}

