package com.goarchery.common.config.handler;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.util.List;

public abstract class JsonListTypeHandler<T> extends BaseTypeHandler<List<T>> {
    private static final ObjectMapper M = new ObjectMapper();
    private final Class<T> elementType;

    protected JsonListTypeHandler(Class<T> elementType) {
        this.elementType = elementType;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<T> parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setObject(i, M.writeValueAsString(parameter), Types.OTHER);
        } catch (Exception e) {
            throw new SQLException("Write JSON failed", e);
        }
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public List<T> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    private List<T> parse(String json) throws SQLException {
        if (json == null) return null;
        try {
            JavaType type = M.getTypeFactory().constructCollectionType(List.class, elementType);
            return M.readValue(json, type);
        } catch (Exception e) {
            throw new SQLException("Parse JSON to List<" + elementType.getSimpleName() + "> failed", e);
        }
    }
}
