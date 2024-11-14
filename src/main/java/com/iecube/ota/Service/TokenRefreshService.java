package com.iecube.ota.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.ota.config.AppAccessTokenConfig;
import com.iecube.ota.exception.ServiceException;
import com.iecube.ota.utils.FeiShu.CheckFSResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Service
@Slf4j
public class TokenRefreshService {
    @Autowired
    private AppAccessTokenConfig AppAccessTokenConfig;

    @Value("${feiShu.appId}")
    private String appId;

    @Value("${feiShu.appSecret}")
    private String appSecret;

    private String APP_ACCESS_TOKEN_URI = "https://open.feishu.cn/open-apis/auth/v3/app_access_token/internal";

    // 模拟获取新的 token 的方法
    private JsonNode fetchNewToken() {
        String url = APP_ACCESS_TOKEN_URI+"?app_id="+appId+"&app_secret="+appSecret;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        try{
            CheckFSResponseUtil.checkErrorResponse(response);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode;
        }catch (Exception e){
            log.error("刷新AppAccessToken失败"+e.getMessage());
            e.printStackTrace();
            throw new ServiceException("JSON 转换异常");
        }
    }

    // 每 1 小时 40 分钟刷新一次 token
    @Scheduled(fixedRate = 100 * 60 * 1000) // 100分钟
    public void refreshAccessToken() {
        JsonNode response = fetchNewToken();
        AppAccessTokenConfig.setAppAccessToken(response.get("app_access_token").asText());
        AppAccessTokenConfig.setTenantAccessToken(response.get("tenant_access_token").asText());
        log.info("刷新appToken成功：appAccessToken:" + response.get("app_access_token").asText()+
                " tenant_access_token:"+response.get("tenant_access_token").asText());
    }
}
