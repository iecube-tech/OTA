package com.iecube.ota.model.message.card;

import lombok.Data;

@Data
public class PublishTableRow {
    private String customer_name;
    private String customer_value;

    public PublishTableRow(String customer_name, String customer_value) {
        this.customer_name = customer_name;
        this.customer_value = customer_value;
    }
}
