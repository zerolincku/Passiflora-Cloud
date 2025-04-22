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
package com.zerolinck.passiflora.common.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import lombok.experimental.UtilityClass;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/** @author 林常坤 on 2024/10/24 */
@UtilityClass
public class FreemarkerUtils {

    private static final Configuration configuration;

    static {
        // 初始化 Freemarker 配置
        configuration = new Configuration(Configuration.VERSION_2_3_33);
        // 设置默认编码
        configuration.setDefaultEncoding("UTF-8");
    }

    /**
     * 使用绝对路径获取模板
     *
     * @param absolutePath 模板的绝对路径
     * @return 模板对象
     */
    public static Template getTemplateByAbsolutePath(String absolutePath) throws IOException {
        configuration.setDirectoryForTemplateLoading(new File(absolutePath).getParentFile());
        return configuration.getTemplate(new File(absolutePath).getName());
    }

    /**
     * 从 classpath 获取模板
     *
     * @param templateName classpath 下的模板文件名
     * @return 模板对象
     */
    public static Template getTemplateFromClasspath(String templateName) throws IOException {
        // 设置模板加载路径为 classpath 下的 /templates 目录
        configuration.setClassLoaderForTemplateLoading(FreemarkerUtils.class.getClassLoader(), "/templates");
        return configuration.getTemplate(templateName);
    }

    /**
     * 渲染模板并返回渲染后的字符串
     *
     * @param template 模板对象
     * @param dataModel 模板中的数据模型
     * @return 渲染后的字符串
     */
    public static String renderTemplate(Template template, Map<String, Object> dataModel)
            throws IOException, TemplateException {
        // 创建输出
        StringWriter writer = new StringWriter();
        // 渲染模板
        template.process(dataModel, writer);
        return writer.toString();
    }
}
