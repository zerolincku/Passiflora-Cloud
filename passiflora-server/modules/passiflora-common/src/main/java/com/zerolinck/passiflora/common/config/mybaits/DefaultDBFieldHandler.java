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

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.zerolinck.passiflora.common.util.CurrentUtil;
import com.zerolinck.passiflora.common.util.StrUtil;
import com.zerolinck.passiflora.model.iam.entity.IamUser;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.springframework.util.ClassUtils;

/**
 * @author linck on 2024-02-07
 */
@Slf4j
public class DefaultDBFieldHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("mybatis plus start insert fill ....");
        IamUser iamUser = CurrentUtil.getCurrentUser();
        LocalDateTime now = LocalDateTime.now();

        fillValIfNullByName("createTime", now, metaObject, true);
        fillValIfNullByName("updateTime", now, metaObject, true);
        fillValIfNullByName("delFlag", 0, metaObject, true);
        if (iamUser != null) {
            fillValIfNullByName("createBy", iamUser.getUserId(), metaObject, true);
            fillValIfNullByName("updateBy", iamUser.getUserId(), metaObject, true);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("mybatis plus start update fill ....");
        LocalDateTime now = LocalDateTime.now();
        IamUser iamUser = CurrentUtil.getCurrentUser();
        fillValIfNullByName("updateTime", now, metaObject, true);
        if (iamUser != null) {
            fillValIfNullByName("updateBy", iamUser.getUserId(), metaObject, true);
        }
    }

    /**
     * 填充值，先判断是否有手动设置，优先手动设置的值
     *
     * @param fieldName 属性名
     * @param fieldVal 属性值
     * @param metaObject MetaObject
     * @param isCover 是否覆盖原有值,避免更新操作手动入参
     */
    private static void fillValIfNullByName(String fieldName, Object fieldVal, MetaObject metaObject, boolean isCover) {
        // 1. 没有 set 方法
        if (!metaObject.hasSetter(fieldName)) {
            return;
        }
        // 2. 如果用户有手动设置的值
        Object userSetValue = metaObject.getValue(fieldName);
        String setValueStr = StrUtil.str(userSetValue, StandardCharsets.UTF_8);
        if (StringUtils.isNotBlank(setValueStr) && !isCover) {
            return;
        }
        if (fieldVal == null) {
            PropertyTokenizer prop = new PropertyTokenizer(fieldName);
            metaObject.getObjectWrapper().set(prop, null);
            return;
        }
        // 3. field 类型相同时设置
        Class<?> getterType = metaObject.getGetterType(fieldName);
        if (ClassUtils.isAssignableValue(getterType, fieldVal)) {
            metaObject.setValue(fieldName, fieldVal);
        }
    }
}
