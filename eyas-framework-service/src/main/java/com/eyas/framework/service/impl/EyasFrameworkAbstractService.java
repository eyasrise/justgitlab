package com.eyas.framework.service.impl;

import com.eyas.framework.EmptyUtil;
import com.eyas.framework.GsonUtil;
import com.eyas.framework.ListUtil;
import com.eyas.framework.enumeration.ErrorFrameworkCodeEnum;
import com.eyas.framework.exception.EyasFrameworkRuntimeException;
import com.eyas.framework.middle.EyasFrameworkMiddle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
 * 3？疑问能不能直接用接口去实现----不用抽象类
 * 4、架构方法实现模板方法模式没有意义-因为本身业务service相当于子类了，
 * 最多直接把架构service抽象成为super父类，没必要继续拆
 */
@Slf4j
public abstract class EyasFrameworkAbstractService<Dto,D,Q> {

    private EyasFrameworkMiddle<D,Q> eyasFrameworkMiddle;

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
     * 构建基本方法逻辑
     * 父类默认实现逻辑
     * 子类可以不用强制覆写
     */

    /**
     * 批量更新
     *
     * @param dtoList
     * @param splitNumber
     * @return
     */
    public Integer batchUpdate(List<Dto> dtoList, Integer splitNumber){
        log.info("dtoList" + GsonUtil.objectToJson(dtoList));
        List<D> dList = new ArrayList<>();
        dtoList.stream().forEach(dto -> {
            D d = this.dtoToD(dto);
            dList.add(d);
        });
        List<List<D>> ddList = ListUtil.splitList(dList, splitNumber);
        AtomicInteger cnt = new AtomicInteger();
        cnt.set(0);
        ddList.stream().forEach(ds -> {
            cnt.set(cnt.get() + this.eyasFrameworkMiddle.batchInsert(dList));
        });
        return cnt.get();
    }

    public List<Dto> queryByDifferentConditions(Q q){
        log.info("q" + GsonUtil.objectToJson(q));
        List<D> dList = this.eyasFrameworkMiddle.queryByDifferentConditions(q);
        List<Dto> dtoList = new ArrayList<>();
        dList.forEach(d->{
            Dto dto = this.dToDto(d);
            // dto转换
            if (EmptyUtil.isNotEmpty(dto)) {
                BeanUtils.copyProperties(d, dto);
            }
            dtoList.add(dto);
        });
        return dtoList;
    }

}
