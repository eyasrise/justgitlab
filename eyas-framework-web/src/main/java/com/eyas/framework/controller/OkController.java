package com.eyas.framework.controller;

import com.eyas.framework.BigDecimalUtil;
import com.eyas.framework.DateUtil;
import com.eyas.framework.JsonUtil;
import com.eyas.framework.annotation.WithOutToken;
import com.eyas.framework.data.EyasFrameworkResult;
import com.eyas.framework.entity.UserEntity;
import com.eyas.framework.entity.UserEntityQuery;
import com.eyas.framework.service.intf.EyasFrameworkConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author Created by yixuan on 2019/7/8.
 */
@Controller
@RequestMapping("/api")
public class OkController {

    @Autowired
    private EyasFrameworkConfigService eyasFrameworkConfigService;

    @GetMapping("/ok")
    @WithOutToken
    public String ok(){
        return "ok!";
    }

    @GetMapping("config/{key}")
    public String config(@PathVariable String key){
        return this.eyasFrameworkConfigService.getAreaConfigValue(key);
    }

    @GetMapping("/testConverter")
    @ResponseBody
    public EyasFrameworkResult aa(){
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1234567890123456712L);
        userEntity.setCode("1212212");
        userEntity.setCreateTime(new Date());
        userEntity.setPayAmount(BigDecimalUtil.bigDecimalTransformation("12.21"));
        userEntity.setEmail(DateUtil.getCurrentDateTime());
        return EyasFrameworkResult.ok(userEntity);
    }
    @GetMapping("/testPage")
    @ResponseBody
    public EyasFrameworkResult testPage(){
        UserEntityQuery userEntityQuery = new UserEntityQuery();
        userEntityQuery.setUserName("121221");
        userEntityQuery.setTotalRecord(50);
        userEntityQuery.setPageSize(10);
        return EyasFrameworkResult.ok(userEntityQuery, userEntityQuery);
    }


    public static void main(String[] args) {
        UserEntityQuery userEntityQuery = new UserEntityQuery();
        userEntityQuery.setUserName("121221");
        userEntityQuery.setTotalRecord(50);
        userEntityQuery.setPageSize(10);
        System.out.println(JsonUtil.toJson(userEntityQuery));
        System.out.println(JsonUtil.toJson(userEntityQuery.getPageSize()));
        System.out.println(JsonUtil.toJson(userEntityQuery.getCurrentPage()));
        System.out.println(JsonUtil.toJson(userEntityQuery.getPageTotal()));
        System.out.println("--"+JsonUtil.toJson(EyasFrameworkResult.ok(1212, userEntityQuery)));
    }
}
