package com.iecube.ota.config;

import com.iecube.ota.Service.TokenRefreshService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class AppAccessTokenConfig {
    private final AtomicReference<String> appAccessToken = new AtomicReference<>();
    private final AtomicReference<String> tenantAccessToken = new AtomicReference<>();

    public String getAppAccessToken() {
        return appAccessToken.get();
    }

    public void setAppAccessToken(String token) {
        appAccessToken.set(token);
    }

    public String getTenantAccessToken() {
        return tenantAccessToken.get();
    }

    public void setTenantAccessToken(String token){
        tenantAccessToken.set(token);
    }

}
