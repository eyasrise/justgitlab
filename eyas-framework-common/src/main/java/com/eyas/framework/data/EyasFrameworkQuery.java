package com.eyas.framework.data;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Created by yixuan on 2019/6/20.
 */
@Data
public class EyasFrameworkQuery extends EyasFrameworkBaseQuery implements Serializable {

    private static final long serialVersionUID = -7779110672848186411L;

    /**
     * 行号
     */
    private Long id;

    /**
     * 业务主键
     */
    private String code;

    /**
     * 数据业务状态
     */
    private Integer status;

    /**
     * 数据类型
     */
    private Integer type;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改人
     */
    private String operator;

    /**
     * 备注
     */
    private String remark;

    // 业务字段

    /**
     * id多值
     */
    private List<String> ids;

    /**
     * status多值
     */
    private String statusStr;

    /**
     * status多值
     */
    private List<String> statusInt;

    /**
     * 备用字段
     */
    private String extAtt;

    /**
     * 项目编码code
     */
    private Long projectCode;
}