package com.iecube.ota.BaseController;

import com.iecube.ota.exception.DeleteException;
import com.iecube.ota.exception.InsertException;
import com.iecube.ota.exception.ServiceException;
import com.iecube.ota.exception.UpdateException;
import com.iecube.ota.model.resource.service.ex.*;
import com.iecube.ota.utils.JsonResult;
import com.iecube.ota.utils.ex.SystemException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpSession;

public class BaseController {
    /**
     * 获取session对象的id
     * @param session session对象
     * @return 当前登录用户的userid
     */
    public final Integer getUserIdFromSession(HttpSession session){
        return Integer.valueOf(session.getAttribute("userid").toString());
    }

    /**
     * 获取session对象的username
     * @param session session 对象
     * @return 当前登录用户的username
     */
    public final String getUsernameFromSession(HttpSession session){
        return session.getAttribute("username").toString();
    }

    public final String getUserTypeFromSession(HttpSession session){
        return session.getAttribute("type").toString();
    }

    public static final int OK=200;

    @ExceptionHandler({FileUploadException.class, SizeLimitExceededException.class, ServiceException.class}) //用于统一处理抛出的异常
    public JsonResult<Void> handleException(Throwable e){
        JsonResult<Void> result = new JsonResult<>(e);
        if (e instanceof ServiceException) {
            result.setState(8000);
        } else if(e instanceof InsertException){
            result.setState(8001);
        } else if (e instanceof UpdateException) {
            result.setState(8002);
        } else if (e instanceof DeleteException) {
            result.setState(8003);
        } else if (e instanceof SystemException) {
            result.setState(8004);
            result.setMessage("系统错误");
        } else if(e instanceof ResourceNotFoundException) {
            result.setState(8010);
        }else if (e instanceof FileCreateFailedException){
            result.setState(8011);
        }else if (e instanceof FileEmptyException){
            result.setState(8012);
            result.setErrno(1);
        }else if (e instanceof FileSizeException){
            result.setState(8013);
        }else if (e instanceof FileTypeException){
            result.setState(8014);
        }else if (e instanceof SizeLimitExceededException){
            result.setState(8015);
            result.setMessage("文件太大，控制单个文件小于1GB");
        }
        return result;
    }
}
