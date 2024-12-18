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

import java.util.Collection;

import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zerolinck.passiflora.model.iam.entity.IamUser;

/** @author linck on 2024-02-07 */
public interface IamUserMapper extends BaseMapper<IamUser> {
    Page<IamUser> page(
            @Param("orgId") String orgId,
            IPage<IamUser> page,
            @Param(Constants.WRAPPER) QueryWrapper<IamUser> searchWrapper,
            @Param("sortWrapper") QueryWrapper<IamUser> sortWrapper);

    /** 使用更新删除，保证 update_by 和 update_time 正确 */
    int deleteByIds(@Param("userIds") Collection<String> userIds, @Param("updateBy") String updateBy);
}
