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
package com.zerolinck.passiflora.model.iam.resp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/** @author linck on 2024-04-29 */
@Data
@Schema(description = "用户")
public class IamUserResp {

    @Schema(description = "主键")
    private String userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "真实姓名")
    private String realName;

    /** 取值参见字典：gender */
    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "身份证号")
    private String idCardNo;

    @Schema(description = "出生日期")
    private LocalDate dateOfBirth;

    @Schema(description = "手机号")
    private String phoneNum;

    @Schema(description = "电子邮箱")
    private String email;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "用户密码")
    private String userPassword;

    @Schema(description = "用户头像")
    private String avatarFile;

    @Schema(description = "所属机构")
    private String orgId;

    @Schema(description = "乐观锁版本")
    private Long version;

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
