/* 
 * Copyright (C) 2025 Linck. <zerolinck@foxmail.com>
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
package com.zerolinck.passiflora.base;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

/** @author 林常坤 on 2024/12/24 */
public interface IUser {
    /** 获取用户ID */
    String getUserId();

    /** 获取用户名 */
    String getUserName();

    /** 获取真实姓名 */
    String getRealName();

    /** 获取性别 */
    Integer getGender();

    /** 获取身份证号 */
    String getIdCardNo();

    /** 获取出生日期 */
    LocalDate getDateOfBirth();

    /** 获取手机号 */
    String getPhoneNum();

    /** 获取电子邮箱 */
    String getEmail();

    /** 获取备注 */
    String getRemark();

    /** 获取用户密码 */
    String getUserPassword();

    /** 获取用户头像 */
    String getAvatarFile();

    /** 获取所属机构 */
    String getOrgId();

    @Getter
    @Setter
    class UserInfo implements IUser {
        /** 主键 */
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
}
