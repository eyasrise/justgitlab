package com.eyas.framework.provide;

import com.eyas.framework.data.EyasFrameworkDo;
import com.eyas.framework.data.EyasFrameworkDto;

public interface FillHandler {

    <Dto extends EyasFrameworkDto, D extends EyasFrameworkDo> void insertFill(Dto dto, Class<D> entityClass);

}
