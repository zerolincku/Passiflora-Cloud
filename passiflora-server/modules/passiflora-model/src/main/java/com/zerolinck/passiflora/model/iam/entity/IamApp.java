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
package com.zerolinck.passiflora.model.iam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zerolinck.passiflora.model.common.BaseEntity;
import com.zerolinck.passiflora.model.common.enums.StatusEnum;
import com.zerolinck.passiflora.model.iam.enums.AppTypeEnum;
import com.zerolinck.passiflora.model.valid.Insert;
import com.zerolinck.passiflora.model.valid.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

/**
 * 应用 Entity
 *
 * @author linck
 * @since 2024-09-30
 */
@Data
@Schema(description = "应用")
@EqualsAndHashCode(callSuper = false)
public class IamApp extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "应用ID", maxLength = 20)
    @Length(
            groups = {Insert.class, Update.class},
            max = 20,
            message = "应用ID长度不能大于20")
    @NotBlank(
            groups = {Update.class},
            message = "应用ID不能为空")
    private String appId;

    @Schema(description = "应用名称", maxLength = 100)
    @Length(
            groups = {Insert.class, Update.class},
            max = 100,
            message = "应用名称长度不能大于100")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "应用名称不能为空")
    private String appName;

    @Schema(description = "应用令牌", maxLength = 100)
    @Length(
            groups = {Insert.class, Update.class},
            max = 100,
            message = "应用令牌长度不能大于100")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "应用令牌不能为空")
    private String appKey;

    @Schema(description = "应用秘钥", maxLength = 100)
    @Length(
            groups = {Insert.class, Update.class},
            max = 100,
            message = "应用秘钥长度不能大于100")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "应用秘钥不能为空")
    private String appSecret;

    @Schema(description = "应用图标", maxLength = 100)
    @Length(
            groups = {Insert.class, Update.class},
            max = 100,
            message = "应用图标长度不能大于100")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "应用图标不能为空")
    private String appIcon;

    @Schema(description = "应用地址", maxLength = 200)
    @Length(
            groups = {Insert.class, Update.class},
            max = 200,
            message = "应用地址长度不能大于200")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "应用地址不能为空")
    private String appUrl;

    @Schema(description = "应用状态")
    @NotNull(
            groups = {Insert.class, Update.class},
            message = "应用状态不能为空")
    private StatusEnum appStatus;

    @Schema(description = "应用类型")
    @NotNull(
            groups = {Insert.class, Update.class},
            message = "应用类型不能为空")
    private AppTypeEnum appType;

    @Schema(description = "应用描述", maxLength = 200)
    @Length(
            groups = {Insert.class, Update.class},
            max = 200,
            message = "应用描述长度不能大于200")
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "应用描述不能为空")
    private String appRemark;

    @Schema(description = "应用令牌有效期")
    private LocalDateTime appPeriod;
}
