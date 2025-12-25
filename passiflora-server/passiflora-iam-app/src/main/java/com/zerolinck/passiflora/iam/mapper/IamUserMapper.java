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

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.zerolinck.passiflora.common.util.Condition;
import com.zerolinck.passiflora.model.iam.entity.IamUser;
import com.zerolinck.passiflora.mybatis.util.ConditionUtils;
import com.zerolinck.passiflora.mybatis.util.PageConvert;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.zerolinck.passiflora.model.iam.entity.table.IamOrgTableDef.IAM_ORG;
import static com.zerolinck.passiflora.model.iam.entity.table.IamUserTableDef.IAM_USER;

/** @author linck on 2024-02-07 */
public interface IamUserMapper extends BaseMapper<IamUser> {

    /**
     * 分页查询
     *
     * @param condition 查询条件
     * @author 林常坤 on 2025/2/7
     */
    @NotNull default com.zerolinck.passiflora.common.api.Page<IamUser> page(@Nullable Condition<IamUser> condition, @Nullable String orgId) {
        condition = Objects.requireNonNullElse(condition, new Condition<>());
        QueryWrapper queryWrapper = ConditionUtils.searchWrapper(condition, IamUser.class);
        if (StringUtils.isNotBlank(orgId)) {
            queryWrapper.in(
                    IAM_ORG.ORG_ID.getName(),
                    QueryWrapper.create().from(IAM_ORG)
                            .select(IAM_ORG.ORG_ID.getName())
                            .like(IAM_ORG.ORG_ID_PATH.getName(), "%" + orgId + "%")
            );
        }
        Page<IamUser> paginate = paginate(
                condition.getPageNum(),
                condition.getPageSize(),
                queryWrapper);
        return PageConvert.toPage(paginate);
    }

    @Nullable default IamUser selectByUsername(String username) {
        return this.selectOneByCondition(QueryCondition.createEmpty().and(IAM_USER.USER_NAME.eq(username)));
    }
}
