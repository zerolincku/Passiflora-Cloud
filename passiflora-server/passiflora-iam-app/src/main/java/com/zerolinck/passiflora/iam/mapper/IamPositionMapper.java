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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.zerolinck.passiflora.base.enums.StatusEnum;
import com.zerolinck.passiflora.model.iam.entity.IamPosition;
import com.zerolinck.passiflora.model.iam.resp.IamPositionResp;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.Nullable;

/**
 * 职位 Mybatis Mapper
 *
 * @author linck
 * @since 2024-05-14
 */
public interface IamPositionMapper extends BaseMapper<IamPosition> {

    /**
     * 根据职位名称查询职位
     *
     * @param positionName 职位名称
     * @return 职位对象
     * @since 2024-05-14
     */
    @SuppressWarnings("unused")
    default IamPosition selectByPositionName(String positionName) {
        return selectOneByCondition(IAM_POSITION.POSITION_NAME.eq(positionName));
    }

    /**
     * 根据父职位ID查询子职位列表
     *
     * @param positionParentId 父职位ID
     * @return 子职位列表
     * @since 2024-05-14
     */
    default List<IamPositionResp> listByParentId(String positionParentId) {
        return selectListByQueryAs(
                QueryWrapper.create()
                        .where(IAM_POSITION.PARENT_POSITION_ID.eq(positionParentId))
                        .orderBy(IAM_POSITION.POSITION_LEVEL, true)
                        .orderBy(IAM_POSITION.ORDER, true)
                        .orderBy(IAM_POSITION.POSITION_NAME, true),
                IamPositionResp.class);
    }

    /**
     * 禁用职位
     *
     * @param positionIds 职位ID集合
     * @return 如果禁用成功返回true，否则返回false
     * @since 2024-05-14
     */
    default boolean disable(Collection<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return true;
        }

        return UpdateChain.of(IamPosition.class)
                .set(IamPosition::getPositionStatus, StatusEnum.DISABLE)
                .in(IamPosition::getPositionId, positionIds)
                .update();
    }

    /**
     * 启用职位
     *
     * @param positionIds 职位ID集合
     * @return 如果启用成功返回true，否则返回false
     * @since 2024-05-14
     */
    default boolean enable(Collection<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return true;
        }

        return UpdateChain.of(IamPosition.class)
                .set(IamPosition::getPositionStatus, StatusEnum.ENABLE)
                .in(IamPosition::getPositionId, positionIds)
                .update();
    }

    /**
     * 更新职位顺序
     *
     * @param iamPositionResp 职位表响应对象
     * @since 2024-05-14
     */
    default void updateOrder(IamPositionResp iamPositionResp) {
        UpdateChain.of(IamPosition.class)
                .set(IamPosition::getOrder, iamPositionResp.getOrder())
                .eq(IamPosition::getPositionId, iamPositionResp.getPositionId())
                .update();
    }

    default List<IamPosition> listByPositionIds(@Nullable Collection<String> positionIds) {
        if (CollectionUtils.isEmpty(positionIds)) {
            return new ArrayList<>();
        }
        return selectListByQuery(QueryWrapper.create().where(IAM_POSITION.POSITION_ID.in(positionIds)));
    }
}
