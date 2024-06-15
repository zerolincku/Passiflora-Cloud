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
package com.zerolinck.passiflora.model.storage.enums;

import com.zerolinck.passiflora.model.common.LabelValueInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件状态
 *
 * @author linck
 * @since 2024-04-09
 */
@Getter
@AllArgsConstructor
public enum FileStatusEnum implements LabelValueInterface {
    /**
     * 文件上传默认为临时文件，临时文件在到达设置的时间期限后，将会被删除
     */
    TEMP("临时文件", 0),
    /**
     * 被其他服务确认引用，会转换为正式文件
     */
    CONFIRMED("正式文件", 1);

    private final String label;

    private final Integer value;
}
