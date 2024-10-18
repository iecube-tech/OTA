package com.iecube.ota.utils.FeiShu;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.ota.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class Auth {
    private static final String fsHost="https://open.feishu.cn";

    private static final String appId="cli_a79a7747870a900c";

    private static final String appSecret="VovoA0iwREBYYmubg3XtpbN1GOzSrSkd";

//    @Value("${feiShu.host}")
//    private String fsHost;
//
//    @Value("${feiShu.appId}")
//    private String appId;
//
//    @Value("${feiShu.appSecret}")
//    private String appSecret;

    private static final String USER_ACCESS_TOKEN_URI = "/open-apis/authen/v1/access_token";
    private static final String APP_ACCESS_TOKEN_URI = "/open-apis/auth/v3/app_access_token/internal";
    private static final String USER_INFO_URI = "/open-apis/authen/v1/user_info";
    private static String appAccessToken;
    private static String userAccessToken;


    public String getUserAccessToken(){
        return this.userAccessToken;
    }

    public String getAppAccessToken(){
        return this.appAccessToken;
    }

    public String getAppId(){
        return this.appId;
    }

    private String genUrl(String url){
        return fsHost+url;
    }

    private void authorizeAppAccessToken(){
        String url = this.genUrl(APP_ACCESS_TOKEN_URI);
        url=url+"?app_id="+appId+"&app_secret="+appSecret;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        HttpEntity<MultiValueMap<String, String> > entity = new HttpEntity<>(headers);
//        String response = restTemplate.postForObject(url, requestBody, String.class);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        this.checkErrorResponse(response);
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            this.appAccessToken=jsonNode.get("app_access_token").asText();
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            throw new ServiceException("JSON 转换异常");
        }
    }

    //这里也可以拿到user_info
    //但是考虑到服务端许多API需要user_access_token，如文档：https://open.feishu.cn/document/ukTMukTMukTM/uUDN04SN0QjL1QDN/document-docx/docx-overview
    //      建议的最佳实践为先获取user_access_token，再获得user_info
    // user_access_token后续刷新可以参阅文档：https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/authen-v1/authen/refresh_access_token
    public void authorizeUserAccessToken(String code){
        // 获取 user_access_token, 依托于飞书开放能力实现.
        // 文档链接: https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/authen-v1/authen/access_token
        this.authorizeAppAccessToken();
        String url = this.genUrl(USER_ACCESS_TOKEN_URI);
//        url=url+
         // “app_access_token” 位于HTTP请求的请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/json; charset=utf-8");
        headers.set("Authorization", "Bearer " +this.appAccessToken);
        // 临时授权码 code 位于HTTP请求的请求体
        Map<String,String> reqBody = new HashMap<>();
        reqBody.put("grant_type", "authorization_code");
        reqBody.put("code", code);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(reqBody,headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        this.checkErrorResponse(response);
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            this.userAccessToken = jsonNode.get("data").get("access_token").asText();
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            throw new ServiceException("JSON 转换异常");
        }
    }

    public String getUserInfo(){
        // 获取 user info, 依托于飞书开放能力实现.
        // 文档链接: https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/authen-v1/authen/user_info
        String url=this.genUrl(USER_INFO_URI);
        // “user_access_token” 位于HTTP请求的请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        headers.set("Authorization", "Bearer " + this.userAccessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        this.checkErrorResponse(response);
        // 如需了解响应体字段说明与示例，请查询开放平台文档：
        // https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/authen-v1/authen/access_token
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("data").toString();
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            throw new ServiceException("JSON 转换异常");
        }
    }

    private void checkErrorResponse(ResponseEntity<String> response){
        if(response.getStatusCode().value() !=200 ){
            throw new FeiShuException("status code:"+response.getStatusCode().value() +":  "+response.getBody().toString());
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            Integer code = jsonNode.get("code").asInt();
            if(!code.equals(0)){
                System.out.println(response);
                throw new FeiShuException("请求响应码异常");
            }
        }catch (Exception e){
            log.error("解析json字符串异常");
            e.printStackTrace();
        }
    }

}
