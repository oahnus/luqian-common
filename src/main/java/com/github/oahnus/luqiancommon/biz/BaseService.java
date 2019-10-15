package com.github.oahnus.luqiancommon.biz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * Created by oahnus on 2019/10/8
 * 11:16.
 *
 * T Entity
 * K Entity Id Type
 */
public class BaseService<M extends Mapper<T>, T, K> {
    @Autowired
    protected M mapper;

    public T selectOne(T ex) {
        return mapper.selectOne(ex);
    }

    public List<T> selectByExample(Example ex) {
        return mapper.selectByExample(ex);
    }

    public T selectOneByExample(Example ex) {
        return mapper.selectOneByExample(ex);
    }

    public T selectById(K id) {
        Type idType = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[2];
        Class<K> clazz = (Class<K>) idType;

        if (clazz.equals(Integer.class)) {
            Integer zId = Integer.valueOf(id.toString());
            return mapper.selectByPrimaryKey(zId);
        }
        if (clazz.equals(Long.class)) {
            Long zId = Long.valueOf(id.toString());
            return mapper.selectByPrimaryKey(zId);
        }
        return mapper.selectByPrimaryKey(id);
    }

    @SuppressWarnings("unchecked")
    public List<T> selectByIdIn(List<K> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return Collections.emptyList();
        }
        Type[] actualTypeArguments = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        Type actualTypeArgument = actualTypeArguments[1];
        Class<T> clazz = (Class<T>) actualTypeArgument;

        Example ex = new Example(clazz);
        Example.Criteria criteria = ex.createCriteria();
        criteria.andIn("id", idList);
        return mapper.selectByExample(ex);
    }

    public void insertSelective(T t) {
        mapper.insertSelective(t);
    }

    public void updateSelective(T t) {
        mapper.updateByPrimaryKeySelective(t);
    }

    public void updateById(T t) {
        mapper.updateByPrimaryKey(t);
    }

    public void removeById(Serializable id) {
        mapper.deleteByPrimaryKey(id);
    }
}
