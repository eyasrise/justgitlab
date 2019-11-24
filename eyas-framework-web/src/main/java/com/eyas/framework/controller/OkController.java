package com.eyas.framework.controller;

import com.eyas.framework.JsonUtil;
import com.eyas.framework.annotation.WithOutToken;
import com.eyas.framework.data.EyasFrameworkResult;
import com.eyas.framework.entity.UserEntityQuery;
import com.eyas.framework.service.intf.EyasFrameworkConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * @author Created by yixuan on 2019/7/8.
 */
@RestController
@RequestMapping(value = "/api", produces = "text/plain;charset=UTF-8")
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
