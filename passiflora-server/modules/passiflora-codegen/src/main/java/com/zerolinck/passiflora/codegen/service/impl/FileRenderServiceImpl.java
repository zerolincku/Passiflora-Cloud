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

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.zerolinck.passiflora.codegen.model.Render;
import com.zerolinck.passiflora.codegen.service.RenderService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author linck on 2024-02-07
 */
@Slf4j
public class FileRenderServiceImpl implements RenderService {

    @Override
    public void render(Render render) {
        TemplateEngine engine =
                TemplateUtil.createEngine(new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate(render.getTemplate());
        String result = template.render(render.getData());
        String currentDirectory = System.getProperty("user.dir");
        String dirPath = String.format(currentDirectory + render.getPath());
        String filePath =
                String.format(dirPath + render.getFileName(), render.getData().get("entityClass"));
        if (!FileUtil.exist(dirPath)) {
            FileUtil.mkdir(dirPath);
        }
        if (render.isOverride()) {
            if (FileUtil.exist(filePath)) {
                log.info("{}文件已存在，覆盖", filePath);
            }
        } else {
            FileUtil.writeUtf8String(result, filePath);
            if (FileUtil.exist(filePath)) {
                log.info("{}文件已存在，不进行写入", filePath);
            } else {
                FileUtil.writeUtf8String(result, filePath);
            }
        }
    }
}
