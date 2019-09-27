package com.eyas.framework.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.eyas.framework.data.EyasFrameworkResult;
import com.eyas.framework.enumeration.ErrorFrameworkCodeEnum;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Created by yixuan on 2019/7/11.
 */
@ControllerAdvice
public class GloablExceptionHandler {
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e) {
        String msg = e.getMessage();
        if (msg == null || "".equals(msg)) {
            msg = "服务器出错";
        }

        return EyasFrameworkResult.fail(ErrorFrameworkCodeEnum.LOGIN_ERROR.getErrCode(), msg);
    }
}
