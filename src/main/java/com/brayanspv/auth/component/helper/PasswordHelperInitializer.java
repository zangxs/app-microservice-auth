package com.brayanspv.auth.component.helper;

import com.brayanspv.auth.component.configuration.AppConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordHelperInitializer {

    private final AppConfig appConfig;

    @PostConstruct
    public void init() {
        PasswordHelper.init(appConfig.getAesKey());
    }
}
