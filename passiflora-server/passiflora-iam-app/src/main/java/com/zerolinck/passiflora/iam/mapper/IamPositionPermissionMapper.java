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
import com.zerolinck.passiflora.model.iam.entity.IamPositionPermission;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** @author linck on 2024-05-06 */
public interface IamPositionPermissionMapper extends BaseMapper<IamPositionPermission> {

    default int deleteByPositionIds(@Nullable @Param("positionIds") Collection<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return 0;
        }

        return this.deleteByQuery(new QueryWrapper().in(IamPositionPermission::getPositionId, positionIds));
    }

    @NotNull @Select(
            """
        SELECT permission_id from iam_permission
        WHERE permission_status = 1 AND del_flag = 0
        AND permission_id IN
            (SELECT permission_id from iam_position_permission
            WHERE del_flag = 0 AND position_id IN
            (SELECT position_id from iam_position_permission
            WHERE del_flag = 0 AND position_id IN (#{positionIds})))""")
    List<String> permissionIdsByPositionIds(@NotNull @Param("positionIds") List<String> positionIds);
}
