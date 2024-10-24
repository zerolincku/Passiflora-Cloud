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
package com.zerolinck.passiflora.model.iam.vo;

import com.zerolinck.passiflora.model.iam.entity.IamPermission;
import java.util.Collection;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author linck on 2024-05-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class IamPermissionTableVo extends IamPermission {

    private Collection<IamPermissionTableVo> children;
}
