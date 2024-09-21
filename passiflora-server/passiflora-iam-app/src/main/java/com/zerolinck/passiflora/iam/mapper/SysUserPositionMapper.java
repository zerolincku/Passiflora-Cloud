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

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zerolinck.passiflora.model.iam.entity.SysUserPosition;
import com.zerolinck.passiflora.model.iam.vo.SysUserPositionVo;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @author linck
 * @since 2024-05-14
 */
public interface SysUserPositionMapper extends BaseMapper<SysUserPosition> {

    @Nonnull
    List<SysUserPositionVo> selectByUserIds(@Nonnull @Param("userIds") Collection<String> userIds);

    /** 使用更新删除，保证 update_by 和 update_time 正确 */
    int deleteByIds(@Nonnull @Param("bindIds") Collection<String> bindIds, @Nonnull @Param("updateBy") String updateBy);

    int deleteByUserIds(
            @Nonnull @Param("userIds") Collection<String> userIds, @Nullable @Param("updateBy") String updateBy);

    int deleteByPositionIds(
            @Nonnull @Param("positionIds") Collection<String> positionIds,
            @Nullable @Param("updateBy") String updateBy);

    int deleteByUserIdAndPositionIds(
            @Nonnull @Param("userId") String userId,
            @Nonnull @Param("positionIds") Collection<String> positionIds,
            @Nullable @Param("updateBy") String updateBy);
}
