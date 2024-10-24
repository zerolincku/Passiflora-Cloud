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
package com.zerolinck.passiflora.codegen.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.zerolinck.passiflora.codegen.model.Column;
import com.zerolinck.passiflora.codegen.model.Table;
import com.zerolinck.passiflora.codegen.service.DbService;
import com.zerolinck.passiflora.codegen.util.TypeConvert;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import lombok.SneakyThrows;

/** @author linck on 2024-02-06 */
public class PostgresDbService implements DbService {

    @Override
    @SneakyThrows
    public Table getTableInfo(String tableName) {
        List<Entity> result = Db.use()
                .query(
                        """
                SELECT A
                    .attname AS COLUMN_NAME,
                    format_type ( A.atttypid, A.atttypmod ) AS field_type,
                    A.attnotnull AS NULLABLE,
                    i.indisprimary AS pk,
                    d.character_maximum_length AS LENGTH,
                    col_description ( A.attrelid, A.attnum ) AS description,
                    d.column_default\s
                FROM
                    pg_class
                    C INNER JOIN pg_attribute A ON A.attrelid = C.
                    OID LEFT JOIN pg_index i ON C.OID = i.indrelid\s
                    AND i.indisprimary = TRUE\s
                    AND A.attnum = ANY ( i.indkey )
                    LEFT JOIN ( SELECT col.COLUMN_NAME, col.column_default, character_maximum_length FROM information_schema.COLUMNS AS col WHERE col.TABLE_NAME = ? ) AS d ON A.attname = d.COLUMN_NAME\s
                WHERE
                    C.relname = ?\s
                    AND A.attnum > 0;
                """,
                        tableName,
                        tableName);
        // 标识是否应该继承 BaseEntity
        Dict baseFlag = Dict.create()
                .set("create_by", 0)
                .set("update_by", 0)
                .set("create_time", 0)
                .set("update_time", 0)
                .set("del_flag", 0)
                .set("version", 0);
        AtomicInteger baseFlagNum = new AtomicInteger();
        result.forEach(entity -> {
            if (baseFlag.containsKey(entity.get("column_name", ""))) {
                baseFlagNum.getAndIncrement();
            }
        });

        List<Column> columnList = new ArrayList<>();
        List<Column> originColumnList = new ArrayList<>();
        AtomicReference<String> pkFiledName = new AtomicReference<>();
        AtomicReference<String> pkColumnName = new AtomicReference<>();
        result.forEach(entity -> {
            Column column = new Column();
            column.setColumnName(entity.get("column_name", ""));
            column.setFieldName(StrUtil.toCamelCase(entity.get("column_name", "")));
            column.setColumnType(entity.get("field_type", ""));
            column.setFieldType(TypeConvert.columnType2FieldType(entity.get("field_type", "")));
            column.setNullable(entity.get("nullable", true));
            column.setPk(entity.get("pk", false));
            column.setLength(entity.get("length", -1));
            column.setDescription(entity.get("description", ""));
            column.setColumnDefault(entity.get("column_default", ""));
            if (column.getPk()) {
                pkFiledName.set(StrUtil.toCamelCase(column.getColumnName()));
                pkColumnName.set(column.getColumnName());
            }
            originColumnList.add(column);
            if (baseFlagNum.get() == baseFlag.size() && baseFlag.containsKey(entity.get("column_name", ""))) {
                return;
            }
            columnList.add(column);
        });

        List<Entity> tableInfo = Db.use().query("SELECT obj_description(?::regclass);", tableName);
        String tableDesc = "";
        for (Entity t : tableInfo) {
            tableDesc = t.get("obj_description", "");
        }
        return new Table(
                tableName,
                pkFiledName.get(),
                pkColumnName.get(),
                tableDesc,
                originColumnList,
                columnList,
                baseFlagNum.get() == baseFlag.size());
    }
}
