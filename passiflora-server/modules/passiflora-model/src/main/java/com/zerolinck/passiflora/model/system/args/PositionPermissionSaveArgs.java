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
package com.zerolinck.passiflora.model.system.args;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author linck
 * @since 2024-06-29
 */
@Data
public class PositionPermissionSaveArgs {

    @Schema(description = "职位ID", maxLength = 20)
    @Length(max = 20, message = "职位ID长度不能大于20") @NotBlank(message = "职位ID不能为空")
    private String positionId;

    private List<String> permissionIds;
}
