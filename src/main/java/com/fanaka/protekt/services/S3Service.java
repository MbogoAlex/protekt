package com.fanaka.protekt.services;

import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

public interface S3Service {
    
    /**
     * Upload a file to S3 and return the filename (S3 key)
     * @param file The file to upload
     * @param folder The folder path in S3
     * @return The S3 key (filename) of the uploaded file
     */
    String uploadFile(MultipartFile file, String folder) throws Exception;
    
    /**
     * Get a presigned URL for downloading a file
     * @param fileName The S3 key of the file
     * @param expiry How long the URL should be valid for
     * @return The presigned URL
     */
    String getFileUrl(String fileName, Duration expiry) throws Exception;
    
    /**
     * Delete a file from S3
     * @param fileName The S3 key of the file to delete
     */
    void deleteFile(String fileName) throws Exception;
    
    /**
     * Check if a file exists in S3
     * @param fileName The S3 key to check
     * @return true if file exists, false otherwise
     */
    boolean fileExists(String fileName) throws Exception;
    
    /**
     * Get file metadata
     * @param fileName The S3 key of the file
     * @return File size in bytes
     */
    long getFileSize(String fileName) throws Exception;
}