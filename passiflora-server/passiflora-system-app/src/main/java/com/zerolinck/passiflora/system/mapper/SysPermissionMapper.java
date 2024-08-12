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
package com.zerolinck.passiflora.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zerolinck.passiflora.model.system.entity.SysPermission;
import com.zerolinck.passiflora.model.system.vo.SysPermissionTableVo;
import jakarta.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author linck
 * @since 2024-05-06
 */
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
    Page<SysPermission> page(
            IPage<SysPermission> page,
            @Param(Constants.WRAPPER) QueryWrapper<SysPermission> searchWrapper,
            @Param("sortWrapper") QueryWrapper<SysPermission> sortWrapper);

    /** 使用更新删除，保证 update_by 和 update_time 正确 */
    int deleteByIds(@Param("permissionIds") Collection<String> permissionIds, @Param("updateBy") String updateBy);

    @Update("UPDATE sys_permission SET \"order\" = #{sysPermissionTableVo.order} WHERE"
            + " permission_id = #{sysPermissionTableVo.permissionId} ")
    void updateOrder(@Param("sysPermissionTableVo") SysPermissionTableVo sysPermissionTableVo);

    void disable(@Param("permissionIds") Collection<String> permissionIds, @Param("updateBy") String updateBy);

    void enable(@Param("permissionIds") Collection<String> permissionIds, @Param("updateBy") String updateBy);

    @Select("SELECT * FROM sys_permission WHERE del_flag = 0 AND permission_parent_id = #{permissionParentId} ORDER BY"
            + " permission_level , \"order\", permission_title")
    List<SysPermission> listByParentId(@Param("permissionParentId") String permissionParentId);

    @Select(
            "SELECT * FROM sys_permission WHERE del_flag = 0 AND permission_id_path like concat('%', #{permissionId}, '%') ORDER BY"
                    + " permission_level , \"order\", permission_title")
    List<SysPermission> listSelfAndSub(@Param("permissionId") String permissionId);

    @Nonnull
    List<SysPermission> listByPositionId(@Nonnull @Param("positionId") String positionId);

    @Nonnull
    List<SysPermission> listByUserId(@Nonnull @Param("userId") String userId);
}
