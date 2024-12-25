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

import jakarta.annotation.Resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.zerolinck.passiflora.common.api.ResultCode;
import com.zerolinck.passiflora.common.util.JsonUtil;
import com.zerolinck.passiflora.common.util.TestUtil;
import com.zerolinck.passiflora.model.iam.entity.IamOrg;
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

/** @author linck on 2024-04-09 */
@Slf4j
@Testcontainers
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IamOrgControllerTest {

    @Resource
    private MockMvc mockMvc;

    private static String testSysOrgId;
    private static IamOrg testIamOrg;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        TestUtil.nacosTestNameSpace(registry);
        TestUtil.postgresContainerStart(registry);
        TestUtil.redisContainerStart(registry);
    }

    @Test
    @Order(1)
    public void testPage() throws Exception {
        mockMvc.perform(get("/iam-org/page").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }

    @Test
    @Order(2)
    public void testAdd() throws Exception {
        IamOrg iamOrg = new IamOrg();
        iamOrg.setOrgName("test");
        iamOrg.setOrgCode("test");
        iamOrg.setOrgLevel(1);
        iamOrg.setOrgType(1);
        iamOrg.setOrder(1);
        mockMvc.perform(post("/iam-org/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(iamOrg)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())))
                .andDo(result ->
                        testSysOrgId = JsonUtil.readTree(result.getResponse().getContentAsString())
                                .get("data")
                                .asText());
    }

    @Test
    @Order(3)
    public void testDetail() throws Exception {
        mockMvc.perform(get("/iam-org/detail").param("orgId", testSysOrgId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    JsonNode jsonNode = JsonUtil.readTree(responseBody);
                    testIamOrg = JsonUtil.convertValue(jsonNode.get("data"), IamOrg.class);
                });
    }

    @Test
    @Order(4)
    public void testUpdate() throws Exception {
        mockMvc.perform(post("/iam-org/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(testIamOrg)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }

    @Test
    @Order(5)
    public void testDelete() throws Exception {
        mockMvc.perform(post("/iam-org/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(new String[] {testSysOrgId})))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }

    @Test
    @Order(6)
    public void testListByParentId() throws Exception {
        mockMvc.perform(get("/iam-org/list-by-parent-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }

    @Test
    @Order(7)
    public void testOrgTree() throws Exception {
        mockMvc.perform(get("/iam-org/org-tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }
}
