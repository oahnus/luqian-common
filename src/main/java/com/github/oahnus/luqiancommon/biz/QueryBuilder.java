package com.github.oahnus.luqiancommon.biz;

import lombok.extern.slf4j.Slf4j;
import tk.mybatis.mapper.entity.Example;

import java.util.Collections;
import java.util.List;

/**
 * Created by oahnus on 2019/4/19
 * 16:30.
 * tkMapper Example简单包装 默认and连接SQL
 * new QueryBuilder(Entity.class).eq("prop1", "val").or().eq("prop2", "val2")
 */
@Slf4j
public class QueryBuilder {
    private Example example;
    private Example.Criteria criteria;
    private static List<Object> EMPTY = Collections.singletonList(-1);
    private Class entityClass;
    private boolean isOr = false;

    public QueryBuilder(Class<?> entityClass) {
        this.entityClass = entityClass;
        this.example = new Example(entityClass);
        this.criteria = this.example.createCriteria();
    }

    public Example getExample() {
        return this.example;
    }

    public Example.Criteria getCriteria() {
        return this.criteria;
    }

    public QueryBuilder eq(String property, Object val){
        if (this.isOr) {
            Example.Criteria orCriteria = example.createCriteria();
            orCriteria.andEqualTo(property, val);
            this.example.or(orCriteria);
            this.isOr = false;
        } else {
            criteria.andEqualTo(property, val);
        }
        return this;
    }

    public QueryBuilder lessThan(String property, Object val) {
        if (this.isOr) {
            Example.Criteria orCriteria = example.createCriteria();
            orCriteria.andLessThan(property, val);
            this.example.or(orCriteria);
            this.isOr = false;
        } else {
            criteria.andLessThan(property, val);
        }
        return this;
    }
    public QueryBuilder greaterThan(String property, Object val) {
        if (this.isOr) {
            Example.Criteria orCriteria = example.createCriteria();
            orCriteria.andGreaterThan(property, val);
            this.example.or(orCriteria);
            this.isOr = false;
        } else {
            criteria.andGreaterThan(property, val);
        }
        return this;
    }
    public QueryBuilder lessEqThan(String property, Object val) {
        if (this.isOr) {
            Example.Criteria orCriteria = example.createCriteria();
            orCriteria.andLessThanOrEqualTo(property, val);
            this.example.or(orCriteria);
            this.isOr = false;
        } else {
            criteria.andLessThanOrEqualTo(property, val);
        }
        return this;
    }
    public QueryBuilder greaterEqThan(String property, Object val) {
        if (this.isOr) {
            Example.Criteria orCriteria = example.createCriteria();
            orCriteria.andGreaterThanOrEqualTo(property, val);
            this.example.or(orCriteria);
            this.isOr = false;
        } else {
            criteria.andGreaterThanOrEqualTo(property, val);
        }
        return this;
    }

    public QueryBuilder notEq(String property, Object val) {
        if (this.isOr) {
            Example.Criteria orCriteria = example.createCriteria();
            orCriteria.andNotEqualTo(property, val);
            this.example.or(orCriteria);
            this.isOr = false;
        } else {
            criteria.andNotEqualTo(property, val);
        }
        return this;
    }

    public QueryBuilder like(String property, String val) {
        if (this.isOr) {
            Example.Criteria orCriteria = example.createCriteria();
            orCriteria.andLike(property, "%" + val + "%");
            this.example.or(orCriteria);
            this.isOr = false;
        } else {
            criteria.andLike(property, "%" + val + "%");
        }
        return this;
    }

    public QueryBuilder notLike(String property, String val) {
        if (this.isOr) {
            Example.Criteria orCriteria = example.createCriteria();
            orCriteria.andNotLike(property, "%" + val + "%");
            this.example.or(orCriteria);
            this.isOr = false;
        } else {
            criteria.andNotLike(property, "%" + val + "%");
        }
        return this;
    }

    public QueryBuilder likeRight(String property, String val) {
        if (this.isOr) {
            Example.Criteria orCriteria = example.createCriteria();
            orCriteria.andLike(property, val + "%");
            this.example.or(orCriteria);
            this.isOr = false;
        } else {
            criteria.andLike(property, val + "%");
        }
        return this;
    }
    public QueryBuilder likeLeft(String property, String val) {
        if (this.isOr) {
            Example.Criteria orCriteria = example.createCriteria();
            orCriteria.andLike(property, "%" + val);
            this.example.or(orCriteria);
            this.isOr = false;
        } else {
            criteria.andLike(property, "%" + val);
        }
        return this;
    }

    public QueryBuilder in(String property, Iterable listVal) {
        // 如果listVal 为空, in查询 select * from [TABLE] where [COLUMN] in (-1) 的结果
        if (!listVal.iterator().hasNext()) {
            listVal = EMPTY;
        }

        if (this.isOr) {
            Example.Criteria orCriteria = example.createCriteria();
            orCriteria.andIn(property, listVal);
            this.example.or(orCriteria);
            this.isOr = false;
        } else {
            criteria.andIn(property, listVal);
        }
        return this;
    }

    public QueryBuilder between(String property, Object val1, Object val2) {
        if (this.isOr) {
            Example.Criteria orCriteria = example.createCriteria();
            orCriteria.andBetween(property, val1, val2);
            this.example.or(orCriteria);
            this.isOr = false;
        } else {
            criteria.andBetween(property, val1, val2);
        }
        return this;
    }

    public QueryBuilder orderByAsc(String... columns) {
        String[] clauses = new String[columns.length];
        for (int i = 0; i < clauses.length; i++) {
            clauses[i] = columns[i] + " ASC";
        }
        String orderClause = String.join(",", clauses);

        this.example.setOrderByClause(orderClause);
        return this;
    }

    public QueryBuilder orderByDesc(String... columns) {
        String[] clauses = new String[columns.length];
        for (int i = 0; i < clauses.length; i++) {
            clauses[i] = columns[i] + " DESC";
        }
        String orderClause = String.join(",", clauses);

        this.example.setOrderByClause(orderClause);
        return this;
    }

    public QueryBuilder or() {
        this.isOr = true;
        return this;
    }
}
