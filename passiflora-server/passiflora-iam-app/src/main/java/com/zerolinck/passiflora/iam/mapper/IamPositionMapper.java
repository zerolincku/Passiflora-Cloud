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

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zerolinck.passiflora.model.iam.entity.IamPosition;
import com.zerolinck.passiflora.model.iam.resp.IamPositionResp;

/** @author linck on 2024-05-14 */
public interface IamPositionMapper extends BaseMapper<IamPosition> {
    default Page<IamPosition> page(
            IPage<IamPosition> page,
            @Param(Constants.WRAPPER) QueryWrapper<IamPosition> searchWrapper,
            @Param("sortWrapper") QueryWrapper<IamPosition> sortWrapper) {
        if (searchWrapper == null) {
            searchWrapper = new QueryWrapper<>();
        }
        searchWrapper.eq("del_flag", 0);

        if (sortWrapper == null
                || sortWrapper.getSqlSegment() == null
                || sortWrapper.getSqlSegment().isEmpty()) {
            searchWrapper.orderByAsc("position_level", "\"order\"", "position_id");
        } else {
            searchWrapper.last(sortWrapper.getSqlSegment());
        }

        return (Page<IamPosition>) this.selectPage(page, searchWrapper);
    }

    default int deleteByIds(Collection<String> positionIds, String updateBy) {
        if (positionIds == null || positionIds.isEmpty()) {
            return 0;
        }

        LambdaUpdateWrapper<IamPosition> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(IamPosition::getPositionId, positionIds)
                .set(IamPosition::getUpdateTime, LocalDateTime.now())
                .set(IamPosition::getUpdateBy, updateBy)
                .set(IamPosition::getDelFlag, 1);

        return this.update(null, updateWrapper);
    }

    @SuppressWarnings("unused")
    @Select("SELECT * FROM iam_position WHERE del_flag = 0 AND position_name = #{positionName}")
    IamPosition selectByPositionName(@Param("positionName") String positionName);

    @Select("SELECT * FROM iam_position WHERE del_flag = 0 AND parent_position_id = #{positionParentId} ORDER BY"
            + " position_level , \"order\", position_name")
    List<IamPositionResp> listByParentId(@Param("positionParentId") String positionParentId);

    default void disable(Collection<String> positionIds, String updateBy) {
        if (positionIds == null || positionIds.isEmpty()) {
            return;
        }

        LambdaUpdateWrapper<IamPosition> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(IamPosition::getPositionIdPath, positionIds)
                .set(IamPosition::getUpdateTime, LocalDateTime.now())
                .set(IamPosition::getUpdateBy, updateBy)
                .set(IamPosition::getPositionStatus, 0);

        this.update(null, updateWrapper);
    }

    default void enable(Collection<String> positionIds, String updateBy) {
        if (positionIds == null || positionIds.isEmpty()) {
            return;
        }

        LambdaUpdateWrapper<IamPosition> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(IamPosition::getPositionId, positionIds)
                .set(IamPosition::getUpdateTime, LocalDateTime.now())
                .set(IamPosition::getUpdateBy, updateBy)
                .set(IamPosition::getPositionStatus, 1);

        this.update(null, updateWrapper);
    }

    @Update("UPDATE iam_position SET \"order\" = #{iamPositionResp.order} WHERE"
            + " position_id = #{iamPositionResp.positionId} ")
    void updateOrder(@Param("iamPositionResp") IamPositionResp iamPositionResp);
}
