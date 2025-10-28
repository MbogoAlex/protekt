package com.fanaka.protekt.controllers;

import com.fanaka.protekt.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Map;

public interface CustomerController {
    ResponseEntity<Object> createCustomer(CustomerCreationDto customerCreationDto) throws Exception;
    ResponseEntity<Object> getCustomerById(Long id) throws Exception;
    ResponseEntity<Object> filterCustomers(String name, String nrc, String email, String gender, String verificationStatus, LocalDate createdAtStartDate, LocalDate createdAtEndDate, Integer page, Integer pageSize);
    ResponseEntity<Object> changeVerificationStatus(CustomerVerificationUpdateDto customerVerificationUpdateDto) throws Exception;
    ResponseEntity<Object> findCustomerVerificationById(Long id) throws Exception;
    ResponseEntity<Object> findCustomerVerificationByCustomerId(Long id) throws Exception;
    ResponseEntity<Object> filterCustomerVerifications(String name, String nrc, String email, String gender, String verificationStatus, LocalDate createdAtStartDate, LocalDate createdAtEndDate, LocalDate statusChangedAtStartDate, LocalDate statusChangedAtEndDate, Integer page, Integer pageSize);
    ResponseEntity<Object> uploadKycDocuments(
            Long customerId,
            Map<String, MultipartFile> documentsWithTypes
    );
    ResponseEntity<Object> getCustomerCheckDetails(String phone, String nrc);
}