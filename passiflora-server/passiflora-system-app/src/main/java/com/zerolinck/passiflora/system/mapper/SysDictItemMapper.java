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
package com.zerolinck.passiflora.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zerolinck.passiflora.model.system.entity.SysDictItem;
import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author linck
 * @since 2024-04-01
 */
public interface SysDictItemMapper extends BaseMapper<SysDictItem> {
    Page<SysDictItem> page(
            IPage<SysDictItem> page,
            @Param(Constants.WRAPPER) QueryWrapper<SysDictItem> searchWrapper,
            @Param("sortWrapper") QueryWrapper<SysDictItem> sortWrapper);

    /** 使用更新删除，保证 update_by 和 update_time 正确 */
    int deleteByIds(@Param("dictItemIds") Collection<String> dictItemIds, @Param("updateBy") String updateBy);

    int deleteByDictIds(@Param("dictIds") Collection<String> dictIds, @Param("updateBy") String updateBy);

    @Select("SELECT * FROM sys_dict_item WHERE del_flag = 0 AND dict_id = #{dictId}")
    List<SysDictItem> listByDictId(@Param("dictId") String dictId);

    @Select("SELECT * FROM sys_dict_item WHERE del_flag = 0 AND dict_id = (SELECT dict_id from"
            + " sys_dict WHERE dict_name = #{dictName})")
    List<SysDictItem> listByDictName(@Param("dictName") String dictName);

    @Select("SELECT * FROM sys_dict_item WHERE del_flag = 0 AND dict_id = (SELECT dict_id from"
            + " sys_dict WHERE dict_tag = #{dictTag})")
    List<SysDictItem> listByDictTag(@Param("dictTag") String dictTag);
}
