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

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.annotation.Resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.zerolinck.passiflora.base.constant.Header;
import com.zerolinck.passiflora.common.api.ResultCode;
import com.zerolinck.passiflora.common.util.JsonUtils;
import com.zerolinck.passiflora.common.util.TestUtils;
import com.zerolinck.passiflora.model.iam.entity.IamUser;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import lombok.extern.slf4j.Slf4j;

/** @author linck on 2024-03-20 */
@Slf4j
@Testcontainers
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IamUserControllerTest {
    @Resource
    private MockMvc mockMvc;

    private static String testUserId;
    private static IamUser testUser;
    private static String testToken;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        TestUtils.nacosTestNameSpace(registry);
        TestUtils.postgresContainerStart(registry);
        TestUtils.redisContainerStart(registry);
    }

    @Test
    @Order(1)
    public void testPage() throws Exception {
        mockMvc.perform(get("/iam-user/page").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }

    @Test
    @Order(2)
    public void testAdd() throws Exception {
        IamUser iamUser = new IamUser();
        iamUser.setUserName("test");
        iamUser.setRealName("test");
        iamUser.setUserPassword("test");
        iamUser.setOrgId("test");
        mockMvc.perform(post("/iam-user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(iamUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())))
                .andDo(result ->
                        testUserId = JsonUtils.readTree(result.getResponse().getContentAsString())
                                .get("data")
                                .asText());
    }

    @Test
    @Order(3)
    public void testDetail() throws Exception {
        mockMvc.perform(get("/iam-user/detail").param("userId", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    JsonNode jsonNode = JsonUtils.readTree(responseBody);
                    testUser = JsonUtils.convertValue(jsonNode.get("data"), IamUser.class);
                });
    }

    @Test
    @Order(4)
    public void testUpdate() throws Exception {
        mockMvc.perform(post("/iam-user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }

    @Test
    @Order(5)
    public void testLogin() throws Exception {
        testUser.setUserName("test");
        testUser.setUserPassword("test");
        mockMvc.perform(post("/iam-user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())))
                .andDo(result ->
                        testToken = JsonUtils.readTree(result.getResponse().getContentAsString())
                                .get("data")
                                .asText());
    }

    @Test
    @Order(6)
    public void testCheckToken() throws Exception {
        mockMvc.perform(get("/iam-user/check-token").header(Header.AUTHORIZATION.toString(), testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }

    @Test
    @Order(7)
    public void testCurrentUserInfo() throws Exception {
        mockMvc.perform(get("/iam-user/current-user-info").header(Header.AUTHORIZATION.toString(), testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }

    @Test
    @Order(8)
    public void testLogout() throws Exception {
        mockMvc.perform(get("/iam-user/logout").header(Header.AUTHORIZATION.toString(), testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }

    @Test
    @Order(9)
    public void testDelete() throws Exception {
        mockMvc.perform(post("/iam-user/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(new String[] {testUserId})))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }
}
