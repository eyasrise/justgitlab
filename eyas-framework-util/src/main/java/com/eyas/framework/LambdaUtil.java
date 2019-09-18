package com.eyas.framework;

import lombok.Data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Created by yixuan on 2019/5/13.
 */
@Data
public class LambdaUtil {

    private Long id;

    private String code;

    /**
     * list去重算法
     *
     * @param tList 去重集合
     * @param keyExtractor 去重对象属性
     * @param <T> 泛型
     * @return
     */
    public static <T> List<T> distinct(List<T> tList, Function<? super T, Object> keyExtractor){
        //list是需要去重的list，返回值是去重后的list
        List<T> distinctTs = tList.stream().filter(distinctByKey(keyExtractor)).collect(Collectors.toList());
        return distinctTs;
    }

    /**
     * list分组算法
     *
     * @param tList 需要分组的集合
     * @param <T>   泛型
     * @return 分组的条件的值作为map的key，分组结果作为Value
     */
    public static <T,E> Map<E, List<T>> groupByToMap(List<T> tList, Function<? super T, E> keyExtractor) {
        return tList.stream().collect(Collectors.groupingBy(keyExtractor));
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return object -> seen.putIfAbsent(keyExtractor.apply(object), Boolean.TRUE) == null;
    }
}
