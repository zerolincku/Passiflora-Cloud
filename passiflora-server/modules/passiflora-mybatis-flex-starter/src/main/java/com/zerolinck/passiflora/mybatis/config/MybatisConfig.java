/* 
 * Copyright (C) 2025 Linck. <zerolinck@foxmail.com>
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
package com.zerolinck.passiflora.mybatis.config;

import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.keygen.KeyGenerators;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import com.zerolinck.passiflora.base.BaseEntity;
import com.zerolinck.passiflora.common.util.SpringContextHolder;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/** @author linck on 2024-02-07 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class MybatisConfig implements MyBatisFlexCustomizer {

    @Override
    public void customize(FlexGlobalConfig flexGlobalConfig) {
        flexGlobalConfig.registerInsertListener(new FlexInsertListener(), BaseEntity.class);
        flexGlobalConfig.registerUpdateListener(new FlexUpdateListener(), BaseEntity.class);

        // 主键生成策略
        FlexGlobalConfig.KeyConfig keyConfig = new FlexGlobalConfig.KeyConfig();
        keyConfig.setKeyType(KeyType.Generator);
        keyConfig.setValue(KeyGenerators.flexId);
        FlexGlobalConfig.getDefaultConfig().setKeyConfig(keyConfig);

        // 本地环境打印日志
        String property = SpringContextHolder.getProperty("spring.profiles.active");
        if (!"local".equals(property)) {
            // return;
        }
        // 开启审计功能
        AuditManager.setAuditEnable(true);

        // 设置 SQL 审计收集器
        AuditManager.setMessageCollector(auditMessage ->
                log.info("sql took {}ms >> {}", auditMessage.getElapsedTime(), auditMessage.getFullSql()));
    }
}
