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

import com.zerolinck.passiflora.codegen.model.Render;
import com.zerolinck.passiflora.codegen.service.RenderService;
import com.zerolinck.passiflora.common.util.FileUtil;
import com.zerolinck.passiflora.common.util.FreemarkerUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/** @author linck on 2024-02-07 */
@Slf4j
public class FileRenderServiceImpl implements RenderService {

    @Override
    @SneakyThrows
    public void render(Render render) {
        String result = FreemarkerUtil.renderTemplate(
                FreemarkerUtil.getTemplateFromClasspath(render.getTemplate()), render.getData());

        // 确定文件路径
        String currentDirectory = System.getProperty("user.dir");
        String dirPath = String.format(currentDirectory + render.getPath());
        String filePath =
                String.format(dirPath + render.getFileName(), render.getData().get("entityClass"));

        try {
            // 确保目录存在
            FileUtil.ensureDirectoryExists(dirPath);
            // 写入文件
            FileUtil.writeFile(filePath, result, render.isOverride());
        } catch (Exception e) {
            log.error("文件写入错误", e);
        }
    }
}
