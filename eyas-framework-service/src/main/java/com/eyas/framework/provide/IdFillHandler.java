package com.eyas.framework.provide;

import com.eyas.framework.SnowflakeIdWorker;
import com.eyas.framework.config.EyasFrameworkProperties;
import com.eyas.framework.data.EyasFrameworkDo;
import com.eyas.framework.data.EyasFrameworkDto;
import org.springframework.beans.factory.annotation.Autowired;

public class IdFillHandler implements FillHandler {

    @Autowired
    private EyasFrameworkProperties eyasFrameworkProperties;

    @Override
    public <Dto extends EyasFrameworkDto, D extends EyasFrameworkDo> void insertFill(Dto dto, Class<D> entityClass) {
        if (dto.getId() != null) {
            if (eyasFrameworkProperties.getService().getIdType() == EyasFrameworkProperties.ServiceConfig.IDType.SNOWFLAKE) {
                //TODO 多节点高并发有冲突 可传入参数解决 也可以参照mp的根据设备标识来区分不同的数据中心
                dto.setId(SnowflakeIdWorker.generateId());
            }
        }
    }
}
