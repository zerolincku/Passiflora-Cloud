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

import com.zerolinck.passiflora.codegen.model.Column;
import com.zerolinck.passiflora.codegen.model.Table;
import com.zerolinck.passiflora.codegen.service.DbService;
import com.zerolinck.passiflora.codegen.util.DataSourceUtil;
import com.zerolinck.passiflora.codegen.util.TypeConvert;
import com.zerolinck.passiflora.common.util.StrUtil;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/** @author linck on 2024-02-06 */
public class PostgresDbService implements DbService {

    Set<String> baseFields = Set.of("create_by", "update_by", "create_time", "update_time", "del_flag", "version");

    @Override
    @SneakyThrows
    public Table getTableInfo(String tableName) {
        QueryRunner runner = new QueryRunner(DataSourceUtil.getDataSource());
        List<Column> originColumnList = getOriginColumnList(tableName, runner);
        AtomicInteger baseFlagNum = new AtomicInteger();
        List<Column> columnList = new ArrayList<>();
        AtomicReference<String> pkFiledName = new AtomicReference<>();
        AtomicReference<String> pkColumnName = new AtomicReference<>();
        originColumnList.forEach(column -> {
            if (baseFields.contains(column.getColumnName())) {
                baseFlagNum.getAndIncrement();
            }
            column.setFieldType(TypeConvert.columnType2FieldType(column.getColumnType()));
            column.setFieldName(StrUtil.toCamelCase(column.getColumnName()));
            if (Boolean.TRUE.equals(column.getPk())) {
                pkFiledName.set(column.getFieldName());
                pkColumnName.set(column.getColumnName());
            } else {
                column.setPk(false);
            }
        });
        originColumnList.forEach(column -> {
            if (baseFlagNum.get() == baseFields.size() && baseFields.contains(column.getColumnName())) {
                return;
            }
            columnList.add(column);
        });

        Table table = runner.query(
                "SELECT obj_description(?::regclass) as description;", new BeanHandler<>(Table.class), tableName);
        table.setTableName(tableName);
        table.setColumnList(columnList);
        table.setOriginColumnList(originColumnList);
        table.setPkFieldName(pkFiledName.get());
        table.setPkColumnName(pkColumnName.get());
        table.setExtendsBase(baseFlagNum.get() == baseFields.size());
        return table;
    }

    private static List<Column> getOriginColumnList(String tableName, QueryRunner runner) throws SQLException {
        String sql =
                """
                SELECT A.attname AS columnName,
                    format_type( A.atttypid, A.atttypmod ) AS columnType,
                    A.attnotnull AS nullable,
                    i.indisprimary AS pk,
                    d.character_maximum_length AS length,
                    col_description( A.attrelid, A.attnum ) AS description,
                    d.column_default as columnDefault
                FROM
                    pg_class
                    C INNER JOIN pg_attribute A ON A.attrelid = C.
                    OID LEFT JOIN pg_index i ON C.OID = i.indrelid
                    AND i.indisprimary = TRUE
                    AND A.attnum = ANY ( i.indkey )
                    LEFT JOIN ( SELECT col.COLUMN_NAME, col.column_default, character_maximum_length FROM information_schema.COLUMNS AS col WHERE col.TABLE_NAME = ? ) AS d ON A.attname = d.COLUMN_NAME
                WHERE
                    C.relname = ?
                    AND A.attnum > 0;
                """;
        return runner.query(sql, new BeanListHandler<>(Column.class), tableName, tableName);
    }
}
