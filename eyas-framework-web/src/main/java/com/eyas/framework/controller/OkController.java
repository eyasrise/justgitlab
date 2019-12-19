package com.eyas.framework.controller;

import com.eyas.framework.JsonUtil;
import com.eyas.framework.data.EyasFrameworkResult;
import com.eyas.framework.entity.UserEntity;
import com.eyas.framework.entity.UserEntityQuery;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Created by yixuan on 2019/7/8.
 */
@RestController
@RequestMapping(value = "/hello")
public class OkController {


    @GetMapping("/ok")
    public String ok(){
        return "ok!";
    }

//    @GetMapping("config/{key}")
//    public String config(@PathVariable String key){
//        return this.eyasFrameworkConfigService.getAreaConfigValue(key);
//    }


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

    @ApiOperation(value="获取用户列表", notes="")
    @GetMapping(value={"/swagger"})
    public EyasFrameworkResult<List<UserEntity>> getUserList() {
        List<UserEntity> r = new ArrayList<>();
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("1212");
        r.add(userEntity);
        return EyasFrameworkResult.ok(r);
    }

}
