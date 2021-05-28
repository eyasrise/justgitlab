package com.eyas.framework.provide;

import com.eyas.framework.data.EyasFrameworkDo;
import com.eyas.framework.data.EyasFrameworkDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TenantCodeFillHandler implements FillHandler {

    private final TenantCodeProvider tenantCodeProvider;

    @Override
    public <Dto extends EyasFrameworkDto, D extends EyasFrameworkDo> void insertFill(Dto dto, Class<D> entityClass) {
        if (dto.getTenantCode() == null) {
            dto.setTenantCode(tenantCodeProvider.tenantCode());
        }
    }
}
