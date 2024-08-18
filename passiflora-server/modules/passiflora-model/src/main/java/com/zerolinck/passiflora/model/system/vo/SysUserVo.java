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
package com.zerolinck.passiflora.model.system.vo;

import com.zerolinck.passiflora.model.system.entity.SysUser;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.Collection;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author linck
 * @since 2024-04-29
 */
@Data
@Schema(description = "用户")
@EqualsAndHashCode(callSuper = true)
public class SysUserVo extends SysUser {

    @Schema(description = "机构名称")
    private String orgName = "";

    @Schema(description = "职位ID集合")
    private Collection<String> positionIds = new ArrayList<>();

    @Schema(description = "职位名称集合")
    private Collection<String> positionNames = new ArrayList<>();

    @Schema(description = "角色ID集合")
    private Collection<String> roleIds = new ArrayList<>();

    @Schema(description = "角色名称集合")
    private Collection<String> roleNames = new ArrayList<>();
}
