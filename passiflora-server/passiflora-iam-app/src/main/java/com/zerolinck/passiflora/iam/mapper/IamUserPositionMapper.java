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

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.zerolinck.passiflora.model.iam.entity.IamUserPosition;
import com.zerolinck.passiflora.model.iam.resp.IamUserPositionResp;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** @author linck on 2024-05-14 */
public interface IamUserPositionMapper extends BaseMapper<IamUserPosition> {

    @NotNull @Select(
            """
            SELECT a.user_id, b.position_id, b.position_name FROM
                 (SELECT user_id, position_id
                    FROM iam_user_position
                    WHERE del_flag = 0 AND user_id IN
                    <foreach item="userId" index="index" collection="userIds" open="(" separator="," close=")">
                        #{userId}
                    </foreach>
                ) as a
                INNER JOIN iam_position as b ON a.position_id = b.position_id
                WHERE b.del_flag = 0 AND b.position_status = 1
    """)
    List<IamUserPositionResp> selectByUserIds(@NotNull @Param("userIds") Collection<String> userIds);

    default int deleteByUserIds(@NotNull @Param("userIds") Collection<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return 0;
        }

        return this.deleteByQuery(new QueryWrapper().in(IamUserPosition::getUserId, userIds));
    }

    default int deleteByPositionIds(@NotNull @Param("positionIds") Collection<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return 0;
        }

        return this.deleteByQuery(new QueryWrapper().in(IamUserPosition::getPositionId, positionIds));
    }

    default int deleteByUserIdAndPositionIds(
            @NotNull @Param("userId") String userId, @Nullable @Param("positionIds") Collection<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return 0;
        }

        return this.deleteByQuery(new QueryWrapper()
                .eq(IamUserPosition::getUserId, userId)
                .in(IamUserPosition::getPositionId, positionIds));
    }
}
