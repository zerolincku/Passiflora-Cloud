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

import java.io.File;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

/** @author 林常坤 on 2024/10/24 */
@Slf4j
public class FileUtil {

    /**
     * 确保目录存在，不存在时创建
     *
     * @param dirPath 目录路径
     * @throws Exception 创建目录时可能抛出的异常
     */
    public static void ensureDirectoryExists(String dirPath) throws Exception {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            FileUtils.forceMkdir(dir);
        }
    }

    /**
     * 将内容写入文件，支持覆盖和不覆盖的逻辑
     *
     * @param filePath 文件路径
     * @param content 写入的内容
     * @param override 是否覆盖文件
     * @throws Exception 文件写入时可能抛出的异常
     */
    public static void writeFile(String filePath, String content, boolean override) throws Exception {
        File file = new File(filePath);
        if (override) {
            if (file.exists()) {
                log.info("{} 文件已存在，覆盖", filePath);
            }
            FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
        } else {
            if (file.exists()) {
                log.info("{} 文件已存在，不进行写入", filePath);
            } else {
                FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
            }
        }
    }
}
