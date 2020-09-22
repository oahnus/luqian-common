package com.github.oahnus.luqiancommon.util;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by oahnus on 2019-11-20
 * 10:57.
 */
public class MyCollectionUtils {
    /**
     * 将list groupBy 为map
     * @param entities entity collection
     * @param keyPropName groupBy 依据的属性 名称
     * @param keyPropClass groupBy 依赖的属性 类
     * @param <K> Key Class
     * @param <E> Entity Class
     * @return Map
     */
    public static <K, E> Map<K, List<E>> groupList2Map(Collection<E> entities, String keyPropName, Class<K> keyPropClass) {
        Map<K, List<E>> map = new HashMap<>();
        if (entities == null || entities.isEmpty()) {
            return map;
        }

        for (E entity : entities) {
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
     * @param entities list
     * @param keyPropName entity中作为key的属性名
     * @param keyPropClass key 属性的class
     * @param <K> Key Class
     * @param <E> Entity Class
     * @return Map
     */
    public static <K, E> Map<K, E> convertList2Map(Collection<E> entities, String keyPropName, Class<K> keyPropClass) {
        Map<K, E> map = new HashMap<>();
        if (entities == null || entities.isEmpty()) {
            return map;
        }

        for (E entity : entities) {
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
}
