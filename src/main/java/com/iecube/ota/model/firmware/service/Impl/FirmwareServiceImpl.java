package com.iecube.ota.model.firmware.service.Impl;

import com.iecube.ota.exception.InsertException;
import com.iecube.ota.model.firmware.entity.Firmware;
import com.iecube.ota.model.firmware.mapper.FirmwareMapper;
import com.iecube.ota.model.firmware.service.FirmwareService;
import com.iecube.ota.model.firmware.vo.FirmwareVo;
import com.iecube.ota.model.mqtt.service.MqttService;
import com.iecube.ota.model.product.entity.PNode;
import com.iecube.ota.model.product.mapper.ProductMapper;
import com.iecube.ota.model.resource.entity.Resource;
import com.iecube.ota.model.resource.mapper.ResourceMapper;
import com.iecube.ota.model.resource.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    @Value("${cdn}")
    private String CDNBasePath;

    @Value("${resource-location}/file")
    private String ResourceLocation;

    @Value("${domainName}")
    private String DomainName;

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
        PNode node = productMapper.pNodeById(firmware.getNodeId());
        Resource resource = resourceMapper.getById(firmware.getResourceId());
        Path cdnPath = Paths.get(CDNBasePath+"/"+firmware.getNodeId()+"_"+node.getName()+"/"+firmware.getVersion());
        try {
            if (!Files.exists(cdnPath)) {// 检查路径是否存在
                Files.createDirectories(cdnPath);// 如果路径不存在，则创建 包括父目录
                log.info("创建cdn路径: " + cdnPath);
            } else {
                log.info("cdn路径存在: " + cdnPath);
            }
        } catch (IOException e) {
            // todo 异常处理
            e.printStackTrace();
        }
        if(resource.getType().equals("application/x-zip-compressed")){
            // 如果zip包 解压到cdn路径下
            try {
                unzip(ResourceLocation+"/"+resource.getFilename(), cdnPath.toString());
                log.info("unzipping "+cdnPath);
            } catch (IOException e) {
                e.printStackTrace();
                // todo 异常处理
            }
        }else{
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
                //todo 异常处理
                e.printStackTrace();
            }
        }
        String cdn = DomainName+"/cdn/"+firmware.getNodeId()+"_"+node.getName()+"/"+firmware.getVersion()+"/";
        firmware.setCdn(cdn);
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
        String topic = "IECUBE/OTA/"+lastFirmwareVo.getProductId()+"/ActiveUpgrade";
        mqttService.topicPublish(topic,jsonString,1, true);
        return firmwareVoList;
    }

    @Override
    public FirmwareVo getByFirmwareId(Long firmwareId) {
        FirmwareVo firmwareVo = firmwareMapper.getByFirmwareId(firmwareId);
        return firmwareVo;
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
