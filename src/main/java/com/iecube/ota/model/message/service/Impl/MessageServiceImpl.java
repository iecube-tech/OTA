package com.iecube.ota.model.message.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.ota.config.AppAccessTokenConfig;
import com.iecube.ota.exception.ServiceException;
import com.iecube.ota.model.message.dto.MessageDto;
import com.iecube.ota.model.message.service.MessageService;
import com.iecube.ota.utils.FeiShu.CheckFSResponseUtil;
import com.iecube.ota.utils.FeiShu.FeiShuException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageServiceImpl implements MessageService {

    private static final String BATCH_SEND_URL = "https://open.feishu.cn/open-apis/message/v4/batch_send/";

    private static final String SEND_TO_USER="https://open.feishu.cn/open-apis/im/v1/messages?receive_id_type=union_id";

    private AppAccessTokenConfig appAccessTokenConfig;

    @Autowired
    public void setAppAccessTokenConfig(AppAccessTokenConfig appAccessTokenConfig) {
        this.appAccessTokenConfig = appAccessTokenConfig;
    }

    @Override
    public void sendToUser(String to, MessageDto message) {

    }

    @Override
    public void sendToDepartmentAndUserList(List<String> departmentList, List<String> userList, MessageDto message) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        headers.set("Authorization", "Bearer "+ appAccessTokenConfig.getTenantAccessToken());
        Map<String,Object> reqBody = new HashMap<>();
        reqBody.put("msg_type", message.getMsgType());
        if(message.getMsgType().equals("interactive")){
            reqBody.put("card", message.getCard());
        }else {
            reqBody.put("content", message.getContent());
        }
        if (departmentList != null && !departmentList.isEmpty()) {
            reqBody.put("department_ids", departmentList);
        }
        if (userList != null && !userList.isEmpty()) {
            reqBody.put("union_ids", userList);

        }
        HttpEntity<Map<String, Object>> req = new HttpEntity<>(reqBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(BATCH_SEND_URL, req, String.class);
        try {
            CheckFSResponseUtil.checkErrorResponse(response);

        } catch (FeiShuException e) {
            throw new ServiceException(e.getMessage());
        }

    }

    @Override
    public String sendCardToUser(String to, String cardMsg) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        headers.set("Authorization", "Bearer "+ appAccessTokenConfig.getTenantAccessToken());
        Map<String,String> reqBody = new HashMap<>();
        reqBody.put("receive_id", to);
        reqBody.put("msg_type", "interactive");
        reqBody.put("content", cardMsg);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(reqBody,headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(SEND_TO_USER, HttpMethod.POST, entity, String.class);
        try{
            CheckFSResponseUtil.checkErrorResponse(response);
            return response.getBody();
        }catch (FeiShuException e){
            throw new ServiceException(e.getMessage());
        }
    }
}
