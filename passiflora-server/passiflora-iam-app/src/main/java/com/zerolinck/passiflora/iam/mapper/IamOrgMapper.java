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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zerolinck.passiflora.model.common.enums.DelFlagEnum;
import com.zerolinck.passiflora.model.iam.entity.IamOrg;
import com.zerolinck.passiflora.model.iam.resp.IamOrgResp;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/** @author linck on 2024-04-09 */
public interface IamOrgMapper extends BaseMapper<IamOrg> {
    default Page<IamOrg> page(
            IPage<IamOrg> page,
            @Param(Constants.WRAPPER) QueryWrapper<IamOrg> searchWrapper,
            @Param("sortWrapper") QueryWrapper<IamOrg> sortWrapper) {
        if (searchWrapper == null) {
            searchWrapper = new QueryWrapper<>();
        }
        searchWrapper.eq("del_flag", 0);

        if (sortWrapper == null
                || sortWrapper.getSqlSegment() == null
                || sortWrapper.getSqlSegment().isEmpty()) {
            searchWrapper.orderByAsc("org_level", "\"order\"", "org_name");
        } else {
            searchWrapper.last(sortWrapper.getSqlSegment());
        }

        return (Page<IamOrg>) this.selectPage(page, searchWrapper);
    }

    /**
     * 逻辑删除指定 ID 的记录
     *
     * @param orgIds   需要删除的组织 ID 集合
     * @param updateBy 更新者
     * @return 更新的行数
     */
    default int deleteByIds(Collection<String> orgIds, String updateBy) {
        if (orgIds == null || orgIds.isEmpty()) {
            return 0;
        }

        LambdaUpdateWrapper<IamOrg> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(IamOrg::getOrgId, orgIds)
                .set(IamOrg::getUpdateTime, LocalDateTime.now())
                .set(IamOrg::getUpdateBy, updateBy)
                .set(IamOrg::getDelFlag, DelFlagEnum.DELETED);

        return this.update(null, updateWrapper);
    }

    /**
     * 逻辑删除指定 ID 及其路径匹配的记录
     *
     * @param orgId    组织 ID
     * @param updateBy 更新者
     * @return 更新的行数
     */
    default int deleteById(String orgId, String updateBy) {
        if (orgId == null || orgId.isEmpty()) {
            return 0;
        }

        LambdaUpdateWrapper<IamOrg> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.like(IamOrg::getOrgIdPath, "%" + orgId + "%")
                .set(IamOrg::getUpdateTime, LocalDateTime.now())
                .set(IamOrg::getUpdateBy, updateBy)
                .set(IamOrg::getDelFlag, DelFlagEnum.DELETED);

        return this.update(null, updateWrapper);
    }

    @Select("SELECT * FROM iam_org WHERE del_flag = 0 AND org_code = #{orgCode}")
    IamOrg selectByOrgCode(@Param("orgCode") String orgCode);

    @Select("SELECT * FROM iam_org WHERE del_flag = 0 AND parent_org_id = #{orgParentId} ORDER BY"
            + " org_level, \"order\", org_name")
    List<IamOrgResp> listByParentId(@Param("orgParentId") String orgParentId);
}
