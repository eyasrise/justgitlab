package com.eyas.framework.provide;

import cn.hutool.core.date.DateUtil;
import com.eyas.framework.data.EyasFrameworkDo;
import com.eyas.framework.data.EyasFrameworkDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class AuditFillHandler implements FillHandler {

    private final AuditProvider auditProvider;

    @Override
    public <Dto extends EyasFrameworkDto, D extends EyasFrameworkDo> void insertFill(Dto dto, Class<D> entityClass) {
        if (StringUtils.isEmpty(dto.getCreator())) {
            dto.setCreator(auditProvider.creator());
        }
        LocalDateTime createTime = auditProvider.createTime();
        if (dto.getCreateTime() == null && createTime != null) {
            dto.setCreateTime(DateUtil.date(createTime));
        }
    }
}
