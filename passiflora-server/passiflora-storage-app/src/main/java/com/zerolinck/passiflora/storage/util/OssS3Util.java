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
package com.zerolinck.passiflora.storage.util;

import java.io.IOException;
import java.io.OutputStream;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

/** @author 林常坤 on 2024/10/17 */
@UtilityClass
public class OssS3Util {

    @Getter
    @Setter
    private static S3Client s3Client;

    /**
     * 检查指定的桶是否存在。
     *
     * @param bucketName 要检查的桶名称
     * @return 如果桶存在返回 true，否则返回 false
     * @throws S3Exception 如果在检查过程中发生 S3 相关错误
     */
    public boolean doesBucketExist(@NotNull String bucketName) {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            return true;
        } catch (NoSuchBucketException e) {
            return false;
        }
    }

    /**
     * 创建一个新的桶。
     *
     * @param bucketName 要创建的桶名称
     * @throws S3Exception 如果在创建过程中发生 S3 相关错误
     */
    public void createBucket(@NotNull String bucketName) {
        s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
    }

    /**
     * 检查指定的文件是否存在于桶中。
     *
     * @param bucketName 要检查的桶名称
     * @param key 要检查的对象的键
     * @return 如果文件存在返回 true，否则返回 false
     * @throws S3Exception 如果在检查过程中发生 S3 相关错误
     */
    public boolean doesFileExist(@NotNull String bucketName, @NotNull String key) {
        try {
            s3Client.headObject(
                    HeadObjectRequest.builder().bucket(bucketName).key(key).build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    /**
     * 上传文件到 S3
     *
     * @param bucketName 目标桶名称
     * @param objectName 对象的键（在 S3 中的唯一标识符）
     * @param file MultipartFile 对象，包含要上传的文件
     * @throws S3Exception 如果在上传过程中发生 S3 相关错误
     * @throws IOException 如果在读取文件内容时发生 I/O 错误
     */
    public void uploadFile(@NotNull String bucketName, @NotNull String objectName, @NotNull MultipartFile file)
            throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    }

    /**
     * 下载指定的文件到输出流。
     *
     * @param bucketName 源桶名称
     * @param key 要下载的对象的键
     * @param outputStream 用于写入下载内容的输出流
     * @throws S3Exception 如果在下载过程中发生 S3 相关错误
     */
    public void downloadFile(@NotNull String bucketName, @NotNull String key, @NotNull OutputStream outputStream) {
        s3Client.getObject(
                GetObjectRequest.builder().bucket(bucketName).key(key).build(),
                ResponseTransformer.toOutputStream(outputStream));
    }

    /**
     * 从指定的桶中删除文件。
     *
     * @param bucketName 包含要删除文件的桶名称
     * @param key 要删除的对象的键
     * @throws S3Exception 如果在删除过程中发生 S3 相关错误
     */
    public void deleteFile(@NotNull String bucketName, @NotNull String key) {
        s3Client.deleteObject(
                DeleteObjectRequest.builder().bucket(bucketName).key(key).build());
    }
}
