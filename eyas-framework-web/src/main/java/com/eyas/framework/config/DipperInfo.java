package com.eyas.framework.config;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface DipperInfo {

    @Select("select ID,CODE,STATUS,TYPE,TENANT_CODE from dipper_info")
    List<DipperInfoDTO> queryAll();

    @Insert("insert into dipper_info (ID, CODE, STATUS, TENANT_CODE) VALUES (#{id}, #{code}, #{status}, #{tenantCode} )")
    Integer insert(DipperInfoDTO dipperInfoDTO);


    @Update("update dipper_info set status = #{status} where id = #{id}")
    Integer update(DipperInfoDTO dipperInfoDTO);

    @Delete("delete from dipper_info where id = #{id}")
    Integer delete(DipperInfoDTO dipperInfoDTO);

}
