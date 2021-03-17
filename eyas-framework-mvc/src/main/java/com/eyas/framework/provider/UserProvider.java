package com.eyas.framework.provider;

import com.eyas.framework.interceptor.TokenInfo;
import com.eyas.framework.interceptor.UserInfo;

/**
 * 获取用户信息的提供者
 * 具体项目可定义自己的实现
 */
@FunctionalInterface
public interface UserProvider {

    /**
     * 如果返回不为null 则代码有用户
     * 建议尽量填充{@link UserInfo}属性
     * 例如:
     * {@link UserInfo#setUserId(String)}
     * {@link UserInfo#setTenantCode(Long)}} 无租户可不设置
     * {@link UserInfo#setSystemUser(Object)}
     * 方便以后统一封装扩展
     *
     * @param userId    暂token 中jwt的id
     * @param tokenInfo 各种token中的信息
     * @return 用户信息统一包装体
     */
    UserInfo getUserInfo(String userId, TokenInfo tokenInfo);

}
