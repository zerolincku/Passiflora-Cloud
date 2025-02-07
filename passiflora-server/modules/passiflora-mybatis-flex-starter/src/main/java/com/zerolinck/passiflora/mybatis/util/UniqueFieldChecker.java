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
package com.zerolinck.passiflora.mybatis.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.zerolinck.passiflora.base.BaseEntity;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.common.util.StrUtils;
import org.apache.commons.lang3.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * 通过唯一字段检测数据是否已经存在
 *
 * @author linck on 2023-12-18
 */
public class UniqueFieldChecker {

    private static final Map<Class<?>, List<UniqueField>> map = new HashMap<>();

    @SneakyThrows
    @SuppressWarnings({"rawtypes"})
    public static <T extends BaseEntity> void checkInsert(BaseMapper mapper, T entity) {
        checkCache(entity);
        List<UniqueField> uniqueFields = map.get(entity.getClass());
        for (UniqueField uniqueField : uniqueFields) {
            uniqueField.getField().setAccessible(true);

            Object fieldValue = uniqueField.getField().get(entity);
            if (fieldValue == null) {
                continue;
            }

            long count = mapper.selectCountByQuery(new QueryWrapper().eq(uniqueField.getFieldName(), fieldValue));
            if (count > 0) {
                String message;
                if (StringUtils.isNotBlank(uniqueField.getMessage())) {
                    message = uniqueField.getMessage();
                } else {
                    message = uniqueField.getFieldDesc() + "已存在，请重新填写";
                }
                throw new BizException(message);
            }
        }
    }

    @SneakyThrows
    @SuppressWarnings({"rawtypes"})
    public static <T extends BaseEntity> void checkUpdate(BaseMapper mapper, T entity) {
        checkCache(entity);
        List<UniqueField> uniqueFields = map.get(entity.getClass());
        for (UniqueField uniqueField : uniqueFields) {
            uniqueField.getField().setAccessible(true);
            uniqueField.getIdField().setAccessible(true);

            Object fieldValue = uniqueField.getField().get(entity);
            if (fieldValue == null) {
                continue;
            }

            String fieldName = uniqueField.getFieldName();
            long count = mapper.selectCountByQuery(new QueryWrapper()
                    .eq(fieldName, fieldValue)
                    .ne(
                            StrUtils.camelToUnderline(uniqueField.getIdField().getName()),
                            uniqueField.getIdField().get(entity)));
            if (count > 0) {
                String message;
                if (StringUtils.isNotBlank(uniqueField.getMessage())) {
                    message = uniqueField.getMessage();
                } else {
                    message = uniqueField.getFieldDesc() + "已存在，请重新填写";
                }
                throw new BizException(message);
            }
        }
    }

    /**
     * 获取实体类字段熟悉添加到缓存中
     *
     * @param entity 实体类对象
     * @param <T> BaseEntity 子类
     * @author 林常坤 on 2024/12/12
     */
    private static <T extends BaseEntity> void checkCache(T entity) {
        if (!map.containsKey(entity.getClass())) {
            Field[] fields = entity.getClass().getDeclaredFields();
            List<UniqueField> result = new ArrayList<>();
            Field idField = null;
            for (Field field : fields) {
                if (field.getAnnotation(Id.class) != null) {
                    idField = field;
                    break;
                }
            }
            for (Field field : fields) {
                com.zerolinck.passiflora.base.valid.UniqueField annotation =
                        field.getAnnotation(com.zerolinck.passiflora.base.valid.UniqueField.class);
                if (annotation == null) {
                    continue;
                }

                String desc = field.getName();

                Schema schema = field.getAnnotation(Schema.class);
                if (schema != null) {
                    desc = schema.description();
                }
                UniqueField uniqueField = new UniqueField(
                        field, idField, StrUtils.camelToUnderline(field.getName()), desc, annotation.message());
                result.add(uniqueField);
            }
            map.put(entity.getClass(), result);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UniqueField {
        /** 实体类字段 */
        private Field field;
        /** 实体类主键字段 */
        private Field idField;
        /** 字段对应数据库字段名 */
        private String fieldName;
        /** 字段描述 */
        private String fieldDesc;
        /** 字段重复时抛出的异常消息 */
        private String message;
    }
}
