package com.iecube.ota.model.message.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

@SpringBootTest
public class MessageServiceTest {
    @Autowired
    private MessageService messageService;

    @Value("classpath:json/publishCardTemplateContent.json")
    private File publishJson;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode readJsonFile() throws IOException {
        return objectMapper.readTree(publishJson);
    }

    // 修改 JSON 数据并返回修改后的 JSON
    public String modifyJsonData() throws IOException {
        JsonNode rootNode = readJsonFile();
        //todo 修改product union_id table_raw_array
        ((ObjectNode) rootNode.get("data").get("template_variable")).put("union_id", "on_04441ed8744548c69a9bc83a06e4ab2a");
        // 将修改后的 JSON 转换为字符串
        System.out.println(objectMapper.writeValueAsString(rootNode));
        return objectMapper.writeValueAsString(rootNode);
    }

    @Test
    public void sendCardToUser() throws IOException {
        String res=messageService.sendCardToUser("on_04441ed8744548c69a9bc83a06e4ab2a", modifyJsonData());
        System.out.println(res);
    }
}
