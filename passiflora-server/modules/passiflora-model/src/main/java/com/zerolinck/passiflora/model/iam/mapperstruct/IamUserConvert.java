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

import com.zerolinck.passiflora.model.iam.entity.IamUser;
import com.zerolinck.passiflora.model.iam.vo.IamUserInfo;
import com.zerolinck.passiflora.model.iam.vo.IamUserVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author linck
 * @since 2024-03-26
 */
@Mapper
@SuppressWarnings("all")
public interface IamUserConvert {
    IamUserConvert INSTANCE = Mappers.getMapper(IamUserConvert.class);

    @Mapping(target = "orgName", ignore = true)
    @Mapping(target = "positionIds", ignore = true)
    @Mapping(target = "positionNames", ignore = true)
    @Mapping(target = "roleIds", ignore = true)
    @Mapping(target = "roleNames", ignore = true)
    IamUserVo entity2vo(IamUser iamUser);

    @Mapping(target = "permission", ignore = true)
    @Mapping(target = "menu", ignore = true)
    @Mapping(target = "salt", ignore = true)
    @Mapping(target = "userPassword", ignore = true)
    IamUserInfo entity2info(IamUser iamUser);
}
