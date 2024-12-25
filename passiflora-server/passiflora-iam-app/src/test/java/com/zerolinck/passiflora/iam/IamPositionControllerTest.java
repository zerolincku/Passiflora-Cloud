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

import java.util.List;
import jakarta.annotation.Resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.zerolinck.passiflora.base.enums.StatusEnum;
import com.zerolinck.passiflora.common.api.ResultCode;
import com.zerolinck.passiflora.common.util.JsonUtil;
import com.zerolinck.passiflora.common.util.TestUtil;
import com.zerolinck.passiflora.model.iam.entity.IamPosition;
import com.zerolinck.passiflora.model.iam.enums.PositionDataScopeTypeEnum;
import com.zerolinck.passiflora.model.iam.resp.IamPositionResp;
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

/** @author linck on 2024-05-14 */
@Slf4j
@Testcontainers
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IamPositionControllerTest {
    @Resource
    private MockMvc mockMvc;

    private static String testSysPositionId;
    private static IamPosition testIamPosition;
    private static List<IamPositionResp> positionTree;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        TestUtil.nacosTestNameSpace(registry);
        TestUtil.postgresContainerStart(registry);
        TestUtil.redisContainerStart(registry);
    }

    @Test
    @Order(1)
    public void testPage() throws Exception {
        mockMvc.perform(get("/iam-position/page").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }

    @Test
    @Order(2)
    public void testAdd() throws Exception {
        IamPosition iamPosition = new IamPosition();
        iamPosition.setPositionId("test");
        iamPosition.setPositionName("test");
        iamPosition.setPositionIdPath("test");
        iamPosition.setDataScopeType(PositionDataScopeTypeEnum.ALL);
        iamPosition.setPositionLevel(1);
        iamPosition.setPositionStatus(StatusEnum.ENABLE);
        iamPosition.setOrder(1);
        mockMvc.perform(post("/iam-position/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(iamPosition)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())))
                .andDo(result -> testSysPositionId = JsonUtil.readTree(
                                result.getResponse().getContentAsString())
                        .get("data")
                        .asText());
    }

    @Test
    @Order(3)
    public void testDetail() throws Exception {
        mockMvc.perform(get("/iam-position/detail").param("positionId", testSysPositionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    JsonNode jsonNode = JsonUtil.readTree(responseBody);
                    testIamPosition = JsonUtil.convertValue(jsonNode.get("data"), IamPosition.class);
                });
    }

    @Test
    @Order(4)
    public void testUpdate() throws Exception {
        mockMvc.perform(post("/iam-position/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(testIamPosition)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }

    @Test
    @Order(5)
    public void testPermissionTree() throws Exception {
        mockMvc.perform(get("/iam-position/position-tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    JsonNode jsonNode = JsonUtil.readTree(responseBody);
                    positionTree = JsonUtil.convertValue(jsonNode.get("data"), new TypeReference<>() {});
                });
    }

    @Test
    @Order(6)
    public void testDisable() throws Exception {
        mockMvc.perform(post("/iam-position/disable")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(new String[] {testSysPositionId})))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }

    @Test
    @Order(7)
    public void testEnable() throws Exception {
        mockMvc.perform(post("/iam-position/enable")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(new String[] {testSysPositionId})))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }

    @Test
    @Order(8)
    public void testUpdateOrder() throws Exception {
        mockMvc.perform(post("/iam-position/update-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(positionTree)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }

    @Test
    @Order(9)
    public void testDelete() throws Exception {
        mockMvc.perform(post("/iam-position/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(new String[] {testSysPositionId})))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }
}
