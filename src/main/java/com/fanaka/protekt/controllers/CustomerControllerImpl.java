package com.fanaka.protekt.controllers;

import com.fanaka.protekt.config.BuildResponse;
import com.fanaka.protekt.dto.*;
import com.fanaka.protekt.services.CustomerService;
import com.fanaka.protekt.services.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/customers")
@CrossOrigin(origins = "*")
public class CustomerControllerImpl implements CustomerController {

    private final CustomerService customerService;
    private final MemberService memberService;
    private final BuildResponse buildResponse;

    @Autowired
    public CustomerControllerImpl(
            CustomerService customerService,
            BuildResponse buildResponse,
            MemberService memberService
    ) {
        this.customerService = customerService;
        this.memberService = memberService;
        this.buildResponse = buildResponse;
    }

    @PostMapping
    @Override
    public ResponseEntity<Object> createCustomer(@RequestBody CustomerCreationDto customerCreationDto) throws Exception {
        try {
            CustomerDto customer = customerService.getCustomerByMemberId(customerCreationDto.getMemberId());

            Boolean isMember = memberService.isMember(customerCreationDto.getMemberId());

            if(!isMember) {
                Map<String, Object> errors = new HashMap<>();
                errors.put("error", "Member not found");
                return buildResponse.error("Failed to create customer", errors, HttpStatus.BAD_REQUEST);
            }

            if(customer != null) {
                Map<String, Object> errors = new HashMap<>();
                errors.put("error", "This customer already exists");
                return buildResponse.error("Failed to create customer", errors, HttpStatus.BAD_REQUEST);
            }

            CustomerDto result = customerService.createCustomer(customerCreationDto);
            return buildResponse.success(result, "Customer created successfully", null, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("general", e.getMessage());
            return buildResponse.error("Failed to create customer", errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<Object> getCustomerById(@PathVariable Long id) throws Exception {
        try {
            CustomerDto result = customerService.getCustomerById(id);
            String message = result != null ? "Customer found" : "Customer not found";
            return buildResponse.success(result, message, null, result != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("general", e.getMessage());
            HttpStatus status = e.getMessage().contains("not found") ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            return buildResponse.error("Failed to retrieve customer", errors, status);
        }
    }

    @GetMapping
    @Override
    public ResponseEntity<Object> filterCustomers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String nrc,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String verificationStatus,
            @RequestParam(required = false) LocalDate createdAtStartDate,
            @RequestParam(required = false) LocalDate createdAtEndDate,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            PaginationDto<CustomerDto> result = customerService.filterCustomers(
                name, nrc, email, gender, verificationStatus,
                createdAtStartDate != null ? createdAtStartDate.atStartOfDay() : null,
                createdAtEndDate != null ? createdAtEndDate.atTime(23, 59, 59) : null,
                page, pageSize
            );
            
            // Build meta according to company standard
            Map<String, Object> meta = new HashMap<>();
            meta.put("page", result.getPageNumber() + 1); // Convert 0-based to 1-based
            meta.put("size", result.getPageSize());
            meta.put("total", result.getTotalElements());
            
            return buildResponse.success(result.getContent(), "Customers retrieved successfully", meta, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("general", e.getMessage());
            return buildResponse.error("Failed to filter customers", errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/verification")
    @Override
    public ResponseEntity<Object> changeVerificationStatus(@RequestBody CustomerVerificationUpdateDto customerVerificationUpdateDto) throws Exception {
        try {
            CustomerVerificationDto result = customerService.changeVerificationStatus(customerVerificationUpdateDto);
            return buildResponse.success(result, "Verification status updated successfully", null, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("general", e.getMessage());
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e.getMessage().contains("not found")) {
                status = HttpStatus.NOT_FOUND;
            } else if (e.getMessage().contains("Invalid") || e.getMessage().contains("already set")) {
                status = HttpStatus.BAD_REQUEST;
            }
            return buildResponse.error("Failed to update verification status", errors, status);
        }
    }

    @GetMapping("/verification/{id}")
    @Override
    public ResponseEntity<Object> findCustomerVerificationById(@PathVariable Long id) throws Exception {
        try {
            CustomerVerificationDto result = customerService.findCustomerVerificationById(id);
            return buildResponse.success(result, "Customer verification retrieved successfully", null, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("general", e.getMessage());
            HttpStatus status = e.getMessage().contains("not found") ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            return buildResponse.error("Failed to retrieve customer verification", errors, status);
        }
    }

    @GetMapping("/{id}/verification")
    @Override
    public ResponseEntity<Object> findCustomerVerificationByCustomerId(@PathVariable Long id) throws Exception {
        try {
            CustomerVerificationDto result = customerService.findCustomerVerificationByCustomerId(id);
            return buildResponse.success(result, "Customer verification retrieved successfully", null, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("general", e.getMessage());
            HttpStatus status = e.getMessage().contains("not found") ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            return buildResponse.error("Failed to retrieve customer verification", errors, status);
        }
    }

    @GetMapping("/verifications")
    @Override
    public ResponseEntity<Object> filterCustomerVerifications(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String nrc,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String verificationStatus,
            @RequestParam(required = false) LocalDate createdAtStartDate,
            @RequestParam(required = false) LocalDate createdAtEndDate,
            @RequestParam(required = false) LocalDate statusChangedAtStartDate,
            @RequestParam(required = false) LocalDate statusChangedAtEndDate,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        try {
            PaginationDto<CustomerVerificationDto> result = customerService.filterCustomerVerifications(
                name, nrc, email, gender, verificationStatus,
                createdAtStartDate != null ? createdAtStartDate.atStartOfDay() : null,
                createdAtEndDate != null ? createdAtEndDate.atTime(23, 59, 59) : null,
                statusChangedAtStartDate != null ? statusChangedAtStartDate.atStartOfDay() : null,
                statusChangedAtEndDate != null ? statusChangedAtEndDate.atTime(23, 59, 59) : null,
                page, pageSize
            );
            
            // Build meta according to company standard
            Map<String, Object> meta = new HashMap<>();
            meta.put("page", result.getPageNumber() + 1); // Convert 0-based to 1-based
            meta.put("size", result.getPageSize());
            meta.put("total", result.getTotalElements());
            
            return buildResponse.success(result.getContent(), "Customer verifications retrieved successfully", meta, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("general", e.getMessage());
            return buildResponse.error("Failed to filter customer verifications", errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{customerId}/kyc-documents")
    @Override
    public ResponseEntity<Object> uploadKycDocuments(
            @PathVariable Long customerId,
            @RequestParam Map<String, MultipartFile> documentsWithTypes
    ) {
        try {
            // Validate inputs
            if (documentsWithTypes == null || documentsWithTypes.isEmpty()) {
                Map<String, Object> errors = new HashMap<>();
                errors.put("documents", "At least one document is required");
                return buildResponse.error("No documents provided", errors, HttpStatus.BAD_REQUEST);
            }

            // Validate document types
            String[] validDocumentTypes = {
                "NATIONAL_ID", "PASSPORT", "BIRTH_CERTIFICATE", "DRIVING_LICENSE", 
                "VOTER_CARD", "HUDUMA_CARD", "MILITARY_ID", "ALIEN_ID", "REFUGEE_ID"
            };

            // Prepare arrays for service call
            MultipartFile[] files = new MultipartFile[documentsWithTypes.size()];
            String[] documentTypes = new String[documentsWithTypes.size()];
            
            int index = 0;
            for (Map.Entry<String, MultipartFile> entry : documentsWithTypes.entrySet()) {
                String documentType = entry.getKey();
                MultipartFile file = entry.getValue();
                
                // Validate document type
                if (documentType == null || documentType.trim().isEmpty()) {
                    Map<String, Object> errors = new HashMap<>();
                    errors.put("documentType_" + documentType, "Document type cannot be empty");
                    return buildResponse.error("Invalid document type", errors, HttpStatus.BAD_REQUEST);
                }

                boolean isValidType = false;
                for (String validType : validDocumentTypes) {
                    if (validType.equalsIgnoreCase(documentType.trim())) {
                        isValidType = true;
                        break;
                    }
                }

                if (!isValidType) {
                    Map<String, Object> errors = new HashMap<>();
                    errors.put("documentType_" + documentType, "Invalid document type: " + documentType);
                    return buildResponse.error("Invalid document type", errors, HttpStatus.BAD_REQUEST);
                }
                
                // Validate file
                if (file == null || file.isEmpty()) {
                    Map<String, Object> errors = new HashMap<>();
                    errors.put("file_" + documentType, "File cannot be empty for document type: " + documentType);
                    return buildResponse.error("Empty file detected", errors, HttpStatus.BAD_REQUEST);
                }

                // Check file size (limit to 10MB per file)
                if (file.getSize() > 10 * 1024 * 1024) {
                    Map<String, Object> errors = new HashMap<>();
                    errors.put("file_" + documentType, "File size cannot exceed 10MB for document type: " + documentType);
                    return buildResponse.error("File too large", errors, HttpStatus.BAD_REQUEST);
                }

                // Check file type (accept common image and PDF formats)
                String contentType = file.getContentType();
                System.out.println("File: " + file.getOriginalFilename() + ", Content-Type: " + contentType);
                if (contentType == null || (!contentType.startsWith("image/") && !contentType.equals("application/pdf"))) {
                    Map<String, Object> errors = new HashMap<>();
                    errors.put("file_" + documentType, "Only image files (JPG, PNG, etc.) and PDF files are allowed for document type: " + documentType + ". Detected content type: " + contentType);
                    return buildResponse.error("Invalid file type", errors, HttpStatus.BAD_REQUEST);
                }
                
                files[index] = file;
                documentTypes[index] = documentType.toUpperCase();
                index++;
            }

            // Upload KYC documents
            CustomerVerificationDto result = customerService.uploadKycDocuments(customerId, files, documentTypes);
            
            return buildResponse.success(
                result, 
                "KYC documents uploaded successfully", 
                null, 
                HttpStatus.CREATED
            );

        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("general", e.getMessage());
            
            // Determine appropriate HTTP status based on error message
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e.getMessage().contains("not found")) {
                status = HttpStatus.NOT_FOUND;
            } else if (e.getMessage().contains("Invalid") || e.getMessage().contains("cannot be empty")) {
                status = HttpStatus.BAD_REQUEST;
            }
            
            return buildResponse.error("Failed to upload KYC documents", errors, status);
        }
    }

    @GetMapping("/customer-check")
    @Override
    public ResponseEntity<Object> getCustomerCheckDetails(
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "nrc", required = false) String nrc
    ) {
        try {
            return buildResponse.success(customerService.getCustomerCheckDetails(phone, nrc), "Customer check details successfully", null, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("general", e.getMessage());


            return buildResponse.error("Customer details check error", errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}