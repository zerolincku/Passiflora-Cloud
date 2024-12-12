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
package com.zerolinck.passiflora.codegen.util;

import java.util.Map;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zerolinck.passiflora.common.util.YamlUtil;

/** @author 林常坤 on 2024/10/24 */
public class DataSourceUtil {

    public static DataSource getDataSource() {
        Map<String, Object> configMap = YamlUtil.loadYamlFromClasspath("application.yml");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(configMap.get("url").toString());
        config.setUsername(configMap.get("username").toString());
        config.setPassword(configMap.get("password").toString());
        config.setMaximumPoolSize(1);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(10000);
        return new HikariDataSource(config);
    }
}
