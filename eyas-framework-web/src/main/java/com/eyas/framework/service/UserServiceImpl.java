package com.eyas.framework.service;

import com.eyas.framework.entity.UserEntity;
import com.eyas.framework.entity.UserEntityDo;
import com.eyas.framework.entity.UserEntityQuery;
import com.eyas.framework.service.impl.EyasFrameworkAbstractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl extends EyasFrameworkAbstractService<UserEntity, UserEntityDo, UserEntityQuery> {

    public void ceShi(UserEntityQuery userEntityQuery, List<UserEntity> userEntityList){
        log.info("ceShi");
        this.queryByDifferentConditions(userEntityQuery);
        this.batchUpdate(userEntityList,100);
    }
}
