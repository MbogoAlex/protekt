package com.fanaka.protekt.services;

import com.fanaka.protekt.dao.MemberDao;
import com.fanaka.protekt.dao.ProductDao;
import com.fanaka.protekt.dao.ProtektClaimDao;
import com.fanaka.protekt.dao.ProtektFileDao;
import com.fanaka.protekt.dto.ProtektClaimCreationDto;
import com.fanaka.protekt.dto.ProtektClaimDto;
import com.fanaka.protekt.dto.mapper.ProtektClaimMapper;
import com.fanaka.protekt.entities.Member;
import com.fanaka.protekt.entities.ProductPolicy;
import com.fanaka.protekt.entities.ProtektClaim;
import com.fanaka.protekt.entities.ProtektClaimDocument;
import com.fanaka.protekt.entities.ProtektFile;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProtektClaimServiceImpl implements ProtektClaimService {

    private final ProtektClaimDao protektClaimDao;
    private final ProtektClaimMapper protektClaimMapper;
    private final ProductDao productDao;
    private final MemberDao memberDao;
    private final S3Service s3Service;
    private final ProtektFileDao protektFileDao;
    private final EntityManager entityManager;

    @Autowired
    public ProtektClaimServiceImpl(
            ProtektClaimDao protektClaimDao,
            ProductDao productDao,
            MemberDao memberDao,
            ProtektClaimMapper protektClaimMapper,
            S3Service s3Service,
            ProtektFileDao protektFileDao,
            EntityManager entityManager
    ) {
        this.protektClaimDao = protektClaimDao;
        this.productDao = productDao;
        this.memberDao = memberDao;
        this.protektClaimMapper = protektClaimMapper;
        this.s3Service = s3Service;
        this.protektFileDao = protektFileDao;
        this.entityManager = entityManager;
    }

    @Transactional
    @Override
    public ProtektClaimDto createClaim(ProtektClaimCreationDto claim, MultipartFile[] files) {
        try {
            // Validate required inputs
            if (claim.getProduct_policy_id() == null) {
                throw new RuntimeException("Product Policy ID is required");
            }
            if (claim.getStaff_id() == null) {
                throw new RuntimeException("Staff ID is required");
            }

            // Get entities
            ProductPolicy productPolicy = productDao.getProductPolicyById(claim.getProduct_policy_id());
            if (productPolicy == null) {
                throw new RuntimeException("Product Policy not found with ID: " + claim.getProduct_policy_id());
            }

            Member staff = memberDao.findMemberById(claim.getStaff_id());
            if (staff == null) {
                throw new RuntimeException("Staff member not found with ID: " + claim.getStaff_id());
            }

            LocalDateTime now = LocalDateTime.now();

            // Create ProtektClaim entity first (without documents)
            ProtektClaim protektClaim = ProtektClaim.builder()
                    .productPolicy(productPolicy)
                    .staff(staff)
                    .incident(claim.getIncident())
                    .dateOfIncident(claim.getDate_of_incident())
                    .timeOfIncident(claim.getTime_of_incident())
                    .createdAt(Timestamp.valueOf(now))
                    .updatedAt(Timestamp.valueOf(now))
                    .build();

            // Save the claim first to get the ID
            protektClaimDao.createClaim(protektClaim);

            // Handle file uploads and create claim documents
            List<ProtektClaimDocument> claimDocuments = new ArrayList<>();
            if (files != null && files.length > 0) {
                for (MultipartFile file : files) {
                    if (file != null && !file.isEmpty()) {
                        // Get content type before uploading (to avoid stream issues)
                        String contentType = file.getContentType();

                        // Upload file to S3
                        String s3Key = s3Service.uploadFile(file, "claim-evidence");

                        // Create ProtektFile entity
                        ProtektFile protektFile = new ProtektFile();
                        protektFile.setFileName(s3Key);
                        protektFile.setCreatedAt(Timestamp.valueOf(now));
                        protektFile.setUpdatedAt(Timestamp.valueOf(now));

                        // Set mime type
                        if (contentType != null) {
                            try {
                                protektFile.setMimeType(MimeType.valueOf(contentType));
                            } catch (Exception e) {
                                // If mime type parsing fails, set to null
                                protektFile.setMimeType(null);
                            }
                        }

                        protektFileDao.createProtektFile(protektFile);

                        // Create ProtektClaimDocument entity
                        ProtektClaimDocument claimDocument = ProtektClaimDocument.builder()
                                .protektClaim(protektClaim)
                                .protektFile(protektFile)
                                .documentType("EVIDENCE") // Default document type
                                .verified(false) // Default to not verified
                                .createdAt(Timestamp.valueOf(now))
                                .updatedAt(Timestamp.valueOf(now))
                                .build();

                        entityManager.persist(claimDocument);
                        claimDocuments.add(claimDocument);
                    }
                }
            }

            // Update the claim with documents
            protektClaim.setDocuments(claimDocuments);

            // Return DTO
            return protektClaimMapper.protektClaimDto(protektClaim);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create claim: " + e.getMessage(), e);
        }
    }

    @Transactional
    @Override
    public ProtektClaimDto updateClaim(ProtektClaimCreationDto claim, MultipartFile[] files) {
        try {
            // Note: For update, we need to identify the claim to update
            // Since the DTO doesn't have an ID field, we need to modify the interface
            // For now, assume the claim has a hidden ID or we use another mechanism
            throw new RuntimeException("Update claim method requires claim ID - interface needs modification");

            // TODO: This would be the implementation once we have the claim ID:
            /*
            // Validate required inputs
            if (claimId == null) {
                throw new RuntimeException("Claim ID is required for update");
            }

            // Get existing claim
            ProtektClaim existingClaim = protektClaimDao.getProtektClaimById(claimId);
            if (existingClaim == null) {
                throw new RuntimeException("Claim not found with ID: " + claimId);
            }

            LocalDateTime now = LocalDateTime.now();

            // Update claim fields if provided
            if (claim.getIncident() != null) {
                existingClaim.setIncident(claim.getIncident());
            }
            if (claim.getDateOfIncident() != null) {
                existingClaim.setDateOfIncident(claim.getDateOfIncident());
            }
            if (claim.getTimeOfIncident() != null) {
                existingClaim.setTimeOfIncident(claim.getTimeOfIncident());
            }

            // Handle new file uploads if provided
            if (files != null && files.length > 0) {
                List<ProtektFile> existingFiles = existingClaim.getFiles() != null ?
                    new ArrayList<>(existingClaim.getFiles()) : new ArrayList<>();

                for (MultipartFile file : files) {
                    if (file != null && !file.isEmpty()) {
                        // Upload file to S3
                        String s3Key = s3Service.uploadFile(file, "claim-evidence");

                        // Create ProtektFile entity
                        ProtektFile protektFile = new ProtektFile();
                        protektFile.setFileName(s3Key);
                        protektFile.setCreatedAt(Timestamp.valueOf(now));
                        protektFile.setUpdatedAt(Timestamp.valueOf(now));

                        // Set mime type
                        String contentType = file.getContentType();
                        if (contentType != null) {
                            try {
                                protektFile.setMimeType(MimeType.valueOf(contentType));
                            } catch (Exception e) {
                                protektFile.setMimeType(null);
                            }
                        }

                        protektFileDao.createProtektFile(protektFile);
                        existingFiles.add(protektFile);
                    }
                }
                existingClaim.setFiles(existingFiles);
            }

            existingClaim.setUpdatedAt(Timestamp.valueOf(now));

            // Update the claim
            protektClaimDao.updateProtektClaim(existingClaim);

            // Return DTO
            return protektClaimMapper.protektClaimDto(existingClaim);
            */

        } catch (Exception e) {
            throw new RuntimeException("Failed to update claim: " + e.getMessage(), e);
        }
    }

    @Override
    public ProtektClaimDto getClaimById(Long id) {
        try {
            // Validate input
            if (id == null) {
                throw new RuntimeException("Claim ID is required");
            }

            // Get claim from database
            ProtektClaim protektClaim = protektClaimDao.getClaimById(id);
            if (protektClaim == null) {
                throw new RuntimeException("Claim not found with ID: " + id);
            }

            // Convert to DTO and return
            return protektClaimMapper.protektClaimDto(protektClaim);

        } catch (Exception e) {
            throw new RuntimeException("Failed to get claim: " + e.getMessage(), e);
        }
    }
}
