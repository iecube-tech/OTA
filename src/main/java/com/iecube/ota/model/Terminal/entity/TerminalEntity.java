package com.iecube.ota.model.Terminal.entity;

import lombok.Data;

@Data
public class TerminalEntity {
    /**
     * did 设备id 用于区分唯一设备
     * productId : ota平台产品id
     * name： 设备名
     * fun:设备类型，
     * version : 版本号
     * timeStamp: 消息时间戳
     * connecting: 连接建立时发送true， 连接断开时发送 false
     * activeDisconnection： 用于遗嘱消息（false） 或者主动断开连接（true）
     * status: 默认为true，当下发更新指令为false，设备更新被动更新功能不可用为false
     * check: 消息校验码 ==> 暂定 'sss'
     */
    String did;
    Long productId;
    String name;
    String fun;
    String version;
    Long timeStamp;
    Boolean connecting;
    Boolean activeDisconnection;
    String check="sss";
    Boolean status;
}
