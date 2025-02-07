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
package com.zerolinck.passiflora.codegen;

import java.util.List;
import java.util.Map;

import com.zerolinck.passiflora.codegen.model.Render;
import com.zerolinck.passiflora.codegen.model.Table;
import com.zerolinck.passiflora.codegen.service.RenderService;
import com.zerolinck.passiflora.codegen.service.impl.FileRenderServiceImpl;
import com.zerolinck.passiflora.codegen.service.impl.PostgresDbService;
import com.zerolinck.passiflora.common.util.MapUtils;
import com.zerolinck.passiflora.common.util.StrUtils;
import com.zerolinck.passiflora.common.util.TimeUtils;

import lombok.SneakyThrows;

/** @author linck on 2024-02-06 */
public class GenerateCode {

    @SneakyThrows
    public static void main(String[] args) {
        List<String> tableNames = List.of("iam_app");
        String moduleName = "iam";
        String contextPath = "/passiflora/iam-api";
        String author = "linck";
        boolean overwrite = false;
        render(tableNames, moduleName, contextPath, overwrite, author);
    }

    private static void render(
            List<String> tableNames, String moduleName, String contextPath, boolean overwrite, String author) {
        tableNames.forEach(tableName -> {
            Map<String, Object> dict = getInfo(tableName, moduleName, contextPath, author);
            RenderService renderService = new FileRenderServiceImpl();
            renderService.render(new Render(
                    overwrite,
                    "entity.java.ftl",
                    "/%s.java",
                    "/passiflora-server/modules/passiflora-model/src/main/java/com/zerolinck/passiflora/model/"
                            + moduleName
                            + "/entity",
                    dict));
            renderService.render(new Render(
                    overwrite,
                    "convert.java.ftl",
                    "/%sConvert.java",
                    "/passiflora-server/modules/passiflora-model/src/main/java/com/zerolinck/passiflora/model/"
                            + moduleName
                            + "/mapperstruct",
                    dict));
            renderService.render(new Render(
                    overwrite,
                    "api.java.ftl",
                    "/%sApi.java",
                    "/passiflora-server/modules/passiflora-feign/src/main/java/com/zerolinck/passiflora/feign/"
                            + moduleName,
                    dict));
            renderService.render(new Render(
                    overwrite,
                    "controller.java.ftl",
                    "/%sController.java",
                    "/passiflora-server/passiflora-" + moduleName
                            + "-app/src/main/java/com/zerolinck/passiflora/"
                            + moduleName
                            + "/controller",
                    dict));
            renderService.render(new Render(
                    overwrite,
                    "service.java.ftl",
                    "/%sService.java",
                    "/passiflora-server/passiflora-" + moduleName
                            + "-app/src/main/java/com/zerolinck/passiflora/"
                            + moduleName
                            + "/service",
                    dict));
            renderService.render(new Render(
                    overwrite,
                    "mapper.java.ftl",
                    "/%sMapper.java",
                    "/passiflora-server/passiflora-" + moduleName
                            + "-app/src/main/java/com/zerolinck/passiflora/"
                            + moduleName
                            + "/mapper",
                    dict));
            renderService.render(new Render(
                    overwrite,
                    "mapper.xml.ftl",
                    "/%sMapper.xml",
                    "/passiflora-server/passiflora-" + moduleName
                            + "-app/src/main/java/com/zerolinck/passiflora/"
                            + moduleName
                            + "/mapper/xml",
                    dict));
            renderService.render(new Render(
                    overwrite,
                    "test.java.ftl",
                    "/%sControllerTest.java",
                    "/passiflora-server/passiflora-" + moduleName
                            + "-app/src/test/java/com/zerolinck/passiflora/"
                            + moduleName,
                    dict));
        });
    }

    private static Map<String, Object> getInfo(String tableName, String moduleName, String contextPath, String author) {
        Table table = new PostgresDbService().getTableInfo(tableName);
        return MapUtils.builder()
                .put("author", author)
                .put("contextPath", contextPath)
                .put("table", table)
                .put("tableName", tableName)
                .put("moduleName", moduleName)
                .put("date", TimeUtils.getDateStrNow())
                .put("entityName", StrUtils.toCamelCase(table.getTableName()))
                .put("entityNameUrl", table.getTableName().replace("_", "-"))
                .put("serviceName", StrUtils.toCamelCase(table.getTableName() + "_" + "service"))
                .put("mapperName", StrUtils.toCamelCase(table.getTableName() + "_" + "mapper"))
                .put("controllerName", StrUtils.toCamelCase(table.getTableName() + "_" + "controller"))
                .put("apiName", StrUtils.toCamelCase(table.getTableName() + "_" + "api"))
                .put("convertName", StrUtils.toCamelCase(table.getTableName() + "_" + "convert"))
                .put("entityClass", StrUtils.upperFirst(StrUtils.toCamelCase(table.getTableName())))
                .put("serviceClass", StrUtils.upperFirst(StrUtils.toCamelCase(table.getTableName() + "_" + "service")))
                .put("mapperClass", StrUtils.upperFirst(StrUtils.toCamelCase(table.getTableName() + "_" + "mapper")))
                .put(
                        "controllerClass",
                        StrUtils.upperFirst(StrUtils.toCamelCase(table.getTableName() + "_" + "controller")))
                .put("apiClass", StrUtils.upperFirst(StrUtils.toCamelCase(table.getTableName() + "_" + "api")))
                .put("convertClass", StrUtils.upperFirst(StrUtils.toCamelCase(table.getTableName() + "_" + "convert")))
                .build();
    }
}
