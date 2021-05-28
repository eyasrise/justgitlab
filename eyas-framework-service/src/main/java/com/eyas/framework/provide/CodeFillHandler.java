package com.eyas.framework.provide;

import com.eyas.framework.SnowflakeIdWorker;
import com.eyas.framework.annotation.Entity;
import com.eyas.framework.data.EyasFrameworkDo;
import com.eyas.framework.data.EyasFrameworkDto;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CodeFillHandler implements FillHandler {

    @Override
    public <Dto extends EyasFrameworkDto, D extends EyasFrameworkDo> void insertFill(Dto dto, Class<D> entityClass) {
        if (StringUtils.isEmpty(dto.getCode())) {
            String codePre;
            Entity entityAnnotation = entityClass.getAnnotation(Entity.class);
            if (entityAnnotation != null) {
                codePre = entityAnnotation.codeIdentifier();
            } else {
                String name = entityClass.getSimpleName();
                String replace = StringUtils.removeEndIgnoreCase(name, "DO");
                codePre = Arrays.stream(ArrayUtils.toObject(replace.toCharArray())).filter(Character::isUpperCase).map(Object::toString).collect(Collectors.joining());
            }
            dto.setCode(codePre + SnowflakeIdWorker.generateId());
        }
    }
}
