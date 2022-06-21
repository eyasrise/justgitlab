package com.eyas.framework.service.impl;

import com.eyas.framework.EmptyUtil;
import com.eyas.framework.ListUtil;
import com.eyas.framework.enumeration.ErrorFrameworkCodeEnum;
import com.eyas.framework.exception.EyasFrameworkRuntimeException;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 模板方法模式抽象
 * 架构业务服务抽象类
 * 做子类共有方法逻辑的抽象
 * --逻辑说明
 * 1、父类描述基本的方法抽象模板，跟逻辑方法抽象模板，
 * 要求子类必须覆写方法抽象模板的具体实现，逻辑有父类处理
 * 2、如果父类抽象模板不满足子类要求，可以自己实现方法具体实现
 */
public abstract class EyasFrameworkAbstractService<Dto,D,Q> {

    private D dtoToD(Dto dto){
        Class<D> entityClass = (Class<D>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        D d;
        try {
            d = entityClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new EyasFrameworkRuntimeException(ErrorFrameworkCodeEnum.NEW_INSTANCE_ERROR, "泛型创建对象有误!");
        }
        if(EmptyUtil.isNotEmpty(dto)) {
            BeanUtils.copyProperties(dto, d);
        }else{
            return null;
        }
        return d;
    }

    private Dto dToDto(D d){
        Class<Dto> entityClass = (Class<Dto>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        Dto dto;
        try {
            dto = entityClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new EyasFrameworkRuntimeException(ErrorFrameworkCodeEnum.NEW_INSTANCE_ERROR, "泛型创建对象有误!");
        }
        if(EmptyUtil.isNotEmpty(d)) {
            BeanUtils.copyProperties(d, dto);
        }else{
            return null;
        }
        return dto;
    }


    /**
     *
     */
    public Integer batchUpdate(List<Dto> dtoList, Integer splitNumber){
        List<D> dList = new ArrayList<>();
        dtoList.stream().forEach(dto -> {
            D d = this.dtoToD(dto);
            dList.add(d);
        });
        List<List<D>> ddList = ListUtil.splitList(dList, splitNumber);
        AtomicInteger cnt = new AtomicInteger();
        cnt.set(0);
        ddList.stream().forEach(ds -> {
            cnt.set(cnt.get() + this.middleInsert(dList));
        });
        return cnt.get();
    }

    public abstract Integer middleInsert(List<D> dList);
}
