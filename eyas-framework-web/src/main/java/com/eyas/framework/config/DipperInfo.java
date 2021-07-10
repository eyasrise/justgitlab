package com.eyas.framework.config;

import org.apache.ibatis.annotations.*;

import java.util.List;

public interface DipperInfo {

    @Select("select ID,CODE,STATUS,TYPE,TENANT_CODE from dipper_info")
    List<DipperInfoDTO> queryAll();

    @Insert("insert into dipper_info (CODE, STATUS, TENANT_CODE) VALUES (#{code}, #{status}, #{tenantCode} )")
    Integer insert(DipperInfoDTO dipperInfoDTO);


    @Update("update dipper_info set status = #{status} where id = #{id}")
    Integer update(DipperInfoDTO dipperInfoDTO);

    @Delete("delete from dipper_info where id = #{id}")
    Integer delete(DipperInfoDTO dipperInfoDTO);

    @Insert({
    "<script>",
    "insert into dipper_info(CODE, STATUS, create_time, update_time, TENANT_CODE) values ",
    "<foreach collection='testLists' item='item' index='index' separator=','>",
    "(#{item.code}, #{item.status}, now(), now(), #{item.tenantCode})",
    "</foreach>",
    "</script>"
    })
    int batchInsert(@Param(value="testLists") List<DipperInfoDTO> dipperInfoDTOList);


}
