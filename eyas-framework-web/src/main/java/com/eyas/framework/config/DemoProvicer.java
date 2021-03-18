package com.eyas.framework.config;

import com.eyas.framework.data.TokenInfo;
import com.eyas.framework.data.UserInfo;
import com.eyas.framework.intf.DatabaseService;
import com.eyas.framework.provider.UserProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DemoProvicer implements UserProvider {

    private final DatabaseService databaseService;

    @Override
    public UserInfo getUserInfo(String userId, TokenInfo tokenInfo) {
        return databaseService.getInfo();
    }
}
