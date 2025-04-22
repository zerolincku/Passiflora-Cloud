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
import com.zerolinck.passiflora.base.enums.YesOrNoEnum;
import com.zerolinck.passiflora.common.api.ResultCode;
import com.zerolinck.passiflora.common.util.JsonUtils;
import com.zerolinck.passiflora.common.util.TestUtils;
import com.zerolinck.passiflora.model.iam.entity.IamDict;
import com.zerolinck.passiflora.model.iam.entity.IamDictItem;
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

/** @author linck on 2024-04-01 */
@Slf4j
@Testcontainers
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IamDictControllerTest {

    @Resource
    private MockMvc mockMvc;

    private static String testSysDictId;
    private static IamDict testIamDict;
    private static String testSysDictItemId;
    private static IamDictItem testIamDictItem;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        TestUtils.nacosTestNameSpace(registry);
        TestUtils.postgresContainerStart(registry);
        TestUtils.redisContainerStart(registry);
    }

    @Test
    @Order(1)
    public void testPage() throws Exception {
        mockMvc.perform(get("/iam-dict/page").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }

    @Test
    @Order(2)
    public void testItemPage() throws Exception {
        mockMvc.perform(get("/iam-dict-item/page").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }

    @Test
    @Order(3)
    public void testAdd() throws Exception {
        IamDict iamDict = new IamDict();
        iamDict.setDictName("test");
        iamDict.setDictTag("test");
        iamDict.setIsSystem(YesOrNoEnum.NO);
        iamDict.setValueIsOnly(YesOrNoEnum.NO);
        mockMvc.perform(post("/iam-dict/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(iamDict)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())))
                .andDo(result ->
                        testSysDictId = JsonUtils.readTree(result.getResponse().getContentAsString())
                                .get("data")
                                .asText());
    }

    @Test
    @Order(4)
    public void testAddItem() throws Exception {
        IamDictItem iamDictItem = new IamDictItem();
        iamDictItem.setDictId(testSysDictId);
        iamDictItem.setLabel("test");
        iamDictItem.setValue("test");
        iamDictItem.setIsSystem(YesOrNoEnum.NO);
        mockMvc.perform(post("/iam-dict-item/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(iamDictItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())))
                .andDo(result -> testSysDictItemId = JsonUtils.readTree(
                                result.getResponse().getContentAsString())
                        .get("data")
                        .asText());
    }

    @Test
    @Order(5)
    public void testDetailItem() throws Exception {
        mockMvc.perform(get("/iam-dict-item/detail").param("dictItemId", testSysDictItemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    JsonNode jsonNode = JsonUtils.readTree(responseBody);
                    testIamDictItem = JsonUtils.convertValue(jsonNode.get("data"), IamDictItem.class);
                });
    }

    @Test
    @Order(6)
    public void testUpdateItem() throws Exception {
        mockMvc.perform(post("/iam-dict-item/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(testIamDictItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }

    @Test
    @Order(7)
    public void testDeleteItem() throws Exception {
        mockMvc.perform(post("/iam-dict-item/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(new String[] {testSysDictItemId})))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }

    @Test
    @Order(8)
    public void testDetail() throws Exception {
        mockMvc.perform(get("/iam-dict/detail").param("dictId", testSysDictId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    JsonNode jsonNode = JsonUtils.readTree(responseBody);
                    testIamDict = JsonUtils.convertValue(jsonNode.get("data"), IamDict.class);
                });
    }

    @Test
    @Order(9)
    public void testUpdate() throws Exception {
        mockMvc.perform(post("/iam-dict/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(testIamDict)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }

    @Test
    @Order(10)
    public void testDelete() throws Exception {
        mockMvc.perform(post("/iam-dict/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(new String[] {testSysDictId})))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }

    @Test
    @Order(11)
    public void testListByDictId() throws Exception {
        mockMvc.perform(get("/iam-dict-item/list-by-dict-id").param("dictId", "1778631179896446977"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }

    @Test
    @Order(12)
    public void testListByDictName() throws Exception {
        mockMvc.perform(get("/iam-dict-item/list-by-dict-name").param("dictName", "机构类型"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }

    @Test
    @Order(13)
    public void testListByDictTag() throws Exception {
        mockMvc.perform(get("/iam-dict-item/list-by-dict-tag").param("dictTag", "orgType"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCode.SUCCESS.getCode())));
    }
}
