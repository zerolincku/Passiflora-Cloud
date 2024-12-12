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
package com.zerolinck.passiflora.common.config.mybaits;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import jakarta.annotation.PostConstruct;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerolinck.passiflora.common.util.EnumUtil;
import com.zerolinck.passiflora.common.util.lock.ClassUtil;
import com.zerolinck.passiflora.model.common.LabelValueInterface;

import lombok.RequiredArgsConstructor;

/** @author linck on 2023-04-11 */
@RequiredArgsConstructor
public class TypeHandlerRegister {
    private final SqlSessionFactory sqlSessionFactory;
    private final ObjectMapper objectMapper;

    @PostConstruct
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void init() {
        // 扫描当前项目下所有 LabelValueInterface 实现类
        Set<Class<?>> classes = ClassUtil.getLabelValueClasses();

        sqlSessionFactory
                .getConfiguration()
                .getTypeHandlerRegistry()
                .register(JsonNode.class, new JsonTypeHandler(objectMapper));

        // 注册枚举类与数据库查询结果/参数的序列化与反序列化规则
        classes.forEach(clazz -> sqlSessionFactory
                .getConfiguration()
                .getTypeHandlerRegistry()
                .register(
                        clazz,
                        new BaseTypeHandler() { // NOPMD
                            @Override
                            public void setNonNullParameter(
                                    PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
                                    throws SQLException {
                                ps.setInt(i, ((LabelValueInterface) parameter).getValue());
                            }

                            @Override
                            public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
                                return EnumUtil.getEnumByValue(
                                        (Class<? extends LabelValueInterface>) clazz, rs.getInt(columnName));
                            }

                            @Override
                            public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
                                return EnumUtil.getEnumByValue(
                                        (Class<? extends LabelValueInterface>) clazz, rs.getInt(columnIndex));
                            }

                            @Override
                            public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
                                return EnumUtil.getEnumByValue(
                                        (Class<? extends LabelValueInterface>) clazz, cs.getInt(columnIndex));
                            }
                        }));
    }
}
