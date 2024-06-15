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
package com.zerolinck.passiflora.model.storage.mapperstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 通用文件 MapStruct Convert
 *
 * @author linck
 * @since 2024-03-26
 */
@Mapper
@SuppressWarnings("all")
public interface StorageFileConvert {
    StorageFileConvert INSTANCE = Mappers.getMapper(StorageFileConvert.class);
}
