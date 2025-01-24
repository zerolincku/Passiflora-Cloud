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
package com.zerolinck.passiflora.storage.mapper;

import static com.zerolinck.passiflora.model.storage.entity.table.StorageFileTableDef.STORAGE_FILE;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.zerolinck.passiflora.model.storage.entity.StorageFile;
import com.zerolinck.passiflora.model.storage.enums.FileStatusEnum;
import org.jetbrains.annotations.NotNull;

/**
 * 通用文件 Mybatis Mapper
 *
 * @author linck
 * @since 2024-05-17
 */
public interface StorageFileMapper extends BaseMapper<StorageFile> {

    /**
     * 根据文件MD5查询文件列表
     *
     * @param fileMd5 文件MD5
     * @return 文件列表
     * @since 2024-05-17
     */
    default List<StorageFile> listByFileMd5(@NotNull String fileMd5) {
        return selectListByCondition(QueryCondition.create(STORAGE_FILE.FILE_MD5, fileMd5));
    }

    /**
     * 根据文件MD5统计文件数量
     *
     * @param fileMd5 文件MD5
     * @return 文件数量
     * @since 2024-05-17
     */
    default long countByFileMd5(@NotNull String fileMd5) {
        return selectCountByCondition(QueryCondition.create(STORAGE_FILE.FILE_MD5, fileMd5));
    }

    /**
     * 增加文件下载次数
     *
     * @param fileId 文件ID
     * @since 2024-05-17
     */
    default void incrDownCount(@NotNull String fileId) {
        UpdateChain.of(StorageFile.class)
                .setRaw(StorageFile::getDownloadCount, "download_count + 1")
                .eq(StorageFile::getFileId, fileId)
                .update();
    }

    /**
     * 确认文件使用，将临时文件转换为正式文件
     *
     * @param fileId 文件ID
     * @since 2024-05-17
     */
    default void confirmFile(@NotNull String fileId) {
        UpdateChain.of(StorageFile.class)
                .set(StorageFile::getFileStatus, FileStatusEnum.CONFIRMED)
                .eq(StorageFile::getFileId, fileId)
                .update();
    }

    /**
     * 获取过期的临时文件ID集合
     *
     * @return 过期的临时文件ID集合
     * @since 2024-05-17
     */
    default Set<String> expiredTempFileIds() {
        List<StorageFile> storageFiles = selectListByQuery(QueryWrapper.create()
                .select(STORAGE_FILE.FILE_ID)
                .from(STORAGE_FILE)
                .where(STORAGE_FILE.FILE_STATUS.eq(FileStatusEnum.TEMP))
                .where(STORAGE_FILE.CREATE_TIME.le(System.currentTimeMillis() - 1000 * 60 * 60 * 24)));
        return storageFiles.stream().map(StorageFile::getFileId).collect(Collectors.toSet());
    }
}
