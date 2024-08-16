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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author linck
 * @since 2024-03-18
 */
public class AssertUtil {

    public static void notEmpty(@Nullable Collection<?> collection, @Nonnull String message) {
        if (collection == null || collection.isEmpty()) {
            throw new BizException(message);
        }
    }

    public static void notBlank(@Nullable CharSequence charSequence, @Nonnull String message) {
        if (charSequence == null || charSequence.isEmpty()) {
            throw new BizException(message);
        }
    }

    public static void notNull(@Nullable Object object, @Nonnull String message) {
        if (object == null) {
            throw new BizException(message);
        }
    }

    public static void notNull(@Nullable Object object, @Nonnull String message, @Nonnull Object... args) {
        if (object == null) {
            throw new BizException(message, args);
        }
    }
}
