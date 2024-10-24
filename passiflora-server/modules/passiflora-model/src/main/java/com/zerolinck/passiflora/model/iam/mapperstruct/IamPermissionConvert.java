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
package com.zerolinck.passiflora.model.iam.mapperstruct;

import com.zerolinck.passiflora.model.iam.entity.IamPermission;
import com.zerolinck.passiflora.model.iam.vo.IamPermissionTableVo;
import com.zerolinck.passiflora.model.iam.vo.IamPermissionVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/** @author linck on 2024-03-26 */
@Mapper
@SuppressWarnings("all")
public interface IamPermissionConvert {
    IamPermissionConvert INSTANCE = Mappers.getMapper(IamPermissionConvert.class);

    @Mapping(target = "children", expression = "java(com.zerolinck.passiflora.model.util.ListUtil.emptyList())")
    @Mapping(target = "name", source = "permissionName")
    @Mapping(target = "meta.title", source = "permissionTitle")
    @Mapping(target = "meta.icon", source = "permissionIcon")
    @Mapping(target = "meta.order", source = "order")
    @Mapping(target = "meta.permissionType", source = "permissionType")
    IamPermissionVo entity2vo(IamPermission menu);

    @Mapping(target = "children", expression = "java(com.zerolinck.passiflora.model.util.ListUtil.emptyList())")
    IamPermissionTableVo entity2tableVo(IamPermission menu);
}
