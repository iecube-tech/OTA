package com.iecube.ota.model.callback.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.ota.exception.ServiceException;
import com.iecube.ota.model.callback.entity.ReqDto;
import com.iecube.ota.model.callback.entity.ResDto;
import com.iecube.ota.model.callback.entity.Toast;
import com.iecube.ota.model.callback.enumeration.CallbackEnum;
import com.iecube.ota.model.callback.service.CallbackService;
import com.iecube.ota.model.firmware.service.FirmwareService;
import com.iecube.ota.model.production.service.ProductionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class CallbackServiceImpl implements CallbackService {

    @Autowired
    private FirmwareService firmwareService;

    @Autowired
    private ProductionService productionService;

    @Override
    public ResDto fs(ReqDto reqDto) {
        ResDto resDto = new ResDto();
        ObjectMapper objectMapper = new ObjectMapper();
        boolean res;
        String messageId;
        String type;
        try{
            JsonNode jn = objectMapper.valueToTree(reqDto.getEvent().getAction()).get("value").get("value").get("examine_res");
            JsonNode jt = objectMapper.valueToTree(reqDto.getEvent().getAction()).get("value").get("value").get("which");
            JsonNode jm= objectMapper.valueToTree(reqDto.getEvent().getContext()).get("open_message_id");
            res = jn != null && jn.asBoolean();
            messageId = jm != null ? jm.asText() : "";
            type = jt != null ? jt.asText() : "";
        }catch (Exception e){
            return resDto;
        }
        Toast toast = new Toast();
        try{
            if(type.isEmpty()){
                throw new ServiceException("回调参数异常");
            } else if (type.equals(CallbackEnum.FP.name())) {
                JsonNode card = firmwareService.examineFirmware(messageId, res);
                resDto.setCard(card);
            } else if (type.equals(CallbackEnum.F2P.name())) {
                JsonNode card = productionService.examineCallback(messageId, res);
                System.out.println(card);
                resDto.setCard(card);
            }
            toast.setType("success");
            toast.setContent("操作成功");
        }catch (Exception e){
            toast.setType("error");
            toast.setContent(e.getMessage());
            log.warn("审批异常, 消息ID: {}", messageId);
            resDto.setToast(toast);
            return resDto;
        }
        resDto.setToast(toast);
        return resDto;
    }
}
