package com.eyas.framework.controller;


import com.eyas.framework.entity.UserEntity;
//import com.eyas.framework.sharding.UserEntityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/sharding")
public class ShardingController {

//    @Resource
//    private UserEntityDao userEntityDao;
//
//    @GetMapping("/userDao")
//    public void aa(){
//        for (int i=0;i<10;i++){
//            UserEntity userEntity = new UserEntity();
//            userEntity.setUserName(String.valueOf(i));
//            this.userEntityDao.insert(userEntity);
//        }
//
//    }

}
