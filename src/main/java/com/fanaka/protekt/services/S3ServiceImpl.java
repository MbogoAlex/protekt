package com.fanaka.protekt.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.base-path:fanaka-protekt}")
    private String basePath;

    @Autowired
    public S3ServiceImpl(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        System.out.println("S3ServiceImpl initialized with bucket: " + bucketName + ", basePath: " + basePath);
    }

    @Override
    public String uploadFile(MultipartFile file, String folder) throws Exception {
        validateFile(file);
        
        try {
            // Generate unique filename while preserving extension
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            
            // Construct S3 key: fanaka-protekt/folder/filename
            String s3Key = String.format("%s/%s/%s", basePath, folder, uniqueFilename);
            
            // Prepare metadata
            Map<String, String> metadata = new HashMap<>();
            metadata.put("original-filename", originalFilename != null ? originalFilename : "unknown");
            metadata.put("upload-timestamp", LocalDateTime.now().toString());
            metadata.put("content-length", String.valueOf(file.getSize()));
            
            // Upload request
            System.out.println("Attempting to upload to bucket: " + bucketName + ", key: " + s3Key);
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .metadata(metadata)
                    .build();

            // Upload the file
            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            
            log.info("Successfully uploaded file to S3: {}", s3Key);
            return s3Key;
            
        } catch (IOException e) {
            log.error("Failed to read file during upload: {}", e.getMessage());
            throw new Exception("Failed to read file: " + e.getMessage(), e);
        } catch (S3Exception e) {
            log.error("S3 error during file upload: {}", e.getMessage());
            throw new Exception("Failed to upload to S3: " + e.getMessage(), e);
        }
    }

    @Override
    public String getFileUrl(String fileName, Duration expiry) throws Exception {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new Exception("File name cannot be empty");
        }
        
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(expiry)
                    .getObjectRequest(getRequest)
                    .build();

            String presignedUrl = s3Presigner.presignGetObject(presignRequest).url().toString();
            log.debug("Generated presigned URL for file: {}", fileName);
            return presignedUrl;
            
        } catch (S3Exception e) {
            log.error("Failed to generate presigned URL for {}: {}", fileName, e.getMessage());
            throw new Exception("Failed to generate file URL: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String fileName) throws Exception {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new Exception("File name cannot be empty");
        }
        
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteRequest);
            log.info("Successfully deleted file from S3: {}", fileName);
            
        } catch (S3Exception e) {
            log.error("Failed to delete file {}: {}", fileName, e.getMessage());
            throw new Exception("Failed to delete file: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean fileExists(String fileName) throws Exception {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }
        
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.headObject(headRequest);
            return true;
            
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            log.error("Error checking file existence for {}: {}", fileName, e.getMessage());
            throw new Exception("Failed to check file existence: " + e.getMessage(), e);
        }
    }

    @Override
    public long getFileSize(String fileName) throws Exception {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new Exception("File name cannot be empty");
        }
        
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            HeadObjectResponse response = s3Client.headObject(headRequest);
            return response.contentLength();
            
        } catch (NoSuchKeyException e) {
            throw new Exception("File not found: " + fileName);
        } catch (S3Exception e) {
            log.error("Failed to get file size for {}: {}", fileName, e.getMessage());
            throw new Exception("Failed to get file size: " + e.getMessage(), e);
        }
    }

    private void validateFile(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new Exception("File cannot be empty");
        }
        
        // Check file size (100MB limit)
        long maxSize = 100 * 1024 * 1024; // 100MB
        if (file.getSize() > maxSize) {
            throw new Exception("File size cannot exceed 100MB");
        }
        
        // Validate content type
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new Exception("File content type cannot be determined");
        }
        
        // Log file details
        log.debug("Validating file: name={}, size={}, contentType={}", 
                 file.getOriginalFilename(), file.getSize(), contentType);
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }
}