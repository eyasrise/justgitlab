package com.eyas.framework.dao;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Created by yixuan on 2019/1/7.
 */
@Repository
public interface EyasFrameworkDao<D, Q> {

    /**
     * 单条添加数据
     *
     * @param d 对象do
     * @return 成功:1 失败:0
     */
    Integer insert(D d);

    /**
     * 批量新增数据
     *
     * @param dList 对象集合
     * @return 新增条数
     */
    Integer batchInsert(List<D> dList);

    /**
     * 更新数据——带乐观锁
     *
     * @param d 对象do
     * @return 1
     */
    Integer update(D d);

    /**
     * 更新数据——不带乐观锁
     *
     * @param d 对象do
     * @return 1
     */
    Integer updateNoLock(D d);

    /**
     * 根据id删除数据——逻辑删除
     *
     * @param id 被删除对象id
     * @return 1
     */
    Integer deleteById(Long id);

    /**
     * 通过不同条件删除数据
     *
     * @param d 对象
     * @return 1
     */
    Integer delete(D d);

    /**
     * 查询-分页查询
     *
     * @param q 对象query
     * @return 对象组
     */
    List<D> query(Q q);

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
     * @param d 对象do
     * @return 更新记录数
     */
    Integer batchUpdate(D d);

    /**
     * 批量删除
     *
     * @param d 对象do
     * @return 删除记录数
     */
    Integer batchDelete(D d);

    /**
     * 业务查询-根据不同的条件查询数据
     *
     * @param q q
     * @return dList
     */
    List<D> queryByDifferentConditions(Q q);

    /**
     * 根据id查询数据
     *
     * @param id id
     * @return D
     */
    D getInfoById(Long id);

    /**
     * 根据id查询数据-用于乐观锁
     *
     * @param d d
     * @return D
     */
    D getInfoById(D d);
}
