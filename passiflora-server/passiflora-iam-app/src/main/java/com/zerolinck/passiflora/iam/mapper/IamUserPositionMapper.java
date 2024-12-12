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
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zerolinck.passiflora.model.iam.entity.IamUserPosition;
import com.zerolinck.passiflora.model.iam.resp.IamUserPositionResp;

/** @author linck on 2024-05-14 */
public interface IamUserPositionMapper extends BaseMapper<IamUserPosition> {

    @NotNull List<IamUserPositionResp> selectByUserIds(@NotNull @Param("userIds") Collection<String> userIds);

    /** 真实删除 */
    int deleteByIds(@NotNull @Param("ids") Collection<String> ids, @NotNull @Param("updateBy") String updateBy);

    /** 真实删除 */
    int deleteByUserIds(
            @NotNull @Param("userIds") Collection<String> userIds, @Nullable @Param("updateBy") String updateBy);

    /** 真实删除 */
    int deleteByPositionIds(
            @NotNull @Param("positionIds") Collection<String> positionIds,
            @Nullable @Param("updateBy") String updateBy);

    /** 真实删除 */
    int deleteByUserIdAndPositionIds(
            @NotNull @Param("userId") String userId,
            @NotNull @Param("positionIds") Collection<String> positionIds,
            @Nullable @Param("updateBy") String updateBy);
}
