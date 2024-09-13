package com.iecube.ota.model.mqtt.service.impl;

import com.iecube.ota.model.mqtt.service.MqttService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MqttServiceImpl implements MqttService {

    @Value("${mqtt.broker}")
    private String Broker;

    @Value("${domainName}")
    private String clientId;

    @Override
    @Async
    public void topicPublish(String topic, String msg, int qos, boolean retained) {
        try{
            MqttClient mqttClient = new MqttClient(Broker, clientId, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();// 连接参数
            options.setConnectionTimeout(60);
            options.setKeepAliveInterval(60);
            mqttClient.connect(options);// 连接
            MqttMessage message = new MqttMessage(msg.getBytes());// 创建消息并设置 QoS
            message.setQos(qos);
            message.setRetained(retained); //保留消息
            mqttClient.publish(topic, message); // 发布消息
            log.info("Message published, topic: " + topic+"; message: " + msg+"; retained: "+retained);
            mqttClient.disconnect(); // 关闭连接
            mqttClient.close();// 关闭客户端
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
