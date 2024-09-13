package com.iecube.ota.model.mqtt.config;

import com.iecube.ota.model.Terminal.service.TerminalService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MqttConfig {

    @Value("${mqtt.broker}")
    private String brokerUrl;

    private static String topic="IECUBE/OTA/Terminal";

    @Autowired
    private TerminalService terminalService;

    @Bean
    public MqttClient mqttClient() throws MqttException {
        MqttClient client = new MqttClient(brokerUrl, "iecube.ota.service");
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        client.connect(options);
        client.subscribe(topic);
        log.info("已开启监听topic："+topic);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                // 处理连接断开
                log.info("连接已断开");
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                //处理接收到消息
                String payload = new String(mqttMessage.getPayload());
                // {"did":"1111111","productId":1111111,"name":"终端名称","fun":"aaa","version":"v1.0","timeStamp":1726129703055,"connecting":true,"activeDisconnection":false,"check":"sss"}
                //根据消息调用不同的服务
                terminalService.updateTerminalStatus(payload);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                //处理消息传递
                log.info("处理消息传递");
            }
        });
        return client;
    }
}
