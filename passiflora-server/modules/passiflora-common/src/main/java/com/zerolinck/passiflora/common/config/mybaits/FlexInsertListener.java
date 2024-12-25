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
package com.zerolinck.passiflora.common.config.mybaits;

import com.mybatisflex.annotation.InsertListener;
import com.zerolinck.passiflora.base.BaseEntity;
import com.zerolinck.passiflora.common.util.CurrentUtil;

/** @author 林常坤 on 2024/12/24 */
public class FlexInsertListener implements InsertListener {
    /**
     * 新增操作的前置操作。
     *
     * @param entity 实体类
     */
    @Override
    public void onInsert(Object entity) {
        String userId = CurrentUtil.getCurrentUserId();
        if (userId != null && entity instanceof BaseEntity baseEntity) {
            baseEntity.setCreateBy(userId);
            baseEntity.setUpdateBy(userId);
        }
    }
}
