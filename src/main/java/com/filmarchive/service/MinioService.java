package com.filmarchive.service;

import io.minio.*;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.endpoint}")
    private String endpoint;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public UploadResult upload(MultipartFile file, String folder) {
        try {
            ensureBucketExists();

            String ext = getExtension(file.getOriginalFilename());
            String slug = folder.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
            String key = slug + "/" + UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            String url = endpoint + "/" + bucket + "/" + key;
            return new UploadResult(key, url);

        } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to upload file to MinIO: " + e.getMessage(), e);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
    }

    public void delete(String key) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from MinIO: " + e.getMessage(), e);
        }
    }

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            // Set public read policy
            String policy = """
                    {
                      "Version":"2012-10-17",
                      "Statement":[{
                        "Effect":"Allow",
                        "Principal":{"AWS":["*"]},
                        "Action":["s3:GetObject"],
                        "Resource":["arn:aws:s3:::%s/*"]
                      }]
                    }
                    """.formatted(bucket);
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(bucket)
                    .config(policy)
                    .build());
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    public record UploadResult(String key, String url) {}

    /**
     * Create a "folder" in the given bucket by uploading an empty object with a trailing slash.
     * Ensures the bucket exists (creates it if missing).
     */
    public void createFolder(String bucket, String folder) throws Exception {
        // Ensure bucket exists
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }

        // Create empty object (folder marker). S3/MinIO treat keys ending with '/' as folders in many UIs.
        byte[] empty = new byte[0];
        try (ByteArrayInputStream bais = new ByteArrayInputStream(empty)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(folder)
                            .stream(bais, empty.length, -1)
                            .contentType("application/x-directory")
                            .build()
            );
        }
    }

    public void createDefaultFolder(String folder) throws Exception {
        createFolder(bucket, folder);
    }
}
