package com.fanaka.protekt.controllers;

import com.fanaka.protekt.config.BuildResponse;
import com.fanaka.protekt.dto.ProtektClaimCreationDto;
import com.fanaka.protekt.dto.ProtektClaimDto;
import com.fanaka.protekt.services.ProtektClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/claims")
@CrossOrigin(origins = "*")
public class ProtektClaimControllerImpl implements ProtektClaimController {

    private final ProtektClaimService protektClaimService;
    private final BuildResponse buildResponse;

    @Autowired
    public ProtektClaimControllerImpl(
            ProtektClaimService protektClaimService,
            BuildResponse buildResponse
    ) {
        this.protektClaimService = protektClaimService;
        this.buildResponse = buildResponse;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    @Override
    public ResponseEntity<Object> createClaim(
            @ModelAttribute ProtektClaimCreationDto claim,
            @RequestParam(value = "files", required = false) MultipartFile[] files
    ) throws Exception {
        try {
            // Validate required fields
            if (claim.getProduct_policy_id() == null) {
                Map<String, Object> errors = new HashMap<>();
                errors.put("productPolicyId", "Product Policy ID is required");
                return buildResponse.error("Failed to create claim", errors, HttpStatus.BAD_REQUEST);
            }

            if (claim.getStaff_id() == null) {
                Map<String, Object> errors = new HashMap<>();
                errors.put("staffId", "Staff ID is required");
                return buildResponse.error("Failed to create claim", errors, HttpStatus.BAD_REQUEST);
            }

            if (claim.getIncident() == null || claim.getIncident().trim().isEmpty()) {
                Map<String, Object> errors = new HashMap<>();
                errors.put("incident", "Incident description is required");
                return buildResponse.error("Failed to create claim", errors, HttpStatus.BAD_REQUEST);
            }

            // Create the claim
            ProtektClaimDto createdClaim = protektClaimService.createClaim(claim, files);

            return buildResponse.success(createdClaim, "Claim created successfully", null, HttpStatus.CREATED);

        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("error", e.getMessage());
            return buildResponse.error("Failed to create claim", errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @Override
    public ResponseEntity<Object> updateClaim(
            @ModelAttribute ProtektClaimCreationDto claim,
            @RequestParam(value = "files", required = false) MultipartFile[] files
    ) throws Exception {
        try {
            // Note: This will throw an exception as mentioned in the service implementation
            // because the interface needs modification to include claim ID
            ProtektClaimDto updatedClaim = protektClaimService.updateClaim(claim, files);

            return buildResponse.success(updatedClaim,"Claim updated successfully", null, HttpStatus.OK);

        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("error", e.getMessage());
            return buildResponse.error("Failed to update claim", errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<Object> getClaimById(@PathVariable Long id) throws Exception {
        try {
            // Validate input
            if (id == null || id <= 0) {
                Map<String, Object> errors = new HashMap<>();
                errors.put("id", "Valid claim ID is required");
                return buildResponse.error("Failed to get claim", errors, HttpStatus.BAD_REQUEST);
            }

            ProtektClaimDto claim = protektClaimService.getClaimById(id);

            return buildResponse.success(claim, "Claim retrieved successfully", null, HttpStatus.OK);

        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                Map<String, Object> errors = new HashMap<>();
                errors.put("error", e.getMessage());
                return buildResponse.error("Claim not found", errors, HttpStatus.NOT_FOUND);
            }

            Map<String, Object> errors = new HashMap<>();
            errors.put("error", e.getMessage());
            return buildResponse.error("Failed to get claim", errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}