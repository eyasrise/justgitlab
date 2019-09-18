package com.eyas.framework;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Created by yixuan on 2018/12/27.
 */
@Slf4j
public class ListUtil {

    /**
     * 将list进行拆分，可以用于批量更新
     *
     * @param strList     用于拆分的集合
     * @param splitNumber 拆个数
     * @return 拆分后的多个子集合
     */
    public static <T> List<List<T>> splitList(List<T> strList, Integer splitNumber) {
        List<List<T>> lists = new ArrayList<>();
        List<T> list = new ArrayList<>();
        int i = 0;
        int j = 0;
        if (EmptyUtil.isEmpty(strList)) {
            return lists;
        }
        for (T t : strList) {
            i++;
            j++;
            list.add(t);
            if (i % splitNumber == 0 || j == strList.size()) {
                lists.add(list);
                list = new ArrayList<>();
                i = 0;
            }
        }
        return lists;
    }

    /**
     * 获取一个list前几条的数据
     *
     * @param tList 集合
     * @param index 条数
     * @param <T> 泛型
     * @return list
     */
    public static <T> List<T> getIndexList(List<T> tList, Integer index) {
        List<T> tListNew = new ArrayList<>();
        for (T t : tList) {
            if (index != 0) {
                tListNew.add(t);
                index--;
            } else {
                break;
            }
        }
        return tListNew;
    }

    /**
     * list<dto> 转换成单条对象
     *
     * @param tList 集合
     * @param <T> 泛型
     * @return 对象
     */
    public static <T> T listDtoToDto(List<T> tList) {
        if (EmptyUtil.isNotEmpty(tList) || tList.size() > 0) {
            return tList.get(0);
        }
        return null;
    }

}
