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

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zerolinck.passiflora.model.iam.entity.IamDictItem;

/** @author linck on 2024-04-01 */
public interface IamDictItemMapper extends BaseMapper<IamDictItem> {
    default Page<IamDictItem> page(
            IPage<IamDictItem> page,
            @Param(Constants.WRAPPER) QueryWrapper<IamDictItem> searchWrapper,
            @Param("sortWrapper") QueryWrapper<IamDictItem> sortWrapper) {
        if (searchWrapper == null) {
            searchWrapper = new QueryWrapper<>();
        }
        searchWrapper.eq("del_flag", 0);

        if (sortWrapper == null
                || sortWrapper.getSqlSegment() == null
                || sortWrapper.getSqlSegment().isEmpty()) {
            searchWrapper.orderByAsc("dict_item_id");
        } else {
            searchWrapper.last(sortWrapper.getSqlSegment());
        }

        return (Page<IamDictItem>) this.selectPage(page, searchWrapper);
    }

    default int deleteByIds(@Param("dictItemIds") Collection<String> dictItemIds, @Param("updateBy") String updateBy) {
        if (dictItemIds == null || dictItemIds.isEmpty()) {
            return 0;
        }

        LambdaUpdateWrapper<IamDictItem> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(IamDictItem::getDictItemId, dictItemIds)
                .set(IamDictItem::getUpdateTime, LocalDateTime.now())
                .set(IamDictItem::getUpdateBy, updateBy)
                .set(IamDictItem::getDelFlag, 1);

        return this.update(null, updateWrapper);
    }

    default int deleteByDictIds(@Param("dictIds") Collection<String> dictIds, @Param("updateBy") String updateBy) {
        if (dictIds == null || dictIds.isEmpty()) {
            return 0;
        }

        LambdaUpdateWrapper<IamDictItem> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(IamDictItem::getDictId, dictIds)
                .set(IamDictItem::getUpdateTime, LocalDateTime.now())
                .set(IamDictItem::getUpdateBy, updateBy)
                .set(IamDictItem::getDelFlag, 1);

        return this.update(null, updateWrapper);
    }

    @Select("SELECT * FROM iam_dict_item WHERE del_flag = 0 AND dict_id = #{dictId}")
    List<IamDictItem> listByDictId(@Param("dictId") String dictId);

    @Select("SELECT * FROM iam_dict_item WHERE del_flag = 0 AND dict_id = (SELECT dict_id from"
            + " iam_dict WHERE dict_name = #{dictName})")
    List<IamDictItem> listByDictName(@Param("dictName") String dictName);

    @Select("SELECT * FROM iam_dict_item WHERE del_flag = 0 AND dict_id = (SELECT dict_id from"
            + " iam_dict WHERE dict_tag = #{dictTag})")
    List<IamDictItem> listByDictTag(@Param("dictTag") String dictTag);
}
