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
package com.zerolinck.passiflora.codegen.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** @author linck on 2024-02-06 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Table {

    private String tableName;

    private String pkFieldName;

    private String pkColumnName;

    private String description;

    /** 原始字段集合 */
    private List<Column> originColumnList;

    /** 移除了{@link com.zerolinck.passiflora.model.common.BaseEntity} 中已存在字段的字段集合 */
    private List<Column> columnList;

    private boolean extentBase;

    public Table(String tableName, String description, List<Column> originColumnList, List<Column> columnList) {
        this.tableName = tableName;
        this.description = description;
        this.originColumnList = originColumnList;
        this.columnList = columnList;
    }
}
