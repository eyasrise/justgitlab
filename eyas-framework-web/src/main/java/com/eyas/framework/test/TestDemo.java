package com.eyas.framework.test;

import com.eyas.framework.GsonUtil;
import io.swagger.models.auth.In;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDemo {

    int count = 0;


    public static void main(String[] args) {

        List<Integer> integerList = new ArrayList<>();
        integerList.add(1);
        integerList.add(3);
        integerList.add(6);
        integerList.add(8);
        integerList.add(10);
        integerList.add(11);
        integerList.add(20);
        integerList.add(30);
        integerList.add(50);

        TestDemo testDemo = new TestDemo();

        testDemo.dealDate(3, integerList.size(), integerList);

    }

    private void dealDate(Integer data, Integer size, List<Integer> list){
        // 9/2 = 4
        // 11>10走右边
        // 4+9 = 13/2 = 6
        // 11 < 20
        // if((6-4)/2 == 1 ){
        // 直接查询

        // }
        // 获取数组长度
        // 折半处理
        int centerIndex = list.size() /2;

        Integer halfValue = size/2;
        count ++ ;
        if (data < list.get(halfValue)){
            // 递归前判断一下
            if ((halfValue-centerIndex) / 2 == 1){
                // 直接查找
                for (int j = centerIndex ; j< halfValue ; j++){
                    if(data == list.get(j)){
                        System.out.println(data + "数组下标位置:" + halfValue);
                        System.out.println("查询次数:" + count);
                    }
                }
            }else {
                dealDate(data, halfValue, list);
            }
        }else if (data > list.get(halfValue)){
            if (halfValue / 2 == 1){
                // 直接查找
                for (int j = halfValue ; j< centerIndex ; j++){
                    if(data == list.get(j)){
                        System.out.println(data + "数组下标位置:" + halfValue);
                        System.out.println("查询次数:" + count);
                    }
                }
            }else {
                dealDate(data, halfValue + list.size(), list);
            }
        }
    }

}
