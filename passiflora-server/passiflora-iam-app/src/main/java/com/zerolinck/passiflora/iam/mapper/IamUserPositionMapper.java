/* 
 * Copyright (C) 2025 Linck. <zerolinck@foxmail.com>
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.zerolinck.passiflora.base.enums.DelFlagEnum;
import com.zerolinck.passiflora.base.enums.StatusEnum;
import com.zerolinck.passiflora.model.iam.entity.IamUserPosition;
import com.zerolinck.passiflora.model.iam.entity.table.IamPositionTableDef;
import com.zerolinck.passiflora.model.iam.entity.table.IamUserPositionTableDef;
import com.zerolinck.passiflora.model.iam.resp.IamUserPositionResp;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 用户职位 Mybatis Mapper
 *
 * @since 2024-05-14
 */
public interface IamUserPositionMapper extends BaseMapper<IamUserPosition> {

    /**
     * 根据用户ids获取所有关联
     *
     * @author 林常坤 on 2025/2/6
     */
    @NotNull default List<IamUserPosition> listByUserIds(@Nullable Collection<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return new ArrayList<>();
        }
        return selectListByQuery(new QueryWrapper().in(IamUserPosition::getUserId, userIds));
    }

    /**
     * 根据职位ids获取所有关联
     *
     * @author 林常坤 on 2025/2/6
     */
    @NotNull default List<IamUserPosition> listByPositionIds(@Nullable Collection<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return new ArrayList<>();
        }
        return selectListByQuery(new QueryWrapper().in(IamUserPosition::getPositionId, positionIds));
    }

    /**
     * 根据用户ID集合查询用户职位响应对象列表
     *
     * @param userIds 用户ID集合
     * @return 用户职位响应对象列表
     * @since 2024-05-14
     */
    @NotNull default List<IamUserPositionResp> selectByUserIds(@NotNull Collection<String> userIds) {
        IamPositionTableDef p = IamPositionTableDef.IAM_POSITION.as("p");
        IamUserPositionTableDef up = IamUserPositionTableDef.IAM_USER_POSITION.as("up");

        return selectListByQueryAs(
                QueryWrapper.create()
                        .select(up.USER_ID, p.POSITION_ID, p.POSITION_NAME)
                        .from(p)
                        .innerJoin(up)
                        .on(up.POSITION_ID.eq(p.POSITION_ID))
                        .where(p.DEL_FLAG.eq(DelFlagEnum.NOT_DELETE))
                        .where(p.POSITION_STATUS.eq(StatusEnum.ENABLE))
                        .and(up.USER_ID.in(userIds)),
                IamUserPositionResp.class);
    }

    /**
     * 根据用户ID集合删除用户职位
     *
     * @param userIds 用户ID集合
     * @return 删除的用户职位数量
     * @since 2024-05-14
     */
    default int deleteByUserIds(@NotNull Collection<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return 0;
        }

        return this.deleteByQuery(new QueryWrapper().in(IamUserPosition::getUserId, userIds));
    }

    /**
     * 根据职位ID集合删除用户职位
     *
     * @param positionIds 职位ID集合
     * @return 删除的用户职位数量
     * @since 2024-05-14
     */
    default int deleteByPositionIds(@NotNull Collection<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return 0;
        }

        return this.deleteByQuery(new QueryWrapper().in(IamUserPosition::getPositionId, positionIds));
    }

    /**
     * 根据用户ID和职位ID集合删除用户职位
     *
     * @param userId 用户ID
     * @param positionIds 职位ID集合
     * @return 删除的用户职位数量
     * @since 2024-05-14
     */
    default int deleteByUserIdAndPositionIds(@NotNull String userId, @Nullable Collection<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return 0;
        }

        return this.deleteByQuery(new QueryWrapper()
                .eq(IamUserPosition::getUserId, userId)
                .in(IamUserPosition::getPositionId, positionIds));
    }
}
