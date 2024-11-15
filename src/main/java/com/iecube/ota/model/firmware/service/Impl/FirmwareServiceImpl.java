package com.iecube.ota.model.firmware.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonParseException;
import com.iecube.ota.exception.InsertException;
import com.iecube.ota.exception.ServiceException;
import com.iecube.ota.exception.UpdateException;
import com.iecube.ota.model.User.dto.CurrentUserDto;
import com.iecube.ota.model.firmware.entity.Firmware;
import com.iecube.ota.model.firmware.mapper.FirmwareMapper;
import com.iecube.ota.model.firmware.service.FirmwareService;
import com.iecube.ota.model.firmware.vo.FirmwareVo;
import com.iecube.ota.model.message.card.PublishTableRow;
import com.iecube.ota.model.message.service.MessageService;
import com.iecube.ota.model.mqtt.service.MqttService;
import com.iecube.ota.model.p_manager.service.PManageService;
import com.iecube.ota.model.product.entity.PNode;
import com.iecube.ota.model.product.mapper.ProductMapper;
import com.iecube.ota.model.resource.entity.Resource;
import com.iecube.ota.model.resource.mapper.ResourceMapper;
import com.iecube.ota.model.resource.service.ResourceService;
import com.iecube.ota.utils.examine.ExamineStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import java.time.Instant;
import com.google.gson.Gson;

@Service
@Slf4j
public class FirmwareServiceImpl implements FirmwareService {
    @Autowired
    private FirmwareMapper firmwareMapper;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private MqttService mqttService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private PManageService pManageService;

    @Value("${cdn}")
    private String CDNBasePath;

    @Value("${resource-location}/file")
    private String ResourceLocation;

    @Value("${domainName}")
    private String DomainName;

    @Value("${feishu.card.template.publishCard}")
    private File publishJson;

    @Value("${feishu.card.template.publishCallbackCard}")
    private File publishCallbackJson;

    @Override
    public List<FirmwareVo> getProductFirmware(Long productId){
        return firmwareMapper.getProductFirmware(productId);
    }

    /**
     * 添加一个新版本，并向选择的管理员发送一个审核消息
     * @param firmware 固件版本
     * @param currentUserDto 创建者
     * @return 此次发布版本的产品形态的全部的 FirmwareVo 列表
     */
    @Override
    public List<FirmwareVo> addFirmware(Firmware firmware, CurrentUserDto currentUserDto) {
        if(firmware.getNodeId()==null || firmware.getExamineUnionId()==null){
            throw new InsertException("参数异常");
        }
        if(!pManageService.isDeveloper(firmware.getNodeId(), currentUserDto.getUserInfoDto().getUnionId())){
            throw new ServiceException("not developer，无权操作");
        }
        if(firmware.getExamineUnionId().equals(currentUserDto.getUserInfoDto().getUnionId())){
            throw new ServiceException("请选择他人审批");
        }
        PNode node = productMapper.pNodeById(firmware.getNodeId());
        Resource resource = resourceMapper.getById(firmware.getResourceId());
        Path cdnPath = Paths.get(CDNBasePath+"/"+firmware.getNodeId()+"_"+node.getName()+"/"+firmware.getVersion());
        try {
            if (!Files.exists(cdnPath)) {// 检查路径是否存在
                Files.createDirectories(cdnPath);// 如果路径不存在，则创建 包括父目录
                log.info("创建cdn路径: {}", cdnPath);
            } else {
                log.info("cdn路径存在: {}", cdnPath);
            }
        } catch (IOException e) {
            throw new ServiceException("处理cdn路径异常"+e.getMessage());
        }
        if(resource.getType().equals("application/x-zip-compressed")||resource.getType().equals("application/zip")){
            // 如果zip包 解压到cdn路径下
            try {
                unzip(ResourceLocation+"/"+resource.getFilename(), cdnPath.toString());
                log.info("unzipping {}", cdnPath);
            } catch (IOException e) {
                throw new ServiceException("zip包解压异常"+e.getMessage());
            }
        }
        else {
            //不是zip包， 复制到cdn路径
            Path sourcePath = Paths.get(ResourceLocation+"/"+resource.getFilename());
            Path targetDir = Paths.get(CDNBasePath+"/"+firmware.getNodeId()+"_"+node.getName()+"/"+firmware.getVersion());
            Path targetPath = targetDir.resolve(resource.getOriginFilename());
            try{
                if (!Files.exists(targetDir)) {
                    Files.createDirectories(targetDir);
                }
                // 复制文件到目标目录并重命名
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e){
                throw new ServiceException("复制到cdn异常"+e.getMessage());
            }
        }
        String cdn = DomainName+"/cdn/"+firmware.getNodeId()+"_"+node.getName()+"/"+firmware.getVersion()+"/";
        Instant now = Instant.now();   //设置时间戳
        long timestamp = now.toEpochMilli();
        firmware.setCdn(cdn);
        firmware.setTimestamp(timestamp);
        firmware.setExamineStatus(ExamineStatus.PENDING.name());
        firmware.setCreator(currentUserDto.getUserInfoDto().getUnionId());
        Resource firmwareResource = resourceMapper.getById(firmware.getResourceId());
        PNode pNode = productMapper.pNodeById(firmware.getNodeId());
        List<PublishTableRow> tableRows = new ArrayList<>();
        tableRows.add(new PublishTableRow("版本号",firmware.getVersion()));
        tableRows.add(new PublishTableRow("全量包", firmware.getIsFull()?"是":"否"));
        tableRows.add(new PublishTableRow("软件包",firmwareResource.getOriginFilename()));
        tableRows.add(new PublishTableRow("包类型",firmwareResource.getType()));
        tableRows.add(new PublishTableRow("MD5",firmwareResource.getMd5()));
        tableRows.add(new PublishTableRow("字节大小",firmwareResource.getSize().toString()));
        tableRows.add(new PublishTableRow("版本特性",firmware.getDescription()));
        ObjectMapper objectMapper = new ObjectMapper();
        String message;
        try{
            JsonNode publishJsonNode = objectMapper.readTree(publishJson);
            ((ObjectNode) publishJsonNode.get("data").get("template_variable"))
                    .put("product", pNode.getName())
                    .put("union_id", firmware.getCreator())
                    .set("table_raw_array", objectMapper.convertValue(tableRows, JsonNode.class));
            message = objectMapper.writeValueAsString(publishJsonNode);
        }catch (IOException e){
            throw new ServiceException("处理json文件异常"+e.getMessage());
        }
        String msgRes = messageService.sendCardToUser(firmware.getExamineUnionId(), message);

        try{
            JsonNode msgJsonNode = objectMapper.readTree(msgRes);
            String messageId = msgJsonNode.get("data").get("message_id")==null?null:msgJsonNode.get("data").get("message_id").asText();
            firmware.setExamineMessageId(messageId);
            log.info("{} 发送了审批请求 {}, 消息ID: {}", currentUserDto.getUserInfoDto().getName(), message, messageId);
            Integer res  = firmwareMapper.insert(firmware);
            if(res!=1){
                throw new InsertException("新增数据异常");
            }
        }catch (IOException e){
            throw new ServiceException("处理json消息异常"+e.getMessage());
        }
        return firmwareMapper.getProductFirmware(firmware.getNodeId());
    }

