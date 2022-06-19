package com.eyas.framework.middle;

import com.eyas.framework.dao.EyasFrameworkDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Created by yixuan on 2019/1/17.
 */
@Component
public class EyasFrameworkMiddle<D, Q> {

    private final EyasFrameworkDao<D, Q> eyasFrameworkDao;

    public EyasFrameworkMiddle(EyasFrameworkDao<D, Q> eyasFrameworkDao) {
        this.eyasFrameworkDao = eyasFrameworkDao;
    }

    public Integer insert(D d) {
        return this.eyasFrameworkDao.insert(d);
    }

    public Integer batchInsert(List<D> dList) {
        return this.eyasFrameworkDao.batchInsert(dList);
    }


    /**
     * 修改
     *
     * @param d 对象do
     * @return 1
     */
    public Integer update(D d) {
        return this.eyasFrameworkDao.update(d);
    }

    /**
     * 修改
     *
     * @param d 对象do
     * @return 1
     */
    public Integer updateNoLock(D d) {
        return this.eyasFrameworkDao.updateNoLock(d);
    }


    /**
     * 删除
     *
     * @param id 被删除对象id
     * @return 1
     */
    public Integer deleteById(Long id) {
        return this.eyasFrameworkDao.deleteById(id);
    }

    /**
     * 通过不同条件删除数据
     *
     * @param d 对象
     * @return 1
     */
    public Integer delete(D d) {
        return this.eyasFrameworkDao.delete(d);
    }

    /**
     * 查询
     *
     * @param q 对象query
     * @return 对象组
     */
    public List<D> query(Q q) {
        return this.eyasFrameworkDao.query(q);
    }

    /**
     * 查询记录数
     *
     * @param q 对象query
     * @return 记录数
     */
    public Integer queryCount(Q q) {
        return this.eyasFrameworkDao.queryCount(q);
    }

    /**
     * 批量更新
     *
     * @param d 对象do
     * @return 更新记录数
     */
    public Integer batchUpdate(D d) {
        return this.eyasFrameworkDao.batchUpdate(d);
    }

    /**
     * 批量删除
     *
     * @param d 对象do
     * @return 删除记录数
     */
    public Integer batchDelete(D d) {
        return this.eyasFrameworkDao.batchDelete(d);
    }

    /**
     * 业务查询-根据不同的条件查询数据
     *
     * @param q q
     * @return dList
     */
    public List<D> queryByDifferentConditions(Q q) {
        return this.eyasFrameworkDao.queryByDifferentConditions(q);
    }

    /**
     * 业务查询-根据主键id查询数据
     *
     * @param id id
     * @return D
     */
    public D getInfoById(Long id) {
        return this.eyasFrameworkDao.getInfoById(id);
    }

    /**
     * 根据id查询数据-用于乐观锁
     *
     * @param d d
     * @return D
     */
    public D getInfoById(D d) {
        return this.eyasFrameworkDao.getInfoById(d);
    }
}
