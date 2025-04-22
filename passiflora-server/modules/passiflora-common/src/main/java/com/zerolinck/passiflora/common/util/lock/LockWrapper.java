/* 
 * Copyright (C) 2025 Linck. <zerolinck@foxmail.com>
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
package com.zerolinck.passiflora.common.util.lock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zerolinck.passiflora.common.util.lock.suppert.SFunction;
import org.apache.commons.collections4.CollectionUtils;

import lombok.Getter;

/** @author linck on 2024-05-04 */
@SuppressWarnings("all")
public class LockWrapper<T> {

    /** 加锁字段 eg: 需要根据 name 字段加锁，XXXEntity::getName */
    @Getter
    private List<SFunction<T, ?>> columns = new ArrayList<>();

    /** 单项加锁 eg: 需要锁定 name: a */
    @Getter
    private List<Object> columnValues = new ArrayList<>();

    /** 多项加锁 eg: 需要锁定 name: a、name: b ... */
    @Getter
    private Map<SFunction<T, ?>, List<?>> columnValueList = new HashMap<>();

    public LockWrapper<T> lock(SFunction<T, ?> column, String columnValue) {
        if (columnValue == null) {
            return this;
        }
        columns.add(column);
        columnValues.add(columnValue);
        return this;
    }

    public LockWrapper<T> lock(SFunction<T, ?> column, List<?> columnValues) {
        if (CollectionUtils.isEmpty(columnValues)) {
            return this;
        }
        columnValueList.put(column, columnValues);
        return this;
    }
}
