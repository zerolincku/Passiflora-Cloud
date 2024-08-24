package com.zerolinck.passiflora.system;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.model.system.entity.SysConfig;
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

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private MockMvc mockMvc;
    
    private static String testSysConfigId;

    private static SysConfig testSysConfig;
    
    @Test
    @Order(1)
    public void testPage() throws Exception {
        mockMvc.perform(get("/sysConfig/page")
            .contentType(MediaType.APPLICATION_JSON))
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
            .andDo(result -> testSysConfigId = objectMapper.readTree(result.getResponse().getContentAsString()).get("data").asText());
    }
    
    @Test
    @Order(3)
    public void testDetail() throws Exception {
        mockMvc.perform(get("/sysConfig/detail")
            .param("configId", testSysConfigId))
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
            .content(objectMapper.writeValueAsString(new String[]{testSysConfigId})))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }

}
