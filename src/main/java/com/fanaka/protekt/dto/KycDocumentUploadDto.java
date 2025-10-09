package com.fanaka.protekt.dto;

import org.springframework.web.multipart.MultipartFile;

public class KycDocumentUploadDto {
    private MultipartFile file;
    private String documentType;

    public KycDocumentUploadDto() {}

    public KycDocumentUploadDto(MultipartFile file, String documentType) {
        this.file = file;
        this.documentType = documentType;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
}