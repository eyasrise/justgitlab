package com.eyas.framework.entity;

import com.eyas.framework.data.EyasFrameworkDo;
import lombok.Data;

/**
 * @author Created by yixuan on 2019/7/11.
 */
@Data
public class UserEntity extends EyasFrameworkDo {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 电话
     */
    private String phone;

    /**
     * email
     */
    private String email;

}
