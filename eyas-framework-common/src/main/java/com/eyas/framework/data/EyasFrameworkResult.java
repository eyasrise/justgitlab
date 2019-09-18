package com.eyas.framework.data;

import com.eyas.framework.exception.EyasFrameworkRuntimeException;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Created by yixuan on 2019/6/20.
 */
@Data
public class EyasFrameworkResult<T> implements Serializable {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 返回数据
     */
    private T data;

    private EyasFrameworkBaseQuery eyasFrameworkBaseQuery;

    /**
     * 错误码
     */
    private String errCode;

    /**
     * 错误信息
     */
    private String errMsg;

    public static EyasFrameworkResult ok(){
        return ok(null);
    }

    public static <T> EyasFrameworkResult ok(T data){
        EyasFrameworkResult<T> result = new EyasFrameworkResult<>();
        result.setData(data);
        result.setSuccess(true);
        return result;
    }

    public static <T> EyasFrameworkResult ok(T data, EyasFrameworkBaseQuery eyasFrameworkBaseQuery){
        EyasFrameworkResult<T> result = new EyasFrameworkResult<>();
        result.setData(data);
        result.setEyasFrameworkBaseQuery(eyasFrameworkBaseQuery);
        result.setSuccess(true);
        return result;
    }

    public static <T> EyasFrameworkResult okFrame(T data){
        EyasFrameworkResult<T> result = new EyasFrameworkResult<>();
        result.setData(data);
        result.setSuccess(true);
        return result;
    }

    public static EyasFrameworkResult fail(String errCode, String errMsg){
        EyasFrameworkResult result = new EyasFrameworkResult();
        result.setSuccess(false);
        result.setErrCode(errCode);
        result.setErrMsg(errMsg);
        return result;
    }

    public static EyasFrameworkResult fail(EyasFrameworkRuntimeException e){
        return fail(e.getCode(), e.getMsg());
    }
}
