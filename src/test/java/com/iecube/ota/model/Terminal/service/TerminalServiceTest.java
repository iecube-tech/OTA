package com.iecube.ota.model.Terminal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.ota.model.Terminal.entity.TerminalEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class TerminalServiceTest {
    @Autowired
    private TerminalService terminalService;

    @Test
    public void outJsonTest(){
        ObjectMapper objectMapper = new ObjectMapper();
        TerminalEntity terminalEntity = new TerminalEntity();
        terminalEntity.setDid("1111111");
        terminalEntity.setProductId(1111111L);
        terminalEntity.setName("终端名称");
        terminalEntity.setFun("aaa");
        terminalEntity.setVersion("v1.0");
        terminalEntity.setTimeStamp(new Date().getTime());
        terminalEntity.setConnecting(true);
        terminalEntity.setActiveDisconnection(false);
        try{
            String jsonString = objectMapper.writeValueAsString(terminalEntity);
            System.out.println(jsonString);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    public void getTerminalListByProductId(){
        List<TerminalEntity> terminalEntity = terminalService.getTerminalListByProductId(11);
        System.out.println(terminalEntity);
    }

    @Test
    public void a(){
        terminalService.devicePassiveUpgrade("1111111",9L);
    }
}
