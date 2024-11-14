package com.iecube.ota.model.message.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class MessageDto {
    private String msgType;
    private String content;
    private JsonNode card;
}
