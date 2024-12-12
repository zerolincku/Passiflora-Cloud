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

import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zerolinck.passiflora.model.common.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** @author linck on 2024-03-19 */
@Data
@Schema(description = "用户")
@EqualsAndHashCode(callSuper = false)
public class IamUser extends BaseEntity {
    /** 主键 */
    @TableId(type = IdType.ASSIGN_ID)
    private String userId;
    /** 用户名 */
    private String userName;
    /** 真实姓名 */
    private String realName;
    /** 性别,取值参见字典：gender */
    private Integer gender;
    /** 身份证号 */
    private String idCardNo;
    /** 出生日期 */
    private LocalDate dateOfBirth;
    /** 手机号 */
    private String phoneNum;
    /** 电子邮箱 */
    private String email;
    /** 备注 */
    private String remark;
    /** 用户密码 */
    private String userPassword;
    /** 用户头像 */
    private String avatarFile;
    /** 所属机构 */
    private String orgId;
}
