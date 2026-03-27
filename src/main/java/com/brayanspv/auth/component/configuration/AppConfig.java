package com.brayanspv.auth.component.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppConfig {

    private String aesKey;
}
