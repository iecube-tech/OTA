package com.iecube.ota.model.mqtt.service;

public interface MqttService {
    void topicPublish(String topic, String msg);
}
