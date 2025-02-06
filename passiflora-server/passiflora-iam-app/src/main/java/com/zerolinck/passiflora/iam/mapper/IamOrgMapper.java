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

import static com.zerolinck.passiflora.model.iam.entity.table.IamOrgTableDef.IAM_ORG;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.zerolinck.passiflora.model.iam.entity.IamOrg;
import com.zerolinck.passiflora.model.iam.resp.IamOrgResp;
import com.zerolinck.passiflora.mybatis.util.FlexPage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 组织 Mybatis Mapper
 *
 * @author linck
 * @since 2024-04-09
 */
public interface IamOrgMapper extends BaseMapper<IamOrg> {

    /**
     * 分页查询
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param queryWrapper 查询条件
     * @author 林常坤 on 2025/1/24
     */
    default com.zerolinck.passiflora.common.api.Page<IamOrg> page(
            Number pageNum, Number pageSize, QueryWrapper queryWrapper) {
        Page<IamOrg> paginate = paginate(pageNum, pageSize, queryWrapper);
        return FlexPage.convert(paginate);
    }

    /**
     * 根据组织代码查询组织
     *
     * @param orgCode 组织代码
     * @return 组织对象
     * @since 2024-04-09
     */
    default IamOrg selectByOrgCode(String orgCode) {
        return selectOneByCondition(QueryCondition.create(IAM_ORG.ORG_CODE, orgCode));
    }

    /**
     * 根据父组织ID查询子组织列表
     *
     * @param orgParentId 父组织ID
     * @return 子组织列表
     * @since 2024-04-09
     */
    default List<IamOrgResp> listByParentId(String orgParentId) {
        return selectListByQueryAs(
                QueryWrapper.create()
                        .where(IAM_ORG.PARENT_ORG_ID.eq(orgParentId))
                        .orderBy(IAM_ORG.ORG_LEVEL, true)
                        .orderBy(IAM_ORG.ORDER, true)
                        .orderBy(IAM_ORG.ORG_NAME, true),
                IamOrgResp.class);
    }

    /**
     * 根据机构名称查询数量
     *
     * @author 林常坤 on 2025/2/6
     */
    default long countByName(@NotNull String orgName, @NotNull String orgParentId, @Nullable String orgId) {
        return this.selectCountByQuery(new QueryWrapper()
                .eq(IamOrg::getOrgName, orgName)
                .eq(IamOrg::getParentOrgId, orgParentId)
                .ne(IamOrg::getOrgId, orgId, StringUtils.isNotBlank(orgId)));
    }

    /**
     * 根据机构ids查询机构列表
     *
     * @author 林常坤 on 2025/2/6
     */
    @NotNull default List<IamOrg> listByOrgIds(@Nullable Collection<String> orgIds) {
        if (CollectionUtils.isEmpty(orgIds)) {
            return Collections.emptyList();
        }
        return this.selectListByQuery(new QueryWrapper().in(IamOrg::getOrgId, orgIds));
    }
}
