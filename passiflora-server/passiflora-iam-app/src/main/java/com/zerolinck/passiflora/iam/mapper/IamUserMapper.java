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
package com.zerolinck.passiflora.iam.mapper;

import static com.zerolinck.passiflora.model.iam.entity.table.IamUserTableDef.IAM_USER;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.zerolinck.passiflora.model.iam.entity.IamUser;
import com.zerolinck.passiflora.mybatis.util.FlexPage;
import org.jetbrains.annotations.Nullable;

/** @author linck on 2024-02-07 */
public interface IamUserMapper extends BaseMapper<IamUser> {

    /**
     * 分页查询
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param queryWrapper 查询条件
     * @author 林常坤 on 2025/1/24
     */
    default com.zerolinck.passiflora.common.api.Page<IamUser> page(
            Number pageNum, Number pageSize, QueryWrapper queryWrapper) {
        Page<IamUser> paginate = paginate(pageNum, pageSize, queryWrapper);
        return FlexPage.convert(paginate);
    }

    @Nullable default IamUser selectByUsername(String username) {
        return this.selectOneByCondition(QueryCondition.createEmpty().and(IAM_USER.USER_NAME.eq(username)));
    }
}
