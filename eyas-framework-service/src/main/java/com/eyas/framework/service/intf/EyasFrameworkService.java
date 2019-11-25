package com.eyas.framework.service.intf;

import com.eyas.framework.data.EyasFrameworkBaseQuery;

import java.util.List;

/**
 * @author Created by yixuan on 2019/1/7.
 */
public interface EyasFrameworkService<Dto, Q> {

    /**
     * 添加
     *
     * @param dto 对象do
     * @return 1
     */
    Integer insert(Dto dto);

    /**
     * 修改
     *
     * @param dto 对象do
     * @return 1
     */
    Integer update(Dto dto);

    /**
     * 删除
     *
     * @param id 被删除对象id
     * @return 1
     */
    Integer deleteById(Long id);

    /**
     *
     *
     * @param d
     * @return
     */
    Integer delete(Dto d);

    /**
     * 查询
     *
     * @param q 对象query
     * @return 对象组
     */
    List<Dto> query(Q q);

    /**
     * 通用多值查询
     *
     * @param q
     * @return
     */
    List<Dto> queryStatusStr(Q q);

    /**
     * 查询记录数
     *
     * @param q 对象query
     * @return 记录数
     */
    Integer queryCount(Q q);

    /**
     * 批量更新
     *
     * @param dto 对象dto
     * @return 更新记录数
     */
    Integer batchUpdate(Dto dto);

    /**
     * 批量删除
     *
     * @param dto 对象dto
     * @return 删除记录数
     */
    Integer batchDelete(Dto dto);

    /**
     * 更新数据-先删除再更新
     * 复杂的逻辑可以先执行删除操作，再执行新增逻辑
     *
     * @param dto 对象dto
     * @return 更新记录数
     */
    Integer updateByDelete(Dto dto);

    /**
     * 根据不同条件查询数据
     *
     * @param q q
     * @return dtoList
     */
    List<Dto> queryByDifferentConditions(Q q);

    /**
     * 根据id查询数据
     *
     * @param id id
     * @return Dto
     */
    Dto getInfoById(Long id);

    /**
     * 根据id查询数据-用于乐观锁
     *
     * @param dto dto
     * @return dto
     */
    Dto getInfoById(Dto dto);

    /**
     * 更新数据-带乐观锁
     *
     * @param dto
     * @return
     */
    Integer updateById(Dto dto);

    /**
     *
     */
    Integer batchInsert(List<Dto> dtoList);
}
