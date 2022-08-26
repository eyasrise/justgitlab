package com.eyas.framework.entity;


import lombok.Data;

import java.util.Date;

@Data
public class DipperInfo {

  private long id;
  private String code;
  private long status;
  private long type;
  private Date createTime;
  private Date updateTime;
  private long rowLock;
  private long rowStatus;
  private String remark;
  private String creator;
  private String operator;
  private String extAtt;
  private String tenantCode;

}
