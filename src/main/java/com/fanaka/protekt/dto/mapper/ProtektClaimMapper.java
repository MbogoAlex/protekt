package com.fanaka.protekt.dto.mapper;

import com.fanaka.protekt.dto.FileDto;
import com.fanaka.protekt.dto.ProtektClaimDto;
import com.fanaka.protekt.entities.KycDocument;
import com.fanaka.protekt.entities.ProtektClaim;
import com.fanaka.protekt.entities.ProtektClaimDocument;
import com.fanaka.protekt.entities.ProtektFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProtektClaimMapper {
    private final FileMapper fileMapper;

    @Autowired
    public ProtektClaimMapper(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public ProtektClaimDto protektClaimDto(ProtektClaim protektClaim) throws Exception {

        List<FileDto> documents = new ArrayList<>();

        if(protektClaim.getDocuments() != null && !protektClaim.getDocuments().isEmpty()) {
            for (ProtektClaimDocument protektClaimDocument : protektClaim.getDocuments()) {
                documents.add(fileMapper.toFileDto(protektClaimDocument.getProtektFile(), null, protektClaimDocument));
            }
        }

        return ProtektClaimDto.builder()
                .id(protektClaim.getId())
                .incident(protektClaim.getIncident())
                .dateOfIncident(protektClaim.getDateOfIncident())
                .timeOfIncident(protektClaim.getTimeOfIncident())
                .createdAt(protektClaim.getCreatedAt() != null ? protektClaim.getCreatedAt().toLocalDateTime() : null)
                .updatedAt(protektClaim.getUpdatedAt() != null ? protektClaim.getUpdatedAt().toLocalDateTime() : null)
                .files(documents)
                .build();
    }
}
