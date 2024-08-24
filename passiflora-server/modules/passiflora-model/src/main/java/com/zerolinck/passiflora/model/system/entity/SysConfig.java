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
package com.zerolinck.passiflora.model.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zerolinck.passiflora.model.common.BaseEntity;
import com.zerolinck.passiflora.model.valid.Insert;
import com.zerolinck.passiflora.model.valid.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

/**
 * 系统配置 Entity
 *
 * @author 林常坤
 * @since 2024-08-24
 */
@Data
@Schema(description = "系统配置")
@EqualsAndHashCode(callSuper = false)
public class SysConfig extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "配置ID", maxLength = 20)
    @Length(
            groups = {Insert.class, Update.class},
            max = 20,
            message = "配置ID长度不能大于20")
    @NotBlank(
            groups = {Update.class},
            message = "配置ID不能为空")
    private String configId;

    @Schema(description = "配置名称", maxLength = 100)
    @Length(
            groups = {Insert.class, Update.class},
            max = 100,
            message = "配置名称长度不能大于100")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "配置名称不能为空")
    private String configName;

    @Schema(description = "配置标识", maxLength = 100)
    @Length(
            groups = {Insert.class, Update.class},
            max = 100,
            message = "配置标识长度不能大于100")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "配置标识不能为空")
    private String configCode;

    @Schema(description = "配置内容", maxLength = 100)
    @Length(
            groups = {Insert.class, Update.class},
            max = 100,
            message = "配置内容长度不能大于100")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "配置内容不能为空")
    private String configValue;
}
