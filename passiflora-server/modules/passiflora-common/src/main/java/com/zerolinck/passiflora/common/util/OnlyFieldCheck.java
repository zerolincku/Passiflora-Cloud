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
package com.zerolinck.passiflora.common.util;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zerolinck.passiflora.common.exception.BizException;
import com.zerolinck.passiflora.model.valid.OnlyField;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

/**
 * @author linck
 * @since 2023-12-18
 */
public class OnlyFieldCheck {

    private static final Map<Class<?>, List<CheckField>> map = new HashMap<>();

    @SneakyThrows
    public static void checkInsert(BaseMapper baseMapper, Object entity) {
        checkCache(entity);
        List<CheckField> fields = map.get(entity.getClass());
        for (CheckField field : fields) {
            field.getField().setAccessible(true);

            Object fieldValue = field.getField().get(entity);
            if (fieldValue == null) {
                continue;
            }

            Long count = baseMapper.selectCount(new QueryWrapper<>().eq(field.getFieldName(), fieldValue));
            if (count > 0) {
                String message;
                if (StrUtil.isNotBlank(field.getMessage())) {
                    message = field.getMessage();
                } else {
                    message = field.getFieldDesc() + "已存在，请重新填写";
                }
                throw new BizException(message);
            }
        }
    }

    @SneakyThrows
    public static void checkUpdate(BaseMapper baseMapper, Object entity) {
        checkCache(entity);
        List<CheckField> fields = map.get(entity.getClass());
        for (CheckField field : fields) {
            field.getField().setAccessible(true);
            field.getIdField().setAccessible(true);

            Object fieldValue = field.getField().get(entity);
            if (fieldValue == null) {
                continue;
            }

            Long count = baseMapper.selectCount(new QueryWrapper<>()
                    .eq(field.getFieldName(), fieldValue)
                    .ne(
                            StrUtil.toUnderlineCase(field.getIdField().getName()),
                            field.getIdField().get(entity)));
            if (count > 0) {
                String message;
                if (StrUtil.isNotBlank(field.getMessage())) {
                    message = field.getMessage();
                } else {
                    message = field.getFieldDesc() + "已存在，请重新填写";
                }
                throw new BizException(message);
            }
        }
    }

    private static void checkCache(Object entity) {
        if (!map.containsKey(entity.getClass())) {
            Field[] fields = entity.getClass().getDeclaredFields();
            List<CheckField> result = new ArrayList<>();
            Field idField = null;
            for (Field field : fields) {
                if (field.getAnnotation(TableId.class) != null) {
                    idField = field;
                    break;
                }
            }
            for (Field field : fields) {
                OnlyField annotation = field.getAnnotation(OnlyField.class);
                if (annotation == null) {
                    continue;
                }

                String desc = field.getName();

                Schema schema = field.getAnnotation(Schema.class);
                if (schema != null) {
                    desc = schema.description();
                }
                CheckField checkField = new CheckField(
                        field, idField, StrUtil.toUnderlineCase(field.getName()), desc, annotation.message());
                result.add(checkField);
            }
            map.put(entity.getClass(), result);
        }
    }

    @Data
    @AllArgsConstructor
    public static class CheckField {

        private Field field;
        private Field idField;
        private String fieldName;
        private String fieldDesc;
        private String message;
    }
}
