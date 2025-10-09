package com.fanaka.protekt.services;

import com.fanaka.protekt.dao.CustomerDao;
import com.fanaka.protekt.dao.CustomerVerificationDao;
import com.fanaka.protekt.dao.MemberDao;
import com.fanaka.protekt.dao.KycDocumentDao;
import com.fanaka.protekt.dao.ProtektFileDao;
import com.fanaka.protekt.dto.*;
import com.fanaka.protekt.dto.mapper.CustomerMapper;
import com.fanaka.protekt.entities.Customer;
import com.fanaka.protekt.entities.CustomerVerification;
import com.fanaka.protekt.entities.Member;
import com.fanaka.protekt.entities.KycDocument;
import com.fanaka.protekt.entities.ProtektFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.MimeType;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerDao customerDao;
    private final CustomerVerificationDao customerVerificationDao;
    private final MemberDao memberDao;
    private final KycDocumentDao kycDocumentDao;
    private final ProtektFileDao protektFileDao;
    private final S3Service s3Service;
    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerServiceImpl(
            CustomerDao customerDao,
            CustomerVerificationDao customerVerificationDao,
            MemberDao memberDao,
            KycDocumentDao kycDocumentDao,
            ProtektFileDao protektFileDao,
            S3Service s3Service,
            CustomerMapper customerMapper
    ) {
        this.customerDao = customerDao;
        this.customerVerificationDao = customerVerificationDao;
        this.memberDao = memberDao;
        this.kycDocumentDao = kycDocumentDao;
        this.protektFileDao = protektFileDao;
        this.s3Service = s3Service;
        this.customerMapper = customerMapper;
    }

    @Transactional
    @Override
    public CustomerDto createCustomer(CustomerCreationDto customerCreationDto) throws Exception {

        LocalDateTime now = LocalDateTime.now();

        Member member = memberDao.findMemberById(customerCreationDto.getMemberId());

        Customer customer = Customer.builder()
                .member(member)
                .createdAt(Timestamp.valueOf(now))
                .updatedAt(Timestamp.valueOf(now))
                .build();

        customerDao.createCustomer(customer);

        CustomerVerification customerVerification = CustomerVerification.builder()
                .customer(customer)
                .kycDocuments(new ArrayList<>())
                .createdAt(Timestamp.valueOf(now))
                .updatedAt(Timestamp.valueOf(now))
                .verificationStatus("PENDING")
                .build();

        customerVerificationDao.createCustomerVerification(customerVerification);
        customer.setCustomerVerification(customerVerification);
        customerDao.updateCustomer(customer);

        return customerMapper.toCustomerDto(customer);
    }

    @Override
    public CustomerDto getCustomerById(Long id) throws Exception {
        return customerMapper.toCustomerDto(customerDao.getCustomerById(id));
    }

    @Override
    public CustomerDto getCustomerByMemberId(Long memberId) throws Exception {
        return customerMapper.toCustomerDto(customerDao.getCustomerByMemberId(memberId));
    }

    @Override
    public PaginationDto<CustomerDto> filterCustomers(String name, String nrc, String email, String gender, String verificationStatus, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, Integer page, Integer pageSize) {
        try {
            Page<Customer> customers = customerDao.filterCustomers(name, nrc, email, gender, verificationStatus, createdAtStartDate, createdAtEndDate, page, pageSize);
            
            // Convert entities to DTOs
            java.util.List<CustomerDto> customerDtos = new java.util.ArrayList<>();
            for (Customer customer : customers.getContent()) {
                try {
                    CustomerDto customerDto = customerMapper.toCustomerDto(customer);
                    customerDtos.add(customerDto);
                } catch (Exception e) {
                    // Log error but continue with other customers
                    System.err.println("Error mapping customer " + customer.getId() + ": " + e.getMessage());
                }
            }
            
            return new PaginationDto<>(
                customerDtos,
                customers.getNumber(),
                customers.getSize(),
                customers.getTotalElements()
            );
        } catch (Exception e) {
            // Handle any DAO exceptions
            System.err.println("Error filtering customers: " + e.getMessage());
            return new PaginationDto<>(new java.util.ArrayList<>(), page != null ? page : 0, pageSize != null ? pageSize : 10, 0);
        }
    }

    @Transactional
    @Override
    public CustomerVerificationDto changeVerificationStatus(CustomerVerificationUpdateDto customerVerificationUpdateDto) throws Exception {

        LocalDateTime now = LocalDateTime.now();

        CustomerVerification customerVerification = customerVerificationDao.getCustomerVerificationByCustomerId(customerVerificationUpdateDto.getCustomerId());

        if(!customerVerificationUpdateDto.getVerificationStatus().equalsIgnoreCase("PENDING") && !customerVerificationUpdateDto.getVerificationStatus().equalsIgnoreCase("SUBMITTED") && !customerVerificationUpdateDto.getVerificationStatus().equalsIgnoreCase("IN_REVIEW") && !customerVerificationUpdateDto.getVerificationStatus().equalsIgnoreCase("ON_HOLD") && !customerVerificationUpdateDto.getVerificationStatus().equalsIgnoreCase("REJECTED") && !customerVerificationUpdateDto.getVerificationStatus().equalsIgnoreCase("FLAGGED") && !customerVerificationUpdateDto.getVerificationStatus().equalsIgnoreCase("VERIFIED")) {
            throw new Exception("Invalid status");
        }

        if(customerVerificationUpdateDto.getVerificationStatus().equalsIgnoreCase(customerVerification.getVerificationStatus())) {
            throw new Exception("Verification status is already set");
        }

        customerVerification.setVerificationStatus(customerVerificationUpdateDto.getVerificationStatus().toUpperCase());
        customerVerification.setVerificationNotes(customerVerificationUpdateDto.getVerificationNotes());
        customerVerification.setUpdatedAt(Timestamp.valueOf(now));
        customerVerification.setStatusChangedAt(Timestamp.valueOf(now));

        if(customerVerification.getVerificationStatus().equalsIgnoreCase("VERIFIED")) {
            if(!customerVerification.getKycDocuments().isEmpty()) {
                for (KycDocument kycDocument : customerVerification.getKycDocuments()) {
                    kycDocument.setVerified(true);
                    kycDocumentDao.updateKycDocument(kycDocument);
                }
            }
        }

        return customerMapper.toCustomerVerificationDto(customerVerificationDao.updateCustomerVerification(customerVerification));
    }

    @Override
    public CustomerVerificationDto findCustomerVerificationById(Long id) throws Exception {
        return customerMapper.toCustomerVerificationDto(customerVerificationDao.getCustomerVerificationById(id));
    }

    @Override
    public CustomerVerificationDto findCustomerVerificationByCustomerId(Long id) throws Exception {
        return customerMapper.toCustomerVerificationDto(customerVerificationDao.getCustomerVerificationByCustomerId(id));
    }

    @Override
    public PaginationDto<CustomerVerificationDto> filterCustomerVerifications(String name, String nrc, String email, String gender, String verificationStatus, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, LocalDateTime statusChangedAtStartDate, LocalDateTime statusChangedAtEndDate, Integer page, Integer pageSize) {
        try {
            Page<CustomerVerification> customerVerifications = customerVerificationDao.filterCustomerVerifications(
                name, nrc, email, gender, verificationStatus, 
                createdAtStartDate, createdAtEndDate, 
                statusChangedAtStartDate, statusChangedAtEndDate, 
                page, pageSize
            );
            
            // Convert entities to DTOs
            java.util.List<CustomerVerificationDto> verificationDtos = new java.util.ArrayList<>();
            for (CustomerVerification verification : customerVerifications.getContent()) {
                try {
                    CustomerVerificationDto verificationDto = customerMapper.toCustomerVerificationDto(verification);
                    verificationDtos.add(verificationDto);
                } catch (Exception e) {
                    // Log error but continue with other verifications
                    System.err.println("Error mapping customer verification " + verification.getId() + ": " + e.getMessage());
                }
            }
            
            return new PaginationDto<>(
                verificationDtos,
                customerVerifications.getNumber(),
                customerVerifications.getSize(),
                customerVerifications.getTotalElements()
            );
        } catch (Exception e) {
            // Handle any DAO exceptions
            System.err.println("Error filtering customer verifications: " + e.getMessage());
            return new PaginationDto<>(new java.util.ArrayList<>(), page != null ? page : 0, pageSize != null ? pageSize : 10, 0);
        }
    }

    @Transactional
    @Override
    public CustomerVerificationDto uploadKycDocuments(Long customerId, MultipartFile[] files, String[] documentTypes) throws Exception {
        // Validate inputs
        if (files == null || files.length == 0) {
            throw new Exception("No files provided for upload");
        }
        
        if (documentTypes == null || documentTypes.length != files.length) {
            throw new Exception("Document types array must match files array length");
        }
        
        // Get customer and their verification
        Customer customer = customerDao.getCustomerById(customerId);
        if (customer == null) {
            throw new Exception("Customer not found with ID: " + customerId);
        }
        
        CustomerVerification customerVerification = customerVerificationDao.getCustomerVerificationByCustomerId(customerId);
        if (customerVerification == null) {
            throw new Exception("Customer verification not found for customer ID: " + customerId);
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // Upload files and create KYC documents
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            String documentType = documentTypes[i];
            
            // Validate document type
            if (documentType == null || documentType.trim().isEmpty()) {
                throw new Exception("Document type cannot be empty for file: " + file.getOriginalFilename());
            }
            
            // Upload file to S3
            String s3Key = s3Service.uploadFile(file, "kyc-documents");
            
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
                    // If mime type parsing fails, set to null
                    protektFile.setMimeType(null);
                }
            }
            
            protektFileDao.createProtektFile(protektFile);
            
            // Create KycDocument entity
            KycDocument kycDocument = new KycDocument();
            kycDocument.setCustomer(customer);
            kycDocument.setCustomerVerification(customerVerification);
            kycDocument.setDocumentType(documentType.toUpperCase());
            kycDocument.setVerified(false); // Default to not verified
            kycDocument.setProtektFile(protektFile);
            kycDocument.setCreatedAt(Timestamp.valueOf(now));
            kycDocument.setUpdatedAt(Timestamp.valueOf(now));
            
            kycDocumentDao.createKycDocument(kycDocument);
        }
        
        // Update customer verification status if it's PENDING
        if (!customerVerification.getVerificationStatus().equalsIgnoreCase("SUBMITTED")) {
            customerVerification.setVerificationStatus("SUBMITTED");
            customerVerification.setStatusChangedAt(Timestamp.valueOf(now));
        }

        customerVerification.setUpdatedAt(Timestamp.valueOf(now));
        customerVerificationDao.updateCustomerVerification(customerVerification);
        
        // Return updated customer verification DTO
        return customerMapper.toCustomerVerificationDto(customerVerificationDao.getCustomerVerificationByCustomerId(customerId));
    }
}
