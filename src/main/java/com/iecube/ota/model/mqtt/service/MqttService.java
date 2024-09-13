package com.iecube.ota.model.mqtt.service;

public interface MqttService {
    /**
     *
     * @param topic
     * @param msg
     * @param qos
     * @param retained
     */
    void topicPublish(String topic, String msg, int qos, boolean retained);
}
