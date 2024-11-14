package com.iecube.ota.utils.FeiShu;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

@Slf4j
public class CheckFSResponseUtil {
    public static void checkErrorResponse(ResponseEntity<String> response){
        if(response.getStatusCode().value() != 200){
            throw new FeiShuException("status code:"+response.getStatusCode().value() +":  "+response.getBody().toString());
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            Integer code = jsonNode.get("code")==null?null:jsonNode.get("code").asInt();
            String msg = jsonNode.get("msg")==null?null:jsonNode.get("msg").asText();
            if (code != null && !code.equals(0)) {
                System.out.println(response);
                throw new FeiShuException(msg);
            }
        }catch (Exception e){
            log.error("解析json字符串异常");
            e.printStackTrace();
            throw new FeiShuException("飞书响应消息异常");
        }
    }
}
