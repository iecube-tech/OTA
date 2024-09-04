package com.iecube.ota.model.firmware.service.Impl;

import com.iecube.ota.exception.InsertException;
import com.iecube.ota.model.firmware.entity.Firmware;
import com.iecube.ota.model.firmware.mapper.FirmwareMapper;
import com.iecube.ota.model.firmware.service.FirmwareService;
import com.iecube.ota.model.firmware.vo.FirmwareVo;
import com.iecube.ota.model.mqtt.service.MqttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.Instant;
import com.google.gson.Gson;

@Service
public class FirmwareServiceImpl implements FirmwareService {
    @Autowired
    private FirmwareMapper firmwareMapper;

    @Autowired
    private MqttService mqttService;

    @Override
    public List<FirmwareVo> getProductFirmware(Long productId){
        List<FirmwareVo> firmwareVoList = firmwareMapper.getProductFirmware(productId);
        return firmwareVoList;
    }

    @Override
    public List<FirmwareVo> addFirmware(Firmware firmware) {
        if(firmware.getNodeId() == null){
            throw new InsertException("参数异常");
        }
        //设置时间戳
        Instant now = Instant.now();
        long timestamp = now.toEpochMilli();
        firmware.setTimestamp(timestamp);
        Integer res  = firmwareMapper.insert(firmware);
        if(res!=1){
            throw new InsertException("新增数据异常");
        }
        List<FirmwareVo> firmwareVoList = firmwareMapper.getProductFirmware(firmware.getNodeId());
        // 获取最新的那个 进行 mqtt 推送
        FirmwareVo lastFirmwareVo = firmwareVoList.get(0);
        Gson gson = new Gson();
        String jsonString = gson.toJson(lastFirmwareVo);
        String topic = lastFirmwareVo.getProductId()+"/ActiveUpgrade";
        mqttService.topicPublish(topic,jsonString);
        return firmwareVoList;
    }
}
