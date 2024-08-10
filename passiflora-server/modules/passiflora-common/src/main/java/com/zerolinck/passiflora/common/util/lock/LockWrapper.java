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
package com.zerolinck.passiflora.common.util.lock;

import cn.hutool.core.collection.CollectionUtil;
import com.zerolinck.passiflora.common.util.lock.suppert.SFunction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

/**
 * @author linck
 * @since 2024-05-04
 */
@SuppressWarnings("all")
public class LockWrapper<T> {

    @Getter
    private List<SFunction<T, ?>> columns = new ArrayList<>();

    @Getter
    private List<Object> columnValues = new ArrayList<>();

    @Getter
    private Map<SFunction<T, ?>, List<? extends T>> entityListLock = new HashMap<>();

    public LockWrapper<T> lock(SFunction<T, ?> column, String columnValue) {
        if (columnValue == null) {
            return this;
        }
        columns.add(column);
        columnValues.add(columnValue);
        return this;
    }

    public LockWrapper<T> lock(SFunction<T, ?> column, List<? extends T> entityList) {
        if (CollectionUtil.isEmpty(entityList)) {
            return this;
        }
        entityListLock.put(column, entityList);
        return this;
    }
}
