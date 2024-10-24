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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zerolinck.passiflora.model.iam.entity.IamPosition;
import com.zerolinck.passiflora.model.iam.vo.IamPositionVo;
import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/** @author linck on 2024-05-14 */
public interface IamPositionMapper extends BaseMapper<IamPosition> {
    Page<IamPosition> page(
            IPage<IamPosition> page,
            @Param(Constants.WRAPPER) QueryWrapper<IamPosition> searchWrapper,
            @Param("sortWrapper") QueryWrapper<IamPosition> sortWrapper);

    /** 使用更新删除，保证 update_by 和 update_time 正确 */
    int deleteByIds(@Param("positionIds") Collection<String> positionIds, @Param("updateBy") String updateBy);

    @SuppressWarnings("unused")
    @Select("SELECT * FROM iam_position WHERE del_flag = 0 AND position_name = #{positionName}")
    IamPosition selectByPositionName(@Param("positionName") String positionName);

    @Select("SELECT * FROM iam_position WHERE del_flag = 0 AND parent_position_id = #{positionParentId} ORDER BY"
            + " position_level , \"order\", position_name")
    List<IamPositionVo> listByParentId(@Param("positionParentId") String positionParentId);

    void disable(@Param("positionIds") Collection<String> positionIds, @Param("updateBy") String updateBy);

    void enable(@Param("positionIds") Collection<String> positionIds, @Param("updateBy") String updateBy);

    @Update("UPDATE iam_position SET \"order\" = #{iamPositionVo.order} WHERE"
            + " position_id = #{iamPositionVo.positionId} ")
    void updateOrder(@Param("iamPositionVo") IamPositionVo iamPositionVo);
}
