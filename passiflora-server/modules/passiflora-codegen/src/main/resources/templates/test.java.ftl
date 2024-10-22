package com.zerolinck.passiflora.${moduleName};

import com.fasterxml.jackson.databind.JsonNode;
import com.zerolinck.passiflora.common.util.JsonUtil;
import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.model.${moduleName}.entity.${entityClass};
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * ${table.description} Unit Test
 *
 * @author ${author}
 * @since ${date}
 */
@Slf4j
@Testcontainers
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ${entityClass}ControllerTest {
    @Resource
    private MockMvc mockMvc;
    
    private static String test${entityClass}Id;
    private static ${entityClass} test${entityClass};

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.0-alpine").withReuse(true);
    
    @Test
    @Order(1)
    public void testPage() throws Exception {
        mockMvc.perform(get("/${entityName}/page")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }
    
    @Test
    @Order(2)
    public void testAdd() throws Exception {
        ${entityClass} ${entityName} = new ${entityClass}();
        <#list table.columnList as column>
            <#if column.nullable>
                <#if column.fieldType.getSimpleName() == "String">
        ${entityName}.set${column.fieldName[0..0]?upper_case}${column.fieldName[1..]}("test");
                </#if>
            </#if>
        </#list>
        mockMvc.perform(post("/${entityName}/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtil.toJson(${entityName})))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())))
            .andDo(result -> test${entityClass}Id = JsonUtil.readTree(result.getResponse().getContentAsString()).get("data").asText());
    }
    
    @Test
    @Order(3)
    public void testDetail() throws Exception {
        mockMvc.perform(get("/${entityName}/detail")
            .param("${table.pkFieldName}", test${entityClass}Id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())))
            .andDo(result -> {
                String responseBody = result.getResponse().getContentAsString();
                JsonNode jsonNode = JsonUtil.readTree(responseBody);
                test${entityClass} = JsonUtil.convertValue(jsonNode.get("data"), ${entityClass}.class);
            });
    }
    
    @Test
    @Order(4)
    public void testUpdate() throws Exception {
        mockMvc.perform(post("/${entityName}/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtil.toJson(test${entityClass})))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }
    
    @Test
    @Order(5)
    public void testDelete() throws Exception {
        mockMvc.perform(post("/${entityName}/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtil.toJson(new String[]{test${entityClass}Id})))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }

}
