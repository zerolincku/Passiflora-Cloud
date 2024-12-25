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

import static com.zerolinck.passiflora.model.iam.entity.table.IamPositionTableDef.IAM_POSITION;

import java.util.Collection;
import java.util.List;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.zerolinck.passiflora.base.enums.StatusEnum;
import com.zerolinck.passiflora.model.iam.entity.IamPosition;
import com.zerolinck.passiflora.model.iam.resp.IamPositionResp;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;

/** @author linck on 2024-05-14 */
public interface IamPositionMapper extends BaseMapper<IamPosition> {
    @SuppressWarnings("unused")
    default IamPosition selectByPositionName(@Param("positionName") String positionName) {
        return selectOneByCondition(IAM_POSITION.POSITION_NAME.eq(positionName));
    }

    default List<IamPositionResp> listByParentId(@Param("positionParentId") String positionParentId) {
        return selectListByQueryAs(
                QueryWrapper.create()
                        .where(IAM_POSITION.PARENT_POSITION_ID.eq(positionParentId))
                        .orderBy(IAM_POSITION.POSITION_LEVEL, true)
                        .orderBy(IAM_POSITION.ORDER, true)
                        .orderBy(IAM_POSITION.POSITION_NAME, true),
                IamPositionResp.class);
    }

    default boolean disable(Collection<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return true;
        }

        return UpdateChain.of(IamPosition.class)
                .set(IamPosition::getPositionStatus, StatusEnum.DISABLE)
                .in(IamPosition::getPositionId, positionIds)
                .update();
    }

    default boolean enable(Collection<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return true;
        }

        return UpdateChain.of(IamPosition.class)
                .set(IamPosition::getPositionStatus, StatusEnum.ENABLE)
                .in(IamPosition::getPositionId, positionIds)
                .update();
    }

    default void updateOrder(@Param("iamPositionResp") IamPositionResp iamPositionResp) {
        UpdateChain.of(IamPosition.class)
                .set(IamPosition::getOrder, iamPositionResp.getOrder())
                .eq(IamPosition::getPositionId, iamPositionResp.getPositionId())
                .update();
    }
}
