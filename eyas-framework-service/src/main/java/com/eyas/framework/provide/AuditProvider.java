package com.eyas.framework.provide;

import java.time.LocalDateTime;

@FunctionalInterface
public interface AuditProvider {

    String creator();

    default LocalDateTime createTime() {
        return null;
    }

}
