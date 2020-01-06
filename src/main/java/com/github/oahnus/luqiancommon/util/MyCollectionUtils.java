package com.github.oahnus.luqiancommon.util;

import lombok.Data;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by oahnus on 2019-11-20
 * 10:57.
 */
public class MyCollectionUtils {
    /**
     * 将list groupBy 为map
     * @param entityList list
     * @param keyPropName groupBy 依据的属性 名称
     * @param keyPropClass groupBy 依赖的属性 类
     * @param <K>
     * @param <E>
     * @return
     */
    public static <K, E> Map<K, List<E>> groupList2Map(List<E> entityList, String keyPropName, Class<K> keyPropClass) {
        Map<K, List<E>> map = new HashMap<>();
        if (entityList == null || entityList.isEmpty()) {
            return map;
        }

        for (E entity : entityList) {
            Field field = getField(entity, keyPropName, keyPropClass);
            if (field == null) {
                throw new RuntimeException(String.format("Class %s Not Contains Prop %s", keyPropClass, keyPropName));
            }
            field.setAccessible(true);
            try {
                K key = (K) field.get(entity);
                List<E> list = map.get(key);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(entity);
                map.put(key, list);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(String.format("Could Not Get Prop %s Value From Class %s", keyPropName, keyPropClass));
            }
        }
        return map;
    }

    /**
     * 将 list 转换为 指定key 对应的map
     * @param entityList list
     * @param keyPropName entity中作为key的属性名
     * @param keyPropClass key 属性的class
     * @param <K>
     * @param <E>
     * @return
     */
    public static <K, E> Map<K, E> convertList2Map(List<E> entityList, String keyPropName, Class<K> keyPropClass) {
        Map<K, E> map = new HashMap<>();
        if (entityList == null || entityList.isEmpty()) {
            return map;
        }

        for (E entity : entityList) {
            Field field = getField(entity, keyPropName, keyPropClass);
            if (field == null) {
                throw new RuntimeException(String.format("Class %s Not Contains Prop %s", keyPropClass, keyPropName));
            }
            field.setAccessible(true);
            try {
                K key = (K) field.get(entity);
                map.put(key, entity);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(String.format("Could Not Get Prop %s Value From Class %s", keyPropName, keyPropClass));
            }
        }
        return map;
    }

    private static Field getField(Object obj, String propName, Class clazz) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(propName) && field.getType().equals(clazz)) {
                return field;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        @Data
        class A {
            private String attr1;
            private Integer attr2;

            public A() { }

            public A(String attr1, Integer attr2) {
                this.attr1 = attr1;
                this.attr2 = attr2;
            }
        }
        List<A> list = Arrays.asList(
                new A("a1", 1),
                new A("a2", 2),
                new A("a3", 3),
                new A("a3", 4)
        );

        Map<String, A> map = convertList2Map(list, "attr1", String.class);
        System.out.println(map);

        Map<String, List<A>> groupMap = groupList2Map(list, "attr1", String.class);
        System.out.println(groupMap);
    }
}
