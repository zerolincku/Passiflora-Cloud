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
import com.zerolinck.passiflora.model.iam.valid.Login;
import com.zerolinck.passiflora.model.valid.Insert;
import com.zerolinck.passiflora.model.valid.OnlyField;
import com.zerolinck.passiflora.model.valid.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

/**
 * @author linck
 * @since 2024-03-19
 */
@Data
@Schema(description = "用户")
@EqualsAndHashCode(callSuper = false)
public class IamUser extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键", maxLength = 20)
    @Length(
            groups = {Update.class},
            max = 20,
            message = "主键长度不能大于20")
    @NotBlank(
            groups = {Update.class},
            message = "主键不能为空")
    private String userId;

    @OnlyField
    @Schema(description = "用户名", maxLength = 50)
    @Length(
            groups = {Insert.class},
            max = 50,
            message = "用户名长度不能大于50")
    @NotBlank(
            groups = {Insert.class},
            message = "用户名不能为空")
    private String userName;

    @OnlyField
    @Schema(description = "真实姓名", maxLength = 50)
    @Length(
            groups = {Insert.class, Update.class},
            max = 50,
            message = "用户名长度不能大于50")
    @NotBlank(
            groups = {Insert.class},
            message = "用户名不能为空")
    private String realName;

    /** 取值参见字典：gender */
    @Schema(description = "性别")
    private Integer gender;

    @OnlyField
    @Schema(description = "身份证号", maxLength = 50)
    @Pattern(
            groups = {Insert.class, Update.class},
            regexp = "^\\d{17}[\\dxX]$",
            message = "身份证号格式错误")
    private String idCardNo;

    @Schema(description = "出生日期")
    private LocalDate dateOfBirth;

    @OnlyField
    @Schema(description = "手机号", maxLength = 30)
    @Length(
            groups = {Insert.class, Update.class},
            max = 30,
            message = "手机号长度不能大于30")
    private String phoneNum;

    @Schema(description = "电子邮箱", maxLength = 50)
    @Length(
            groups = {Insert.class, Update.class},
            max = 50,
            message = "电子邮箱长度不能大于50")
    private String email;

    @Schema(description = "备注", maxLength = 200)
    @Length(
            groups = {Insert.class, Update.class},
            max = 200,
            message = "备注长度不能大于200")
    private String remark;

    @Schema(description = "用户密码", maxLength = 50)
    @Length(
            groups = {Login.class},
            max = 50,
            message = "用户密码长度不能大于50")
    @NotBlank(
            groups = {Login.class},
            message = "用户密码不能为空")
    private String userPassword;

    @Schema(description = "密码盐", maxLength = 50)
    private String salt;

    @Schema(description = "用户头像", maxLength = 20)
    @Length(
            groups = {Insert.class, Update.class},
            max = 20,
            message = "用户头像长度不能大于20")
    private String avatarFile;

    @Schema(description = "所属机构", maxLength = 20)
    @NotBlank(
            groups = {Insert.class, Update.class},
            message = "所属机构不能为空")
    @Length(
            groups = {Insert.class, Update.class},
            max = 20,
            message = "用户头像长度不能大于20")
    private String orgId;
}
