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
package com.zerolinck.passiflora.iam;

import com.zerolinck.passiflora.common.config.PassifloraProperties;
import com.zerolinck.passiflora.common.util.NetUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@MapperScan("com.zerolinck.**.mapper")
@EnableConfigurationProperties(PassifloraProperties.class)
@EnableFeignClients(basePackages = {"com.zerolinck.passiflora.feign.**"})
public class PassifloraIamApplication {

    @SneakyThrows
    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(PassifloraIamApplication.class, args);
        ConfigurableEnvironment environment = application.getEnvironment();
        String env = environment.getProperty("spring.profiles.active");
        String projectVersion = environment.getProperty("passiflora.project-version");
        String port = environment.getProperty("server.port");
        String path = environment.getProperty("server.servlet.context-path");
        String buildTime = environment.getProperty("passiflora.build-time");
        String outIp = NetUtils.findOutIp();
        log.info(
                """
            \n项目启动成功: {} 环境
            本地 Swagger: \t\thttp://localhost:{}{}/doc.html
            外部地址 Swagger: \thttp://{}:{}{}/doc.html
            Passiflora (C) 2024 version: {}
            BuildTime: {}""",
                env,
                port,
                path,
                outIp,
                port,
                path,
                projectVersion,
                buildTime);
    }
}
