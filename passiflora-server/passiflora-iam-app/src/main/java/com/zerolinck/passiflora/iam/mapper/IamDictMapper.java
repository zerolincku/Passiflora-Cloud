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
package com.zerolinck.passiflora.iam.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zerolinck.passiflora.model.iam.entity.IamDict;
import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Collection;

/** @author linck on 2024-04-01 */
public interface IamDictMapper extends BaseMapper<IamDict> {
    @NotNull
    default Page<IamDict> page(
            @NotNull IPage<IamDict> page,
            @Param(Constants.WRAPPER) QueryWrapper<IamDict> searchWrapper,
            @Param("sortWrapper") QueryWrapper<IamDict> sortWrapper) {
        if (searchWrapper == null) {
            searchWrapper = new QueryWrapper<>();
        }
        searchWrapper.eq("del_flag", 0);

        if (sortWrapper == null
            || sortWrapper.getSqlSegment() == null
            || sortWrapper.getSqlSegment().isEmpty()) {
            searchWrapper.orderByAsc("dict_id");
        } else {
            searchWrapper.last(sortWrapper.getSqlSegment());
        }

        return (Page<IamDict>) this.selectPage(page, searchWrapper);
    }

    default int deleteByIds(@Param("dictIds") Collection<String> dictIds, @Param("updateBy") String updateBy) {
        if (dictIds == null || dictIds.isEmpty()) {
            return 0;
        }

        LambdaUpdateWrapper<IamDict> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(IamDict::getDictId, dictIds)
                .set(IamDict::getUpdateTime, LocalDateTime.now())
                .set(IamDict::getUpdateBy, updateBy)
                .set(IamDict::getDelFlag, 1);

        return this.update(null, updateWrapper);
    }
}
