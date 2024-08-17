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
import com.zerolinck.passiflora.model.system.entity.SysUserRole;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 用户角色绑定 Unit Test
 *
 * @author 林常坤
 * @since 2024-08-17
 */
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SysUserRoleControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private static String testSysUserRoleId;

    private static SysUserRole testSysUserRole;

    @Test
    @Order(1)
    public void testPage() throws Exception {
        mockMvc.perform(get("/sysUserRole/page").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }

    @Test
    @Order(2)
    public void testAdd() throws Exception {
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setId("test");
        sysUserRole.setUserId("test");
        sysUserRole.setRoleId("test");
        mockMvc.perform(post("/sysUserRole/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sysUserRole)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())))
                .andDo(result -> testSysUserRoleId = objectMapper
                        .readTree(result.getResponse().getContentAsString())
                        .get("data")
                        .asText());
    }

    @Test
    @Order(3)
    public void testDetail() throws Exception {
        mockMvc.perform(get("/sysUserRole/detail").param("id", testSysUserRoleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    testSysUserRole = objectMapper.convertValue(jsonNode.get("data"), SysUserRole.class);
                });
    }

    @Test
    @Order(4)
    public void testUpdate() throws Exception {
        mockMvc.perform(post("/sysUserRole/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSysUserRole)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }

    @Test
    @Order(5)
    public void testDelete() throws Exception {
        mockMvc.perform(post("/sysUserRole/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new String[] {testSysUserRoleId})))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }
}
