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
package com.zerolinck.passiflora.iam.mapper;

import java.util.Objects;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.zerolinck.passiflora.common.util.Condition;
import com.zerolinck.passiflora.model.iam.entity.IamDict;
import com.zerolinck.passiflora.mybatis.util.ConditionUtils;
import com.zerolinck.passiflora.mybatis.util.PageConvert;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** @author linck on 2024-04-01 */
public interface IamDictMapper extends BaseMapper<IamDict> {

    /**
     * 分页查询
     *
     * @param condition 查询条件
     * @author 林常坤 on 2025/2/7
     */
    @NotNull default com.zerolinck.passiflora.common.api.Page<IamDict> page(@Nullable Condition<IamDict> condition) {
        condition = Objects.requireNonNullElse(condition, new Condition<>());
        Page<IamDict> paginate = paginate(
                condition.getPageNum(),
                condition.getPageSize(),
                ConditionUtils.searchWrapper(condition, IamDict.class));
        return PageConvert.toPage(paginate);
    }
}
