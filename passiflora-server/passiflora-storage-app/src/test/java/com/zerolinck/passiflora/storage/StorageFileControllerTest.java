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
package com.zerolinck.passiflora.storage;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.model.storage.entity.StorageFile;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * 通用文件 Unit Test
 *
 * @author linck
 * @since 2024-05-17
 */
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StorageFileControllerTest {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private MockMvc mockMvc;

    private static String testStorageFileId;
    private static String quickUploadStorageFileId;

    @Test
    @Order(1)
    public void testPage() throws Exception {
        mockMvc.perform(get("/storageFile/page").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }

    @Test
    @Order(2)
    public void testUpload() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile("file", "test.txt", "text/plain", "This is a test file".getBytes());
        mockMvc.perform(multipart("/storageFile/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    testStorageFileId = objectMapper.convertValue(jsonNode.get("data"), String.class);
                });
    }

    @Test
    @Order(3)
    public void testTryQuicklyUpload() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile("file", "test.txt", "text/plain", "This is a test file".getBytes());
        StorageFile storageFile = new StorageFile();
        storageFile.setOriginalFileName("quickTest.txt");
        storageFile.setContentType("text/plain");
        storageFile.setFileMd5(DigestUtils.md5Hex(file.getBytes()));
        mockMvc.perform(post("/storageFile/tryQuicklyUpload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storageFile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    quickUploadStorageFileId = objectMapper.convertValue(jsonNode.get("data"), String.class);
                });
    }

    @Test
    @Order(4)
    public void testListByFileIds() throws Exception {
        mockMvc.perform(post("/storageFile/listByFileIds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new String[] {
                            testStorageFileId, quickUploadStorageFileId,
                        })))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }

    @Test
    @Order(5)
    public void testDetail() throws Exception {
        mockMvc.perform(get("/storageFile/detail").param("fileId", testStorageFileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }

    @Test
    @Order(6)
    public void testConfirmFile() throws Exception {
        mockMvc.perform(post("/storageFile/confirmFile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new String[] {testStorageFileId})))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }

    @Test
    @Order(7)
    public void testDownloadFile() throws Exception {
        MvcResult result = mockMvc.perform(get("/storageFile/downloadFile").param("fileId", testStorageFileId))
                .andExpect(status().isOk())
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertNotNull(response.getContentAsByteArray());
    }

    @Test
    @Order(8)
    public void testDownloadZip() throws Exception {
        MvcResult result = mockMvc.perform(post("/storageFile/downloadZip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new String[] {
                            testStorageFileId, quickUploadStorageFileId,
                        })))
                .andExpect(status().isOk())
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertNotNull(response.getContentAsByteArray());
    }

    @Test
    @Order(9)
    public void testDelete() throws Exception {
        mockMvc.perform(post("/storageFile/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new String[] {
                            testStorageFileId, quickUploadStorageFileId,
                        })))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }
}
