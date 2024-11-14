package com.iecube.ota.model.resource.service;

import com.iecube.ota.model.resource.entity.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ResourceService {
    Resource UploadImage(MultipartFile file, String lastModifiedUser) throws IOException;

    Resource UploadFile(MultipartFile file, String creator) throws IOException;

    void deleteResource(String filename);

    void deleteById(Long id);

    Resource getResourceByFilename(String filename);
}
