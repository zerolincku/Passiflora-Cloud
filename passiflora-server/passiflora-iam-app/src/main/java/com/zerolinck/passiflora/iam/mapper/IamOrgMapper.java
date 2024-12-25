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

import java.util.List;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.zerolinck.passiflora.model.iam.entity.IamOrg;
import com.zerolinck.passiflora.model.iam.resp.IamOrgResp;
import org.apache.ibatis.annotations.Param;

/** @author linck on 2024-04-09 */
public interface IamOrgMapper extends BaseMapper<IamOrg> {
    default IamOrg selectByOrgCode(@Param("orgCode") String orgCode) {
        return selectOneByCondition(QueryCondition.create(IAM_ORG.ORG_CODE, orgCode));
    }

    default List<IamOrgResp> listByParentId(@Param("orgParentId") String orgParentId) {
        return selectListByQueryAs(
                QueryWrapper.create()
                        .where(IAM_ORG.PARENT_ORG_ID.eq(orgParentId))
                        .orderBy(IAM_ORG.ORG_LEVEL, true)
                        .orderBy(IAM_ORG.ORDER, true)
                        .orderBy(IAM_ORG.ORG_NAME, true),
                IamOrgResp.class);
    }
}
