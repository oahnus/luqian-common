package com.github.oahnus.luqiancommon.mybatis;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * Created by oahnus on 2019/10/24
 * 13:14.
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
