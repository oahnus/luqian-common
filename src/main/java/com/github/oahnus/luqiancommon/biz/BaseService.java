package com.github.oahnus.luqiancommon.biz;

import com.github.oahnus.luqiancommon.mybatis.MyMapper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
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
public class BaseService<M extends MyMapper<T>, T, K> {
    @Autowired
    protected M mapper;
    @Autowired
    protected SqlSessionFactory sqlSessionFactory;

    public T selectOne(T ex) {
        return mapper.selectOne(ex);
    }

    public List<T> selectByExample(Example ex) {
        return mapper.selectByExample(ex);
    }

    public T selectOneByExample(Example ex) {
        return mapper.selectOneByExample(ex);
    }

    public T selectOne(QueryBuilder qb) {
        return mapper.selectOneByExample(qb.getExample());
    }

    public List<T> selectList(QueryBuilder qb) {
        return mapper.selectByExample(qb.getExample());
    }

    public int count(QueryBuilder qb) {
        return mapper.selectCountByExample(qb.getExample());
    }

    public List<T> selectAll() {
        return mapper.selectAll();
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

    public int insertSelective(T t) {
        return mapper.insertSelective(t);
    }

    public int updateSelective(T t) {
        return mapper.updateByPrimaryKeySelective(t);
    }

    public int updateById(T t) {
        return mapper.updateByPrimaryKey(t);
    }

    public int save(T t) {
        return mapper.insert(t);
    }

    public int saveBatch(List<T> entityList) {
        return mapper.insertList(entityList);
    }

    public int updateBatchById(List<T> entityList) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        int res = 0;
        try {
            Type idType = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            Class<K> clazz = (Class<K>) idType;

            MyMapper mapper = (MyMapper) sqlSession.getMapper(clazz);
            // TODO 切片
            for (T t : entityList) {
                res += mapper.updateByPrimaryKey(t);
            }
            sqlSession.commit();
            sqlSession.clearCache();
        }catch (Exception e) {
            sqlSession.rollback();
            throw e;
        } finally {
            sqlSession.close();
        }
        return res;
    }

    public int removeById(Serializable id) {
        return mapper.deleteByPrimaryKey(id);
    }
}
