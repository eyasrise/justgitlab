package com.eyas.framework;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
     * 将list进行拆分，可以用于批量更新，去空
     *
     * @param strList     用于拆分的集合
     * @param splitNumber 拆个数
     * @param removeNull 是否需要进行空数据移除
     * @return 拆分后的多个子集合
     */
    public static <T> List<List<T>> splitList(List<T> strList, Integer splitNumber, boolean removeNull) {
        strList.removeIf(Objects::isNull);
        return ListUtil.splitList(strList, splitNumber);
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

    /**
     * 数组长度动态切割算法
     * 默认数组长度小于粒度*10或者；粒度*10 < 1000--默认最多一个数组放100个
     * 超过粒度的10倍走动态切割，就是步长固定，为了预防数组长度过大，数组过多的问题。
     *
     * @param tList 切割list
     * @param <T> 切割数据返回多list集合
     * @return List<List<T>> 切割以后的数据
     * v3-2022-07-12
     * 扩展因子固定为10
     */
    public static <T> List<List<T>> getListLengthDynamicExpansion(List<T> tList){

        // 粒度判断-粒度不应该超过整体长度粒度的一半，比如1000的长度你的粒度最好不能超过100
        // 粒度开根号处理
        int size = tList.size();
        double sqrtDouble = Math.sqrt(size);
        int sqrtInt = (int) sqrtDouble;
        int specialLength = size/sqrtInt;
        return ListUtil.splitList(tList, specialLength);
    }


    public static void main(String[] args) {

        List<Integer> aa = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            aa.add(i);
        }
        double bb = Math.sqrt(aa.size());
        int ss = (int) bb;
        List<List<Integer>> aallist =  ListUtil.getListLengthDynamicExpansion(aa);
        for (int i = 0; i < aallist.size(); i++) {
            System.out.println(i + "----->" + aallist.get(i));
        }
    }

}
