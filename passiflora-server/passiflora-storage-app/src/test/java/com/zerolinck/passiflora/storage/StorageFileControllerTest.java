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
import com.zerolinck.passiflora.common.api.ResultCodeEnum;
import com.zerolinck.passiflora.common.util.JsonUtil;
import com.zerolinck.passiflora.model.storage.entity.StorageFile;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * 通用文件 Unit Test
 *
 * @author linck
 * @since 2024-05-17
 */
@Slf4j
@Testcontainers
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StorageFileControllerTest {
    @Resource
    private MockMvc mockMvc;

    private static String testStorageFileId;
    private static String quickUploadStorageFileId;

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:13.16-bookworm").withReuse(true);

    @Test
    @Order(1)
    public void testPage() throws Exception {
        mockMvc.perform(get("/storage-file/page").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }

    @Test
    @Order(2)
    public void testUpload() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile("file", "test.txt", "text/plain", "This is a test file".getBytes());
        mockMvc.perform(multipart("/storage-file/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    JsonNode jsonNode = JsonUtil.readTree(responseBody);
                    testStorageFileId = JsonUtil.convertValue(jsonNode.get("data"), String.class);
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
        mockMvc.perform(post("/storage-file/try-quickly-upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(storageFile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    JsonNode jsonNode = JsonUtil.readTree(responseBody);
                    quickUploadStorageFileId = JsonUtil.convertValue(jsonNode.get("data"), String.class);
                });
    }

    @Test
    @Order(4)
    public void testListByFileIds() throws Exception {
        mockMvc.perform(post("/storage-file/list-by-file-ids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(new String[] {
                            testStorageFileId, quickUploadStorageFileId,
                        })))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }

    @Test
    @Order(5)
    public void testDetail() throws Exception {
        mockMvc.perform(get("/storage-file/detail").param("fileId", testStorageFileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }

    @Test
    @Order(6)
    public void testConfirmFile() throws Exception {
        mockMvc.perform(post("/storage-file/confirm-file")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(new String[] {testStorageFileId})))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }

    @Test
    @Order(7)
    public void testDownloadFile() throws Exception {
        MvcResult result = mockMvc.perform(get("/storage-file/download-file").param("fileId", testStorageFileId))
                .andExpect(status().isOk())
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertNotNull(response.getContentAsByteArray());
    }

    @Test
    @Order(8)
    public void testDownloadZip() throws Exception {
        MvcResult result = mockMvc.perform(post("/storage-file/download-zip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(new String[] {
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
        mockMvc.perform(post("/storage-file/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(new String[] {
                            testStorageFileId, quickUploadStorageFileId,
                        })))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(ResultCodeEnum.SUCCESS.getCode())));
    }
}
