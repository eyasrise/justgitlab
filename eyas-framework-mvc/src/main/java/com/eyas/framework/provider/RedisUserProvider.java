package com.eyas.framework.provider;

import com.eyas.framework.interceptor.TokenInfo;
import com.eyas.framework.interceptor.UserInfo;
import com.eyas.framework.intf.RedisService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RedisUserProvider implements UserProvider {

    private final RedisService redisService;

    @Override
    public UserInfo getUserInfo(String userId, TokenInfo tokenInfo) {
        Object value = redisService.get(userId + tokenInfo.getTenantCode());
        if (value == null) {
            return null;
        }
        return UserInfo.builder().userId(userId).userCode(userId).tenantCode(tokenInfo.getTenantCode()).systemUser(value).build();
    }
}
