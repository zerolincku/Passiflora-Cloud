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
import com.zerolinck.passiflora.model.common.constant.Constants;
import com.zerolinck.passiflora.model.system.entity.SysUser;
import lombok.RequiredArgsConstructor;
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
 * @author linck
 * @since 2024-03-20
 */
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SysUserControllerTest {

    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;

    private static String testUserId;
    private static SysUser testUser;
    private static String testToken;

    @Test
    @Order(1)
    public void testPage() throws Exception {
        mockMvc
            .perform(
                get("/sysUser/page").contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode()))
            );
    }

    @Test
    @Order(2)
    public void testAdd() throws Exception {
        SysUser sysUser = new SysUser();
        sysUser.setUserName("test");
        sysUser.setRealName("test");
        sysUser.setUserPassword("test");
        sysUser.setOrgId("test");
        mockMvc
            .perform(
                post("/sysUser/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sysUser))
            )
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode()))
            )
            .andDo(result ->
                testUserId =
                objectMapper
                    .readTree(result.getResponse().getContentAsString())
                    .get("data")
                    .asText()
            );
        System.out.println();
    }

    @Test
    @Order(3)
    public void testDetail() throws Exception {
        mockMvc
            .perform(get("/sysUser/detail").param("userId", testUserId))
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode()))
            )
            .andDo(result -> {
                String responseBody = result.getResponse().getContentAsString();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                testUser =
                objectMapper.convertValue(jsonNode.get("data"), SysUser.class);
            });
        System.out.println();
    }

    @Test
    @Order(4)
    public void testUpdate() throws Exception {
        mockMvc
            .perform(
                post("/sysUser/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testUser))
            )
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode()))
            );
    }

    @Test
    @Order(5)
    public void testLogin() throws Exception {
        testUser.setUserName("test");
        testUser.setUserPassword("test");
        mockMvc
            .perform(
                post("/sysUser/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testUser))
            )
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode()))
            )
            .andDo(result ->
                testToken =
                objectMapper
                    .readTree(result.getResponse().getContentAsString())
                    .get("data")
                    .asText()
            );
    }

    @Test
    @Order(6)
    public void testCheckToken() throws Exception {
        mockMvc
            .perform(
                get("/sysUser/checkToken")
                    .header(Constants.Authorization, testToken)
            )
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode()))
            );
    }

    @Test
    @Order(7)
    public void testCurrentUserInfo() throws Exception {
        mockMvc
            .perform(
                get("/sysUser/currentUserInfo")
                    .header(Constants.Authorization, testToken)
            )
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode()))
            );
    }

    @Test
    @Order(8)
    public void testLogout() throws Exception {
        mockMvc
            .perform(
                get("/sysUser/logout")
                    .header(Constants.Authorization, testToken)
            )
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode()))
            );
    }

    @Test
    @Order(9)
    public void testDelete() throws Exception {
        mockMvc
            .perform(
                post("/sysUser/delete")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            new String[] { testUserId }
                        )
                    )
            )
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode()))
            );
    }
}
