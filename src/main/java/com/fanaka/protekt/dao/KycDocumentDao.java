package com.fanaka.protekt.dao;

import com.fanaka.protekt.entities.KycDocument;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface KycDocumentDao {
    KycDocument createKycDocument(KycDocument kycDocument);
    KycDocument updateKycDocument(KycDocument kycDocument);
    KycDocument findKycDocumentById(Long id);
    KycDocument findKycDocumentByCustomerId(Long id);
    KycDocument findKycDocumentByVerificationId(Long id);
    Page<KycDocument> findAllKycDocuments(Long customerId, Long verificationId, Boolean verified, String documentType, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, Integer page, Integer pageSize);
}
