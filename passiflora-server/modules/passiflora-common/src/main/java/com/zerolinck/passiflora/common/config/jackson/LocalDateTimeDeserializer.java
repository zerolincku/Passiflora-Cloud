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
package com.zerolinck.passiflora.common.config.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.zerolinck.passiflora.common.util.TimeUtil;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author linck
 * @since 2024-02-06
 */
public class LocalDateTimeDeserializer extends com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer {

    public LocalDateTimeDeserializer() {
        super(TimeUtil.NORMAL_DATE_TIME_FORMATTER);
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String timeStr = parser.getText();
        return TimeUtil.commonStrDate2LocalDateTime(timeStr);
    }
}
