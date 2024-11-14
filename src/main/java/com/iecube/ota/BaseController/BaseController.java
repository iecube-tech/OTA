package com.iecube.ota.BaseController;

import com.iecube.ota.exception.DeleteException;
import com.iecube.ota.exception.InsertException;
import com.iecube.ota.exception.ServiceException;
import com.iecube.ota.exception.UpdateException;
import com.iecube.ota.model.Terminal.service.ex.SendMqttMessageException;
import com.iecube.ota.model.User.dto.CurrentUserDto;
import com.iecube.ota.model.resource.service.ex.*;
import com.iecube.ota.utils.FeiShu.AuthUtil;
import com.iecube.ota.utils.JsonResult;
import com.iecube.ota.utils.ex.SystemException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpSession;

public class BaseController {
    public static final int OK=200;

    public final CurrentUserDto currentUser(){
        return AuthUtil.getCurrentUser();
    }

    @ExceptionHandler({FileUploadException.class, SizeLimitExceededException.class, ServiceException.class}) //用于统一处理抛出的异常
    public JsonResult<Void> handleException(Throwable e){
        JsonResult<Void> result = new JsonResult<>(e);
         if(e instanceof InsertException){
            result.setState(8001);
            result.setMessage(e.getMessage());
        } else if (e instanceof UpdateException) {
            result.setState(8002);
            result.setMessage(e.getMessage());
        } else if (e instanceof DeleteException) {
            result.setState(8003);
            result.setMessage(e.getMessage());
        } else if (e instanceof SystemException) {
            result.setState(8004);
            result.setMessage("系统错误");
        } else if(e instanceof ResourceNotFoundException) {
            result.setState(8010);
            result.setMessage(e.getMessage());
        }else if (e instanceof FileCreateFailedException){
            result.setState(8011);
            result.setMessage(e.getMessage());
        }else if (e instanceof FileEmptyException){
            result.setState(8012);
            result.setErrno(1);
            result.setMessage(e.getMessage());
        }else if (e instanceof FileSizeException){
            result.setState(8013);
            result.setMessage(e.getMessage());
        }else if (e instanceof FileTypeException){
            result.setState(8014);
            result.setMessage(e.getMessage());
        }else if (e instanceof SizeLimitExceededException){
            result.setState(8015);
            result.setMessage("文件太大，控制单个文件小于1GB");
        } else if (e instanceof SendMqttMessageException) {
            result.setState(8016);
            result.setMessage(e.getMessage());
        } else if (e instanceof ServiceException) {
             result.setState(8000);
             result.setMessage(e.getMessage());
         }
        return result;
    }
}
