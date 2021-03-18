package com.eyas.framework.impl;

import com.eyas.framework.data.UserInfo;
import com.eyas.framework.intf.DatabaseService;
import org.springframework.stereotype.Service;

@Service
public class DatabaseServiceImpl implements DatabaseService {
    @Override
    public UserInfo getInfo() {

        return UserInfo.builder().tenantCode(300l).userCode("xs33333").build();
    }
}
