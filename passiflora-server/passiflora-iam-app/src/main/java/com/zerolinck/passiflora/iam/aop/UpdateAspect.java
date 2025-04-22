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
package com.zerolinck.passiflora.iam.aop;

import com.zerolinck.passiflora.base.BaseEntity;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 拦截 update 方法，置空 createAt 和 createBy
 *
 * @author linck on 2024-03-19
 */
@Slf4j
@Aspect
@Component
public class UpdateAspect {

    @Before("execution(*"
            + " com.zerolinck.passiflora.iam.*.*Controller.update(com.zerolinck.passiflora.base.BaseEntity+))"
            + " && args(entity)")
    public void beforeUpdate(JoinPoint joinPoint, BaseEntity entity) {
        entity.setCreateTime(null);
        entity.setCreateBy(null);
    }
}
