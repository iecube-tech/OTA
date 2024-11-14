package com.iecube.ota.model.message.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CardConfig {
    @JsonProperty("wide_screen_mode")
    private boolean wideScreenMode;
}
