/* 
 * Copyright (C) 2024 Linck. <zerolinck@foxmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zerolinck.passiflora.mybatis.config;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;

import lombok.SneakyThrows;

/** @author linck on 2024-01-11 */
public class JsonTypeHandler extends BaseTypeHandler<JsonNode> {

    private static final PGobject jsonObject = new PGobject();

    private final ObjectMapper objectMapper;

    public JsonTypeHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JsonNode parameter, JdbcType jdbcType)
            throws SQLException {
        jsonObject.setType("json");
        jsonObject.setValue(parameter.toString());
        ps.setObject(i, jsonObject);
    }

    @SneakyThrows
    @Override
    public JsonNode getNullableResult(ResultSet resultSet, String s) {
        return objectMapper.readValue(resultSet.getString(s), JsonNode.class);
    }

    @SneakyThrows
    @Override
    public JsonNode getNullableResult(ResultSet resultSet, int i) {
        return objectMapper.readValue(resultSet.getString(i), JsonNode.class);
    }

    @SneakyThrows
    @Override
    public JsonNode getNullableResult(CallableStatement callableStatement, int i) {
        return objectMapper.readValue(callableStatement.getString(i), JsonNode.class);
    }
}
