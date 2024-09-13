package com.iecube.ota.model.Terminal.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.iecube.ota.model.Terminal.entity.TerminalEntity;
import com.iecube.ota.model.Terminal.mapper.TerminalMapper;
import com.iecube.ota.model.Terminal.service.TerminalService;
import com.iecube.ota.model.Terminal.service.ex.SendMqttMessageException;
import com.iecube.ota.model.firmware.service.FirmwareService;
import com.iecube.ota.model.firmware.vo.FirmwareVo;
import com.iecube.ota.model.mqtt.service.MqttService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TerminalServiceImpl implements TerminalService {

    private String terminalPassiveUpgrade = "IECUBE/OTA/";

    @Autowired
    private TerminalMapper terminalMapper;

    @Autowired
    private FirmwareService firmwareService;

    @Autowired
    private MqttService mqttService;

    @Override
    public void updateTerminalStatus(String mqttMessage){
        //{"did":"1111111","productId":1111111,"name":"终端名称","fun":"aaa","version":"v1.0","timeStamp":1726129703055,"connecting":true,"activeDisconnection":false,"check":"sss"}
        ObjectMapper objectMapper = new ObjectMapper();
        TerminalEntity terminalEntity = null;
        try {
            terminalEntity = objectMapper.readValue(mqttMessage, TerminalEntity.class);
            log.info("处理来自："+terminalEntity.getDid()+"的消息");
        } catch (Exception e) {
            log.error("终端消息格式错误：" + mqttMessage);
            e.printStackTrace();
        }
        try{
            TerminalEntity existTerminal = terminalMapper.getByDid(terminalEntity.getDid());
            if(existTerminal != null){
                // 更新
                Integer res = terminalMapper.updateByDid(terminalEntity);
                if(res!=1){
                    log.error("持久化更新terminal信息失败");
                }
            }else{
                // 新建
                Integer res = terminalMapper.insert(terminalEntity);
                if(res!=1){
                    log.error("持久化新建terminal信息失败");
                }
            }
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<TerminalEntity> getTerminalListByProductId(Integer productId){
        List<TerminalEntity> terminalEntityList = terminalMapper.getByProductId(productId);
        return terminalEntityList;
    }

    @Override
    public TerminalEntity devicePassiveUpgrade(String did, Long firmwareId){

        FirmwareVo firmwareVo = firmwareService.getByFirmwareId(firmwareId);
        TerminalEntity terminal = terminalMapper.getByDid(did);
        try{
            Gson gson = new Gson();
            String jsonString = gson.toJson(firmwareVo);
            String topic = "IECUBE/OTA/"+terminal.getDid()+"/PassiveUpgrade";
            mqttService.topicPublish(topic,jsonString,0, false);
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            throw new SendMqttMessageException("发送设备更新版本指令失败");
        }

        terminalMapper.updateStatusByDid(0,did);
        terminal.setStatus(false);
        return terminal;
    }

}
