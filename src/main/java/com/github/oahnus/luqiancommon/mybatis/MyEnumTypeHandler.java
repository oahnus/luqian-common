package com.github.oahnus.luqiancommon.mybatis;

import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by oahnus on 2019/10/24
 * 14:20.
 */
public class MyEnumTypeHandler<E extends Enum<E> & BaseEnum> extends EnumTypeHandler<E> {
    private E[] enums;
    private Class<E> type;

    public MyEnumTypeHandler(Class<E> type) {
        super(type);
        this.type = type;
        this.enums = type.getEnumConstants();
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return find(rs.getInt(columnName));
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return find(rs.getInt(columnIndex));
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return find(cs.getInt(columnIndex));
    }

    private E find(int code) {
        System.out.println("find :" + code);

        for (E e : enums) {
            if (e.getCode() == code) {
                return e;
            }
        }
        return null;
    }
}
