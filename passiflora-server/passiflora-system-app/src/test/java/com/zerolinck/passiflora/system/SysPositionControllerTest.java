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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.model.common.enums.StatusEnum;
import com.zerolinck.passiflora.model.system.entity.SysPosition;
import com.zerolinck.passiflora.model.system.enums.PositionDataScopeTypeEnum;
import com.zerolinck.passiflora.model.system.vo.SysPositionVo;
import java.util.List;
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
 * @author linck
 * @since 2024-05-14
 */
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SysPositionControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private static String testSysPositionId;

    private static SysPosition testSysPosition;

    private static List<SysPositionVo> positionTree;

    @Test
    @Order(1)
    public void testPage() throws Exception {
        mockMvc
            .perform(
                get("/sysPosition/page").contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode()))
            );
    }

    @Test
    @Order(2)
    public void testAdd() throws Exception {
        SysPosition sysPosition = new SysPosition();
        sysPosition.setPositionId("test");
        sysPosition.setPositionName("test");
        sysPosition.setPositionIdPath("test");
        sysPosition.setDataScopeType(PositionDataScopeTypeEnum.ALL);
        sysPosition.setPositionLevel(1);
        sysPosition.setPositionStatus(StatusEnum.ENABLE);
        mockMvc
            .perform(
                post("/sysPosition/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sysPosition))
            )
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode()))
            )
            .andDo(result ->
                testSysPositionId =
                objectMapper
                    .readTree(result.getResponse().getContentAsString())
                    .get("data")
                    .asText()
            );
    }

    @Test
    @Order(3)
    public void testDetail() throws Exception {
        mockMvc
            .perform(
                get("/sysPosition/detail")
                    .param("positionId", testSysPositionId)
            )
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode()))
            )
            .andDo(result -> {
                String responseBody = result.getResponse().getContentAsString();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                testSysPosition =
                objectMapper.convertValue(
                    jsonNode.get("data"),
                    SysPosition.class
                );
            });
    }

    @Test
    @Order(4)
    public void testUpdate() throws Exception {
        mockMvc
            .perform(
                post("/sysPosition/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testSysPosition))
            )
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode()))
            );
    }

    @Test
    @Order(5)
    public void testPermissionTree() throws Exception {
        mockMvc
            .perform(get("/sysPosition/positionTree"))
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode()))
            )
            .andDo(result -> {
                String responseBody = result.getResponse().getContentAsString();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                positionTree =
                objectMapper.convertValue(
                    jsonNode.get("data"),
                    new TypeReference<>() {}
                );
            });
    }

    @Test
    @Order(6)
    public void testDisable() throws Exception {
        mockMvc
            .perform(
                post("/sysPosition/disable")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            new String[] { testSysPositionId }
                        )
                    )
            )
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode()))
            );
    }

    @Test
    @Order(7)
    public void testEnable() throws Exception {
        mockMvc
            .perform(
                post("/sysPosition/enable")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            new String[] { testSysPositionId }
                        )
                    )
            )
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode()))
            );
    }

    @Test
    @Order(8)
    public void testUpdateOrder() throws Exception {
        mockMvc
            .perform(
                post("/sysPosition/updateOrder")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(positionTree))
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
                post("/sysPosition/delete")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            new String[] { testSysPositionId }
                        )
                    )
            )
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode()))
            );
    }
}
