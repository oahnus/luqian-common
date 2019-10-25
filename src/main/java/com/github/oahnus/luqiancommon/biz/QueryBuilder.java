package com.github.oahnus.luqiancommon.biz;

import lombok.extern.slf4j.Slf4j;
import tk.mybatis.mapper.entity.Example;

import java.util.Collections;
import java.util.List;

/**
 * Created by oahnus on 2019/4/19
 * 16:30.
 * tkMapper Example简单包装 默认and连接SQL
 * 复杂查询使用QueryBuilder.or(QueryBuilder.createCriteria) 通过criteria构造符合查询
 *
 * TODO order
 */
@Slf4j
public class QueryBuilder extends Example {
    private Criteria criteria;
    private static List<Object> EMPTY = Collections.singletonList(-1);
    private Class<?> entityClass;

    public QueryBuilder(Class<?> entityClass) {
        super(entityClass);
        this.entityClass = entityClass;
        this.criteria = this.createCriteria();
    }

    public QueryBuilder(Class<?> entityClass, boolean exists) {
        super(entityClass, exists);
        this.criteria = this.createCriteria();
    }

    public QueryBuilder(Class<?> entityClass, boolean exists, boolean notNull) {
        super(entityClass, exists, notNull);
        this.criteria = this.createCriteria();
    }

    public QueryBuilder eq(String property, Object val){
        criteria.andEqualTo(property, val);
        return this;
    }

    public QueryBuilder lessThan(String property, Object val) {
        criteria.andLessThan(property, val);
        return this;
    }
    public QueryBuilder greaterThan(String property, Object val) {
        criteria.andGreaterThan(property, val);
        return this;
    }
    public QueryBuilder lessEqThan(String property, Object val) {
        criteria.andLessThanOrEqualTo(property, val);
        return this;

    }
    public QueryBuilder greaterEqThan(String property, Object val) {
        criteria.andGreaterThanOrEqualTo(property, val);
        return this;
    }



    public QueryBuilder notEq(String property, Object val) {
        criteria.andNotEqualTo(property, val);
        return this;
    }

    public QueryBuilder like(String property, String val) {
        criteria.andLike(property, "%" + val + "%");
        return this;
    }

    public QueryBuilder notLike(String property, String val) {
        criteria.andNotLike(property, "%" + val + "%");
        return this;
    }

    public QueryBuilder likeRight(String property, String val) {
        criteria.andLike(property, val + "%");
        return this;
    }
    public QueryBuilder likeLeft(String property, String val) {
        criteria.andLike(property, "%" + val);
        return this;
    }

    public QueryBuilder in(String property, Iterable listVal) {
        // 如果listVal 为空, in查询 select * from [TABLE] where [COLUMN] in (-1) 的结果
        if (!listVal.iterator().hasNext()) {
            criteria.andIn(property, EMPTY);
        } else {
            criteria.andIn(property, listVal);
        }
        return this;
    }

    public QueryBuilder between(String property, Object val1, Object val2) {
        criteria.andBetween(property, val1, val2);
        return this;
    }

    public QueryBuilder orderByAsc(String column) {
        this.setOrderByClause(column.trim() + " ASC");
        return this;
    }

    public QueryBuilder orderByDesc(String column) {
        this.setOrderByClause(column.trim() + " DESC");
        return this;
    }
}