    @Override
    public JsonNode examineFirmware(String messageId, Boolean res) {
        Firmware firmware = firmwareMapper.getFirmwareByExamineMessageId(messageId);
        if (firmware==null){
            throw new UpdateException("未找到相应数据");
        }
        FirmwareVo firmwareVo = firmwareMapper.getByFirmwareId(firmware.getId());
        if(res){
            firmware.setExamineStatus(ExamineStatus.APPROVED.name());
            firmware.setExamineTime(new Date());
            log.info("审批通过了, 消息ID: {}", messageId);
        }else {
            firmware.setExamineStatus(ExamineStatus.REJECTED.name());
            firmware.setExamineTime(new Date());
            log.info("审批未通过, 消息ID: {}", messageId);
        }
        Integer upRes = firmwareMapper.updateFirmware(firmware);
        if(upRes!=1){
            throw new UpdateException("更新数据异常");
        }
        List<PublishTableRow> tableRows = new ArrayList<>();
        tableRows.add(new PublishTableRow("版本号",firmware.getVersion()));
        tableRows.add(new PublishTableRow("软件包",firmwareVo.getOriginFilename()));
        tableRows.add(new PublishTableRow("包类型",firmwareVo.getType()));
        tableRows.add(new PublishTableRow("MD5",firmwareVo.getMd5()));
        tableRows.add(new PublishTableRow("字节大小",firmwareVo.getSize().toString()));
        tableRows.add(new PublishTableRow("版本特性",firmware.getDescription()));
        ObjectMapper objectMapper = new ObjectMapper();
        if(res){
            FirmwareVo firmwareVo1 = firmwareMapper.getByFirmwareId(firmware.getId());
            pushFirmware(firmwareVo1);
        }
        try{
            JsonNode publishCallbackJsonNode = objectMapper.readTree(publishCallbackJson);
            ((ObjectNode) publishCallbackJsonNode.get("data").get("template_variable"))
                    .put("product", firmwareVo.getProductName())
                    .put("union_id", firmware.getCreator())
                    .put("result", res?"审批通过":"审批拒绝")
                    .set("table_raw_array", objectMapper.convertValue(tableRows, JsonNode.class));
            return publishCallbackJsonNode;
        }catch (IOException e){
            throw new ServiceException("处理json文件异常"+e.getMessage());
        }
    }


    public void pushFirmware(FirmwareVo firmwareVo){
        // 获取最新的那个 进行 mqtt 推送
        Gson gson = new Gson();
        String jsonString = gson.toJson(firmwareVo);
        String topic = "IECUBE/OTA/"+firmwareVo.getProductId()+"/ActiveUpgrade";
        mqttService.topicPublish(topic,jsonString,1, true);
    }

    @Override
    public FirmwareVo getByFirmwareId(Long firmwareId) {
        return firmwareMapper.getByFirmwareId(firmwareId);
    }

    public static void unzip(String zipFilePath, String destDir) throws IOException {
        File destDirFile = new File(destDir);
        if (!destDirFile.exists()) {
            destDirFile.mkdirs();  // 创建目标目录
        }
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File entryFile = new File(destDirFile, entry.getName());

                if (entry.isDirectory()) {
                    entryFile.mkdirs();  // 创建目录
                } else {
                    // 创建父目录
                    File parentDir = entryFile.getParentFile();
                    if (parentDir != null && !parentDir.exists()) {
                        parentDir.mkdirs();
                    }

                    // 解压文件
                    try (InputStream in = zipFile.getInputStream(entry);
                         OutputStream out = new FileOutputStream(entryFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = in.read(buffer)) > 0) {
                            out.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }
}
