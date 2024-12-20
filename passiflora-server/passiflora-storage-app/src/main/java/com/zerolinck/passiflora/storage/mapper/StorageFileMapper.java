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

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zerolinck.passiflora.model.storage.entity.StorageFile;

/**
 * 通用文件 Mybatis Mapper
 *
 * @author linck on 2024-05-17
 */
public interface StorageFileMapper extends BaseMapper<StorageFile> {

    @NotNull default Page<StorageFile> page(
            @NotNull IPage<StorageFile> page,
            @Param(Constants.WRAPPER) QueryWrapper<StorageFile> searchWrapper,
            @Param("sortWrapper") QueryWrapper<StorageFile> sortWrapper) {
        if (searchWrapper == null) {
            searchWrapper = new QueryWrapper<>();
        }
        searchWrapper.eq("del_flag", 0);

        if (sortWrapper == null
                || sortWrapper.getSqlSegment() == null
                || sortWrapper.getSqlSegment().isEmpty()) {
            searchWrapper.orderByAsc("file_id");
        } else {
            searchWrapper.last(sortWrapper.getSqlSegment());
        }

        return (Page<StorageFile>) this.selectPage(page, searchWrapper);
    }

    default int deleteByIds(
            @NotNull @Param("fileIds") Collection<String> fileIds, @Nullable @Param("updateBy") String updateBy) {
        if (CollectionUtils.isEmpty(fileIds)) {
            return 0;
        }

        LambdaUpdateWrapper<StorageFile> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(StorageFile::getFileId, fileIds)
                .set(StorageFile::getUpdateTime, LocalDateTime.now())
                .set(StorageFile::getUpdateBy, updateBy)
                .set(StorageFile::getDelFlag, 1);

        return this.update(null, updateWrapper);
    }

    @Select("SELECT * FROM storage_file WHERE file_md5 = #{fileMd5} AND del_flag = 0")
    List<StorageFile> listByFileMd5(@NotNull @Param("fileMd5") String fileMd5);

    @Select("SELECT count(*) FROM storage_file WHERE file_md5 = #{fileMd5} AND del_flag = 0")
    Integer countByFileMd5(@NotNull @Param("fileMd5") String fileMd5);

    @Update(
            "UPDATE storage_file SET last_download_time = now(), download_count = download_count + 1 WHERE file_id = #{fileId}")
    void incrDownCount(@NotNull @Param("fileId") String fileId);

    default int confirmFile(@NotNull @Param("fileId") String fileId, @Nullable @Param("updateBy") String updateBy) {
        LambdaUpdateWrapper<StorageFile> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(StorageFile::getFileId, fileId)
                .set(StorageFile::getUpdateTime, LocalDateTime.now())
                .set(StorageFile::getUpdateBy, updateBy)
                .set(StorageFile::getFileStatus, 1);

        return this.update(null, updateWrapper);
    }

    @Select(
            "SELECT file_id from storage_file WHERE file_status = 0 AND create_time <= DATE_SUB(NOW(), INTERVAL 24 HOUR)")
    Set<String> expiredTempFileIds();
}
