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

import static com.mybatisflex.core.query.QueryMethods.select;
import static com.zerolinck.passiflora.model.iam.entity.table.IamPermissionTableDef.IAM_PERMISSION;
import static com.zerolinck.passiflora.model.iam.entity.table.IamPositionPermissionTableDef.IAM_POSITION_PERMISSION;

import java.util.Collection;
import java.util.List;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.zerolinck.passiflora.base.enums.DelFlagEnum;
import com.zerolinck.passiflora.base.enums.StatusEnum;
import com.zerolinck.passiflora.model.iam.entity.IamPositionPermission;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 职位权限 Mybatis Mapper
 *
 * @since 2024-05-06
 */
public interface IamPositionPermissionMapper extends BaseMapper<IamPositionPermission> {

    /**
     * 根据职位ID集合删除职位权限
     *
     * @param positionIds 职位ID集合
     * @return 删除的职位权限数量
     * @since 2024-05-06
     */
    default int deleteByPositionIds(@Nullable Collection<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return 0;
        }

        return this.deleteByQuery(new QueryWrapper().in(IamPositionPermission::getPositionId, positionIds));
    }

    /**
     * 根据职位ID集合查询权限ID集合
     *
     * @param positionIds 职位ID集合
     * @return 权限ID集合
     * @since 2024-05-06
     */
    default List<String> permissionIdsByPositionIds(@NotNull List<String> positionIds) {
        return this.selectListByQueryAs(
                QueryWrapper.create()
                        .select(IAM_PERMISSION.PERMISSION_ID)
                        .from(IAM_PERMISSION)
                        .where(IAM_PERMISSION.PERMISSION_STATUS.eq(StatusEnum.ENABLE))
                        .and(IAM_PERMISSION.DEL_FLAG.eq(DelFlagEnum.NOT_DELETE))
                        .and(IAM_PERMISSION.PERMISSION_ID.in(select(IAM_POSITION_PERMISSION.PERMISSION_ID)
                                .from(IAM_POSITION_PERMISSION)
                                .where(IAM_POSITION_PERMISSION.DEL_FLAG.eq(DelFlagEnum.NOT_DELETE))
                                .and(IAM_POSITION_PERMISSION.POSITION_ID.in(positionIds)))),
                String.class);
    }
}
