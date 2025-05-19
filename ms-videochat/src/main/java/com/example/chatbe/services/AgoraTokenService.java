package com.example.chatbe.services;

// src/main/java/com/example/agora/service/AgoraTokenService.java

import com.example.chatbe.config.AgoraConfig;
import com.example.chatbe.services.ChatTokenBuilder; // supponendo tu usi l'SDK ufficiale
import io.agora.media.AccessToken2;
import org.springframework.stereotype.Service;

@Service
public class AgoraTokenService {
    private final AgoraConfig config;
    private final ChatTokenBuilder tokenBuilder;

    public AgoraTokenService(AgoraConfig config) {
        this.config = config;
        // Inizializza il builder (dipende dalla tua libreria SDK)
        this.tokenBuilder = new ChatTokenBuilder();
    }

    public String buildAppToken(String appId, String appCertificate, int expire) throws Exception {
        if (appId == null || appCertificate == null) {
            throw new IllegalArgumentException("appId o appCertificate non possono essere nulli");
        }

        // Crea un nuovo token
        AccessToken2 token = new AccessToken2(appId, appCertificate, expire);

        // Definisce il servizio chat
        AccessToken2.ServiceChat serviceChat = new AccessToken2.ServiceChat();

        // Aggiungi il privilegio per l'app
        serviceChat.addPrivilegeChat(AccessToken2.PrivilegeChat.PRIVILEGE_CHAT_APP, expire);

        // Aggiungi il servizio al token
        token.addService(serviceChat);

        // Costruisci il token
        String appToken = token.build();


        return appToken;
    }


    /** Genera un User Token valido ttl secondi per userId */
    public String buildUserToken(String userId, int ttl) {
        return tokenBuilder.buildUserToken(
                config.getAppId(),
                config.getAppCertificate(),
                userId,
                ttl
        );
    }
}

