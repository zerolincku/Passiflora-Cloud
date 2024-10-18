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
package com.zerolinck.passiflora.iam;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.common.util.JsonUtil;
import com.zerolinck.passiflora.common.util.TestUtil;
import com.zerolinck.passiflora.model.common.enums.StatusEnum;
import com.zerolinck.passiflora.model.iam.entity.IamApp;
import com.zerolinck.passiflora.model.iam.enums.AppTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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

/**
 * 应用 Unit Test
 *
 * @author linck
 * @since 2024-09-30
 */
@Slf4j
@Testcontainers
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IamAppControllerTest {

    @Resource
    private MockMvc mockMvc;

    private static String testIamAppId;

    private static IamApp testIamApp;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        TestUtil.postgresContainerStart(registry);
    }

    @Test
    @Order(1)
    public void testPage() throws Exception {
        mockMvc.perform(get("/iamApp/page").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }

    @Test
    @Order(2)
    public void testAdd() throws Exception {
        IamApp iamApp = new IamApp();
        iamApp.setAppId("test");
        iamApp.setAppName("test");
        iamApp.setAppKey("test");
        iamApp.setAppSecret("test");
        iamApp.setAppStatus(StatusEnum.ENABLE);
        iamApp.setAppType(AppTypeEnum.WEB_APP);
        iamApp.setAppIcon("test");
        iamApp.setAppUrl("test");
        iamApp.setAppRemark("test");
        mockMvc.perform(post("/iamApp/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(iamApp)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())))
                .andDo(result ->
                        testIamAppId = JsonUtil.readTree(result.getResponse().getContentAsString())
                                .get("data")
                                .asText());
    }

    @Test
    @Order(3)
    public void testDetail() throws Exception {
        mockMvc.perform(get("/iamApp/detail").param("appId", testIamAppId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    JsonNode jsonNode = JsonUtil.readTree(responseBody);
                    testIamApp = JsonUtil.convertValue(jsonNode.get("data"), IamApp.class);
                });
    }

    @Test
    @Order(4)
    public void testUpdate() throws Exception {
        mockMvc.perform(post("/iamApp/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(testIamApp)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }

    @Test
    @Order(5)
    public void testDelete() throws Exception {
        mockMvc.perform(post("/iamApp/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(new String[] {testIamAppId})))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }
}
