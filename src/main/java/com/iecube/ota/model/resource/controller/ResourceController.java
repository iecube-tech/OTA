package com.iecube.ota.model.resource.controller;

import com.iecube.ota.BaseController.BaseController;
import com.iecube.ota.model.resource.entity.Resource;
import com.iecube.ota.model.resource.service.ResourceService;
import com.iecube.ota.model.resource.vo.WangEditorRes;
import com.iecube.ota.utils.DownloadUtil;
import com.iecube.ota.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

//import javax.servlet.http.HttpServletResponse;
//import org.springframework.http.server.reactive.ServerHttpResponse;
import jakarta.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/files")
public class ResourceController extends BaseController {
    @Autowired
    private ResourceService resourceService;

    @Value("${resource-location}/image")
    private String image;

    // 文件路径
    @Value("${resource-location}/file")
    private String files;
    

    /**
     * 上传图片
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upimage")
    public JsonResult<Resource> UploadImage(MultipartFile file) throws IOException{
        long creator = 0;
        Resource resource = resourceService.UploadImage(file,creator);
        return new JsonResult<>(OK, resource);
    }

    @PostMapping ("/e/image")
    public JsonResult<WangEditorRes> EditorUploadImage(MultipartFile file) throws IOException{
        long creator = 0;
        Resource resource = resourceService.UploadImage(file,creator);
        WangEditorRes wangEditorRes = new WangEditorRes();
        wangEditorRes.setUrl("/files/image/"+resource.getFilename());
        wangEditorRes.setAlt(resource.getOriginFilename());
        wangEditorRes.setTitle(resource.getOriginFilename());
        wangEditorRes.setFilename(resource.getFilename());
        return new JsonResult<>(OK,0,wangEditorRes);
    }

    @PostMapping("/upfile")
    public JsonResult<Resource> UploadFile(MultipartFile file) throws IOException{
        long creator = 0;
        Resource resource = resourceService.UploadFile(file,creator);
        return new JsonResult<>(OK, resource);
    }

    /**
     * 请求图片
     * @param fileName
     * @param response
     */
    @GetMapping("image/{fileName}")
    public void GetImage(@PathVariable String fileName, HttpServletResponse response){
        Resource resource = resourceService.getResourceByFilename(fileName);
//        DownloadUtil.httpDownload(new File(this.image, fileName), resource.getOriginFilename(), response);
        DownloadUtil.httpDownload(new File(this.image, resource.getFilename()), response);
    }

    /**
     * 请求文件
     * @param fileName
     * @param response
     */
    @GetMapping("file/{fileName}")
    public void GetFile(@PathVariable String fileName, HttpServletResponse response){
        Resource resource = resourceService.getResourceByFilename(fileName);
        DownloadUtil.httpDownload(new File(this.files, fileName), resource.getOriginFilename(), response);
    }

}
