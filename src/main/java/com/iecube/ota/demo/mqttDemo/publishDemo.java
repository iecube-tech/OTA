package com.iecube.ota.demo.mqttDemo;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Scanner;

public class publishDemo {
    public static void main(String[] args) {
        String broker = "tcp://47.94.161.154:1883";
//        String broker = "tcp://broker.emqx.io:1883";
        String topic = "IECUBE/OTA/Terminal";
        String clientId = "device";
        String content = "{\"did\":\"1111111\",\"productId\":1111111,\"name\":\"终端名称\",\"fun\":\"aaa\",\"version\":\"v1.0\",\"timeStamp\":1726129703055,\"connecting\":true,\"activeDisconnection\":false,\"check\":\"sss\"}";
        String willMessageStr = "{\"did\":\"1111111\",\"productId\":1111111,\"name\":\"终端名称\",\"fun\":\"aaa\",\"version\":\"v1.0\",\"timeStamp\":1726129703055,\"connecting\":false,\"activeDisconnection\":true,\"check\":\"sss\"}";
        int qos = 0;

        try {
            MqttClient client = new MqttClient(broker, clientId, new MemoryPersistence());
            // 连接参数
            MqttConnectOptions options = new MqttConnectOptions();
            // 设置用户名和密码
//            options.setUserName(username);
//            options.setPassword(password.toCharArray());
            options.setConnectionTimeout(60);
            options.setKeepAliveInterval(60);
            options.setWill(topic, willMessageStr.getBytes() , qos, false);
            // 连接
            client.connect(options);
            // 创建消息并设置 QoS
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            // 发布消息
            client.publish(topic, message);
            System.out.println("Message published");
            System.out.println("topic: " + topic);
            System.out.println("message content: " + content);

            // 启动子线程：监听按键输入
            Thread inputThread = new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("exit")) {
                        closeConnection(client);
                        break;
                    }
                }
                scanner.close();
            });

            inputThread.start();
        } catch (MqttException e) {
            e.printStackTrace();
//            throw new RuntimeException(e);
        }
    }

    private static void closeConnection(MqttClient client) {
        try{
            // 关闭连接
            System.out.println("关闭连接");
            String content = "{\"did\":\"1111111\",\"productId\":1111111,\"name\":\"终端名称\",\"fun\":\"aaa\",\"version\":\"v1.0\",\"timeStamp\":1726129703055,\"connecting\":false,\"activeDisconnection\":true,\"check\":\"sss\"}";
            client.publish("IECUBE/OTA/Terminal", content.getBytes(),0, false);
            client.disconnect();
            // 关闭客户端
            client.close();
        }catch (MqttException  e){
            e.printStackTrace();
        }
    }
}
