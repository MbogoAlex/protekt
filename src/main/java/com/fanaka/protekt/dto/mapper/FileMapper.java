package com.fanaka.protekt.dto.mapper;

import com.fanaka.protekt.dto.FileDto;
import com.fanaka.protekt.entities.KycDocument;
import com.fanaka.protekt.entities.ProtektClaimDocument;
import com.fanaka.protekt.entities.ProtektFile;
import com.fanaka.protekt.services.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class FileMapper {

    public S3Service s3Service;

    @Autowired
    public FileMapper(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    FileDto toFileDto(ProtektFile protektFile, KycDocument kycDocument, ProtektClaimDocument protektClaimDocument) throws Exception {

        FileDto file = FileDto.builder()
                .id(protektFile.getId())
                .url(s3Service.getFileUrl(protektFile.getFileName(), Duration.ofHours(24)))
                .build();

        if(kycDocument != null) {
            file.setDocumentType(kycDocument.getDocumentType());
            file.setDocumentVerified(kycDocument.getVerified());
        }

        if(protektClaimDocument != null) {
            file.setDocumentType(protektClaimDocument.getDocumentType());
            file.setDocumentVerified(protektClaimDocument.getVerified());
        }

        return file;

    }
}
