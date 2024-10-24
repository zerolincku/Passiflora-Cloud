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
package com.zerolinck.passiflora.common.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import lombok.SneakyThrows;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/** @author 林常坤 on 2024/10/24 */
public class YamlUtil {

    private static final Yaml yaml = new Yaml();

    /** 从绝对路径加载 YAML 文件 */
    @SneakyThrows
    public static Map<String, Object> loadYamlFromFile(String filePath) {
        try (InputStream inputStream = new FileInputStream(filePath)) {
            return yaml.load(inputStream);
        }
    }

    /** 从 classpath 加载 YAML 文件 */
    @SneakyThrows
    public static Map<String, Object> loadYamlFromClasspath(String classpathFileName) {
        try (InputStream inputStream = YamlUtil.class.getClassLoader().getResourceAsStream(classpathFileName)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found in classpath: " + classpathFileName);
            }
            return yaml.load(inputStream);
        }
    }

    /** 从绝对路径加载 YAML 文件并解析为指定的类型 */
    @SneakyThrows
    public static <T> T loadYamlFromAbsolutePath(String filePath, Class<T> type) {
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
            LoaderOptions loaderOptions = new LoaderOptions();
            Constructor constructor = new Constructor(type, loaderOptions);
            Yaml yaml = new Yaml(constructor);
            return yaml.load(inputStream);
        }
    }

    /** 从 classpath 加载 YAML 文件并解析为指定的类型 */
    @SneakyThrows
    public static <T> T loadYamlFromClasspath(String resourcePath, Class<T> type) {
        try (InputStream inputStream = YamlUtil.class.getClassLoader().getResourceAsStream(resourcePath)) {
            LoaderOptions loaderOptions = new LoaderOptions();
            Constructor constructor = new Constructor(type, loaderOptions);
            Yaml yaml = new Yaml(constructor);
            return yaml.load(inputStream);
        }
    }

    /** 渲染 YAML 文件为对象 */
    public static <T> T loadYaml(String yamlContent, Class<T> type) {
        LoaderOptions loaderOptions = new LoaderOptions();
        Constructor constructor = new Constructor(type, loaderOptions);
        Yaml yaml = new Yaml(constructor);
        return yaml.load(yamlContent);
    }
}
