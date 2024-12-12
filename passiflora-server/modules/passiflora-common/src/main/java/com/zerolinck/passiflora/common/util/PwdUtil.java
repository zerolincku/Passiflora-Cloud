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
package com.zerolinck.passiflora.common.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

import lombok.experimental.UtilityClass;

/**
 * 密码工具类
 *
 * @author 林常坤 on 2024-08-13
 */
@UtilityClass
public class PwdUtil {

    /**
     * 生成 hash 密码，前十位是 salt
     *
     * @param password 明文密码
     * @since 2024-08-13
     */
    @NotNull public static String hashPassword(@NotNull String password) {
        String salt = RandomStringUtils.random(10, true, true);
        String hashPwd = DigestUtils.sha1Hex(salt + password);
        return salt + hashPwd;
    }

    /**
     * 校验密码是否正确
     *
     * @param password 明文密码
     * @param hashPwd hash 密码
     * @since 2024-08-13
     */
    public static boolean verifyPassword(@NotNull String password, @NotNull String hashPwd) {
        String salt = hashPwd.substring(0, 10);
        return hashPwd.equals(salt + DigestUtils.sha1Hex(salt + password));
    }
}
