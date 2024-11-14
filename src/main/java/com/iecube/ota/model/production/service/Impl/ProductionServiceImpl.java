package com.iecube.ota.model.production.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iecube.ota.exception.InsertException;
import com.iecube.ota.exception.ServiceException;
import com.iecube.ota.exception.UpdateException;
import com.iecube.ota.model.User.dto.CurrentUserDto;
import com.iecube.ota.model.firmware.mapper.FirmwareMapper;
import com.iecube.ota.model.firmware.vo.FirmwareVo;
import com.iecube.ota.model.message.card.PublishTableRow;
import com.iecube.ota.model.message.dto.MessageDto;
import com.iecube.ota.model.message.service.MessageService;
import com.iecube.ota.model.p_manager.service.PManageService;
import com.iecube.ota.model.production.entity.Production;
import com.iecube.ota.model.production.entity.ProductionExamine;
import com.iecube.ota.model.production.mapper.ProductionMapper;
import com.iecube.ota.model.production.service.Impl.ex.ProductionException;
import com.iecube.ota.model.production.service.ProductionService;
import com.iecube.ota.model.production.vo.ProductionVo;
import com.iecube.ota.model.production_member.entity.ProductionMember;
import com.iecube.ota.model.production_member.service.ProductionMemberService;
import com.iecube.ota.utils.examine.ExamineStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ProductionServiceImpl implements ProductionService {

    @Autowired
    private ProductionMapper productionMapper;

    @Autowired
    private FirmwareMapper firmwareMapper;

    @Autowired
    private MessageService messageService;

    @Autowired
    private PManageService pManageService;

    @Autowired
    private ProductionMemberService productionMemberService;

    @Value("classpath:json/firmwareToProductionExamine.json")
    private File firmwareToProductionExamineJson;

    @Value("classpath:json/firmwareToProductionExamineCallback.json")
    private File firmwareToProductionExamineCallbackJson;

    @Value("classpath:json/newProduction.json")
    private File firmwareNewProductionJson;

    @Value("${domainFront}")
    private String domainFront;

    @Value("${feiShu.appId}")
    private String appId;

    /**
     * 提交转生产请求 发送审批消息
     * @param firmwareId 版本id
     * @param unionId 审批人
     * @param currentUserDto 操作用户
     */
    @Override
    public void firmwareToProduction(Long firmwareId, String unionId, CurrentUserDto currentUserDto) {
        FirmwareVo firmwareVo = firmwareMapper.getByFirmwareId(firmwareId);
        if(firmwareVo == null) {
            throw new ProductionException("未找到该版本");
        }
        if(!pManageService.isDeveloper(firmwareVo.getProductId(), currentUserDto.getUserInfoDto().getUnionId())){
            throw new ProductionException("not developer, 无权操作");
        }
        if(!firmwareVo.getExamineStatus().equals(ExamineStatus.APPROVED.name())){
            throw new ProductionException("该版本未经过审批");
        }
        if(!firmwareVo.getIsFull()){
            throw new ProductionException("非全量包不允许提交生产");
        }
        if(unionId.equals(currentUserDto.getUserInfoDto().getUnionId())){
            throw new ProductionException("请选择他人审批");
        }
        List<PublishTableRow> tableRows = new ArrayList<>();
        tableRows.add(new PublishTableRow("版本号",firmwareVo.getVersion()));
        tableRows.add(new PublishTableRow("全量包", firmwareVo.getIsFull()?"是":"否"));
        tableRows.add(new PublishTableRow("软件包",firmwareVo.getOriginFilename()));
        tableRows.add(new PublishTableRow("包类型",firmwareVo.getType()));
        tableRows.add(new PublishTableRow("MD5",firmwareVo.getMd5()));
        tableRows.add(new PublishTableRow("字节大小",firmwareVo.getSize().toString()));
        tableRows.add(new PublishTableRow("版本特性",firmwareVo.getDescription()));
        ObjectMapper objectMapper = new ObjectMapper();
        String message;
        try{
            JsonNode publishJsonNode = objectMapper.readTree(firmwareToProductionExamineJson);
            ((ObjectNode) publishJsonNode.get("data").get("template_variable"))
                    .put("product", firmwareVo.getProductName())
                    .put("union_id", currentUserDto.getUserInfoDto().getUnionId())
                    .set("table_raw_array", objectMapper.convertValue(tableRows, JsonNode.class));
            message = objectMapper.writeValueAsString(publishJsonNode);
        }catch (IOException e){
            throw new ServiceException("处理json文件异常"+e.getMessage());
        }
        String msgRes = messageService.sendCardToUser(unionId, message);
        try{
            ProductionExamine productionExamine = new ProductionExamine();
            productionExamine.setFirmwareId(firmwareId);
            productionExamine.setProductId(firmwareVo.getProductId());
            productionExamine.setCreator(currentUserDto.getUserInfoDto().getUnionId());
            productionExamine.setCreateTime(new Date());
            productionExamine.setExamineUnionId(unionId);
            productionExamine.setExamineStatus(ExamineStatus.PENDING.name());
            JsonNode msgJsonNode = objectMapper.readTree(msgRes);
            String messageId = msgJsonNode.get("data").get("message_id")==null?null:msgJsonNode.get("data").get("message_id").asText();
            productionExamine.setExamineMessageId(messageId);
            log.info("{} 发送了审批请求 {}, 消息ID: {}", currentUserDto.getUserInfoDto().getName(), message, messageId);
            Integer res  = productionMapper.insertProductionExamine(productionExamine);
            if(res!=1){
                throw new InsertException("新增数据异常");
            }
        }catch (IOException e){
            throw new ServiceException("处理json消息异常"+e.getMessage());
        }
    }

    @Override
    public JsonNode examineCallback(String messageId, Boolean res) {
        ProductionExamine productionExamine = productionMapper.getByExamineMessageId(messageId);
        if(res){
            // 审批通过
            // production_examine update
            // 查询是否已经有该product的production条目，有则update , 没有则 new production ，
            productionExamine.setExamineStatus(ExamineStatus.APPROVED.name());
            Production production = new Production();
            production.setProductId(productionExamine.getProductId());
            production.setFirmwareId(productionExamine.getFirmwareId());
            production.setExamineId(productionExamine.getId());
            Production oldProduction = productionMapper.getProduction(productionExamine.getProductId());
            if(oldProduction!=null){
                production.setId(oldProduction.getId());
                Integer res1 = productionMapper.updateProduction(production);
                if(res1!=1){
                    throw new UpdateException("更新数据异常");
                }
            }else{
                Integer res1 = productionMapper.insertProduction(production);
                if(res1!=1){
                    throw new InsertException("添加数据异常");
                }
            }
            log.info("审批通过了, 消息ID: {}", messageId);
        }else {
            // 审批不通过
            productionExamine.setExamineStatus(ExamineStatus.REJECTED.name());
            log.info("审批未通过, 消息ID: {}", messageId);
        }
        productionExamine.setExamineTime(new Date());
        Integer upRes = productionMapper.updateProductionExamine(productionExamine);
        if(upRes!=1){
            throw new UpdateException("更新数据异常");
        }
        FirmwareVo firmwareVo = firmwareMapper.getByFirmwareId(productionExamine.getFirmwareId());
        List<PublishTableRow> tableRows = new ArrayList<>();
        tableRows.add(new PublishTableRow("版本号",firmwareVo.getVersion()));
        tableRows.add(new PublishTableRow("全量包", firmwareVo.getIsFull()?"是":"否"));
        tableRows.add(new PublishTableRow("软件包",firmwareVo.getOriginFilename()));
        tableRows.add(new PublishTableRow("包类型",firmwareVo.getType()));
        tableRows.add(new PublishTableRow("MD5",firmwareVo.getMd5()));
        tableRows.add(new PublishTableRow("字节大小",firmwareVo.getSize().toString()));
        tableRows.add(new PublishTableRow("版本特性",firmwareVo.getDescription()));
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            JsonNode publishCallbackJsonNode = objectMapper.readTree(firmwareToProductionExamineCallbackJson);
            ((ObjectNode) publishCallbackJsonNode.get("data").get("template_variable"))
                    .put("product", firmwareVo.getProductName())
                    .put("union_id", productionExamine.getCreator())
                    .put("result",res?"审批通过":"审批拒绝")
                    .set("table_raw_array", objectMapper.convertValue(tableRows, JsonNode.class));
            if(res){
                ProductionVo productionVo = new ProductionVo();
                productionVo.setProductionExamine(productionExamine);
                productionVo.setFirmwareVo(firmwareVo);
                this.newProductionInform(productionVo);
            }
            return publishCallbackJsonNode;
        }catch (IOException e){
            throw new ServiceException("处理json文件异常"+e.getMessage());
        }
    }

    @Override
    public ProductionVo getProduction(Long productId) {
        Production production = productionMapper.getProduction(productId);
        if(production==null){
            return null;
        }
        ProductionExamine productionExamine = productionMapper.getById(production.getExamineId());
        FirmwareVo firmwareVo = firmwareMapper.getByFirmwareId(production.getFirmwareId());
        ProductionVo productionVo = new ProductionVo();
        productionVo.setProductionExamine(productionExamine);
        productionVo.setFirmwareVo(firmwareVo);
        return productionVo;
    }

    @Override
    public void newProductionInform(ProductionVo productionVo) {
        List<ProductionMember> productionMemberList =productionMemberService.findAll();
        System.out.println(productionMemberList);
        List<String> memberUnionIds = new ArrayList<>();
        for(ProductionMember productionMember : productionMemberList){
            memberUnionIds.add(productionMember.getUnionId());
        }
        System.out.println(memberUnionIds.toString());
        if(memberUnionIds.isEmpty()){
            return;
        }
        MessageDto messageDto = new MessageDto();
        messageDto.setMsgType("interactive");
        JsonNode card;
        FirmwareVo firmwareVo = productionVo.getFirmwareVo();
        List<PublishTableRow> tableRows = new ArrayList<>();
        tableRows.add(new PublishTableRow("审批时间",productionVo.getProductionExamine().getExamineTime().toString()));
        tableRows.add(new PublishTableRow("版本号",firmwareVo.getVersion()));
        tableRows.add(new PublishTableRow("全量包", firmwareVo.getIsFull()?"是":"否"));
        tableRows.add(new PublishTableRow("软件包",firmwareVo.getOriginFilename()));
        tableRows.add(new PublishTableRow("包类型",firmwareVo.getType()));
        tableRows.add(new PublishTableRow("MD5",firmwareVo.getMd5()));
        tableRows.add(new PublishTableRow("字节大小",firmwareVo.getSize().toString()));
        tableRows.add(new PublishTableRow("版本特性",firmwareVo.getDescription()));
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            JsonNode newProductionJsonNode = objectMapper.readTree(firmwareNewProductionJson);
            ((ObjectNode) newProductionJsonNode.get("data").get("template_variable"))
                    .put("product", firmwareVo.getProductName())
                    .set("table_raw_array", objectMapper.convertValue(tableRows, JsonNode.class));
            ((ObjectNode) newProductionJsonNode.get("data").get("template_variable").get("detail_url"))
                    .put("pc_url",
                            "https://applink.feishu.cn/client/web_app/open?appId="+appId+"&path=/production/"+firmwareVo.getProductId())
                    .put("android_url",
                            "https://applink.feishu.cn/client/web_app/open?appId="+appId+"&path=/production/"+firmwareVo.getProductId())
                    .put("ios_url",
                            "https://applink.feishu.cn/client/web_app/open?appId="+appId+"&path=/production/"+firmwareVo.getProductId())
                    .put("url","https://applink.feishu.cn/client/web_app/open?appId="+appId);
            card = newProductionJsonNode;
        }catch (IOException e){
            throw new ServiceException("处理json文件异常"+e.getMessage());
        }
        messageDto.setCard(card);
        System.out.println(memberUnionIds);
        System.out.println(messageDto);
        try{
            System.out.println(objectMapper.writeValueAsString(memberUnionIds));
        }catch (IOException e){
            throw new ServiceException("处理json文件异常"+e.getMessage());
        }
        messageService.sendToDepartmentAndUserList(null,memberUnionIds,messageDto);
    }
}
