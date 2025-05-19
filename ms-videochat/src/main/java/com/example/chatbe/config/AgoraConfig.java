package com.example.chatbe.config;

// src/main/java/com/example/agora/config/AgoraConfig.java

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgoraConfig {
    @Value("${agora.appId}")
    private String appId;

    @Value("${agora.domain}")  // REST API domain, es. a41.chat.agora.io
    private String appDomain;

    @Value("${agora.appCertificate}")
    private String appCertificate;

    @Value("${agora.appKey}")
    private String appKey;

    public String getAppId() {
        return appId;
    }

    public String getAppCertificate() {
        return appCertificate;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getDomain() {
        return appDomain;
    }
}

