package com.eyas.framework.interceptor;

import com.eyas.framework.data.EyasFrameworkResult;
import com.eyas.framework.enumeration.ErrorFrameworkCodeEnum;
import com.eyas.framework.exception.EyasFrameworkRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Created by yixuan on 2019/7/11.
 */
@ControllerAdvice
@Slf4j
public class GloablExceptionHandler {
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e) {
        String msg = e.getMessage();
        if (msg == null || "".equals(msg)) {
            msg = "服务器出错";
        }
        log.info(ErrorFrameworkCodeEnum.SYSTEM_ERROR.getErrCode() + "-" + msg);
        return EyasFrameworkResult.fail(ErrorFrameworkCodeEnum.SYSTEM_ERROR.getErrCode(), msg);
    }

    @ResponseBody
    @ExceptionHandler(EyasFrameworkRuntimeException.class)
    public Object handleException1(EyasFrameworkRuntimeException e) {
        String msg = e.getMsg();
        if (msg == null || "".equals(msg)) {
            msg = "服务器出错";
        }
        log.info(e.getCode() + "-" + msg);
        return EyasFrameworkResult.fail(e.getCode(), e.getMsg());
    }
}