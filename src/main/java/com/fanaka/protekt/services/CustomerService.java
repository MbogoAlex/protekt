package com.fanaka.protekt.services;

import com.fanaka.protekt.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public interface CustomerService {
    CustomerDto createCustomer(CustomerCreationDto customerCreationDto) throws Exception;
    CustomerDto getCustomerById(Long id) throws Exception;
    CustomerDto getCustomerByMemberId(Long memberId) throws Exception;
    PaginationDto<CustomerDto> filterCustomers(String name, String nrc, String email, String gender, String verificationStatus, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, Integer page, Integer pageSize);
    CustomerVerificationDto changeVerificationStatus(CustomerVerificationUpdateDto customerVerificationUpdateDto) throws Exception;
    CustomerVerificationDto findCustomerVerificationById(Long id) throws Exception;
    CustomerVerificationDto findCustomerVerificationByCustomerId(Long id) throws Exception;
    PaginationDto<CustomerVerificationDto> filterCustomerVerifications(String name, String nrc, String email, String gender, String verificationStatus, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, LocalDateTime statusChangedAtStartDate, LocalDateTime statusChangedAtEndDate, Integer page, Integer pageSize);
    
    CustomerVerificationDto uploadKycDocuments(Long customerId, MultipartFile[] files, String[] documentTypes) throws Exception;
    CustomerCheckDto getCustomerCheckDetails(String phone, String nrc);
}
