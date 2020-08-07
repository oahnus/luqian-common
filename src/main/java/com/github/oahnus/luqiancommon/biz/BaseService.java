package com.github.oahnus.luqiancommon.biz;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by oahnus on 2019/10/8
 * 11:16.
 *
 * T Entity
 * K Entity Id Type
 */
public class BaseService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {
    @Autowired
    protected M mapper;
}
