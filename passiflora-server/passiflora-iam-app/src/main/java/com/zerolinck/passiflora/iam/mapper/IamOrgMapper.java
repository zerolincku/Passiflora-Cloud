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
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zerolinck.passiflora.model.iam.entity.IamOrg;
import com.zerolinck.passiflora.model.iam.vo.IamOrgVo;
import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author linck
 * @since 2024-04-09
 */
public interface IamOrgMapper extends BaseMapper<IamOrg> {
    Page<IamOrg> page(
            IPage<IamOrg> page,
            @Param(Constants.WRAPPER) QueryWrapper<IamOrg> searchWrapper,
            @Param("sortWrapper") QueryWrapper<IamOrg> sortWrapper);

    /** 使用更新删除，保证 update_by 和 update_time 正确 */
    int deleteByIds(@Param("orgIds") Collection<String> orgIds, @Param("updateBy") String updateBy);

    /** 此方法会级联删除下级机构 */
    int deleteById(@Param("orgId") String orgId, @Param("updateBy") String updateBy);

    @Select("SELECT * FROM iam_org WHERE del_flag = 0 AND org_code = #{orgCode}")
    IamOrg selectByOrgCode(@Param("orgCode") String orgCode);

    @Select("SELECT * FROM iam_org WHERE del_flag = 0 AND parent_org_id = #{orgParentId} ORDER BY"
            + " org_level, \"order\", org_name")
    List<IamOrgVo> listByParentId(@Param("orgParentId") String orgParentId);
}
