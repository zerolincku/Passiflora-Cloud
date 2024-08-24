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
package com.zerolinck.passiflora.system;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.model.system.entity.SysConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 系统配置 Unit Test
 *
 * @author 林常坤
 * @since 2024-08-24
 */
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SysConfigControllerTest {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private MockMvc mockMvc;

    private static String testSysConfigId;

    private static SysConfig testSysConfig;

    @Test
    @Order(1)
    public void testPage() throws Exception {
        mockMvc.perform(get("/sysConfig/page").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }

    @Test
    @Order(2)
    public void testAdd() throws Exception {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigId("test");
        sysConfig.setConfigName("test");
        sysConfig.setConfigCode("test");
        sysConfig.setConfigValue("test");
        mockMvc.perform(post("/sysConfig/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sysConfig)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())))
                .andDo(result -> testSysConfigId = objectMapper
                        .readTree(result.getResponse().getContentAsString())
                        .get("data")
                        .asText());
    }

    @Test
    @Order(3)
    public void testDetail() throws Exception {
        mockMvc.perform(get("/sysConfig/detail").param("configId", testSysConfigId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    testSysConfig = objectMapper.convertValue(jsonNode.get("data"), SysConfig.class);
                });
    }

    @Test
    @Order(4)
    public void testUpdate() throws Exception {
        mockMvc.perform(post("/sysConfig/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSysConfig)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }

    @Test
    @Order(5)
    public void testDelete() throws Exception {
        mockMvc.perform(post("/sysConfig/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new String[] {testSysConfigId})))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }
}