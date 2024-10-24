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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.common.util.JsonUtil;
import com.zerolinck.passiflora.common.util.TestUtil;
import com.zerolinck.passiflora.model.iam.entity.IamPermission;
import com.zerolinck.passiflora.model.iam.enums.PermissionTypeEnum;
import com.zerolinck.passiflora.model.iam.vo.IamPermissionTableVo;
import jakarta.annotation.Resource;
import java.util.List;
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

/** @author linck on 2024-05-06 */
@Slf4j
@Testcontainers
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IamPermissionControllerTest {

    @Resource
    private MockMvc mockMvc;

    private static String testSysPermissionId;
    private static IamPermission testIamPermission;
    private static List<IamPermissionTableVo> permissionTree;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        TestUtil.nacosTestNameSpace(registry);
        TestUtil.postgresContainerStart(registry);
        TestUtil.redisContainerStart(registry);
    }

    @Test
    @Order(1)
    public void testPage() throws Exception {
        mockMvc.perform(get("/iam-permission/page").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }

    @Test
    @Order(2)
    public void testAdd() throws Exception {
        IamPermission iamPermission = new IamPermission();
        iamPermission.setPermissionId("test");
        iamPermission.setPermissionTitle("test");
        iamPermission.setPermissionName("test");
        iamPermission.setPermissionIdPath("test");
        iamPermission.setPermissionType(PermissionTypeEnum.MENU_SET);
        mockMvc.perform(post("/iam-permission/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(iamPermission)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())))
                .andDo(result -> testSysPermissionId = JsonUtil.readTree(
                                result.getResponse().getContentAsString())
                        .get("data")
                        .asText());
    }

    @Test
    @Order(3)
    public void testDetail() throws Exception {
        mockMvc.perform(get("/iam-permission/detail").param("permissionId", testSysPermissionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    JsonNode jsonNode = JsonUtil.readTree(responseBody);
                    testIamPermission = JsonUtil.convertValue(jsonNode.get("data"), IamPermission.class);
                });
    }

    @Test
    @Order(4)
    public void testUpdate() throws Exception {
        mockMvc.perform(post("/iam-permission/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(testIamPermission)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }

    @Test
    @Order(5)
    public void testMenuTree() throws Exception {
        mockMvc.perform(get("/iam-permission/menu-tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }

    @Test
    @Order(6)
    public void testPermissionTree() throws Exception {
        mockMvc.perform(get("/iam-permission/permission-table-tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    JsonNode jsonNode = JsonUtil.readTree(responseBody);
                    permissionTree = JsonUtil.convertValue(jsonNode.get("data"), new TypeReference<>() {});
                });
    }

    @Test
    @Order(7)
    public void testDisable() throws Exception {
        mockMvc.perform(post("/iam-permission/disable")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(new String[] {testSysPermissionId})))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }

    @Test
    @Order(8)
    public void testEnable() throws Exception {
        mockMvc.perform(post("/iam-permission/enable")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(new String[] {testSysPermissionId})))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }

    @Test
    @Order(9)
    public void testUpdateOrder() throws Exception {
        mockMvc.perform(post("/iam-permission/update-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(permissionTree)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }

    @Test
    @Order(10)
    public void testDelete() throws Exception {
        mockMvc.perform(post("/iam-permission/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(new String[] {testSysPermissionId})))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }
}
