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

import com.zerolinck.passiflora.common.exception.BizException;
import java.util.Collection;

/**
 * @author linck
 * @since 2024-03-18
 */
public class AssertUtil {

    public static void notEmpty(Collection<?> collection) {
        if (collection == null || collection.isEmpty()) {
            throw new BizException("参数不能为空");
        }
    }

    public static void notBlank(CharSequence charSequence) {
        if (charSequence == null || charSequence.isEmpty()) {
            throw new BizException("参数不能为空");
        }
    }

    public static void notNull(Object object) {
        if (object == null) {
            throw new BizException("参数不能为空");
        }
    }
}
