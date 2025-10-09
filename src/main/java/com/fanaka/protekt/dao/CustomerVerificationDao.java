package com.fanaka.protekt.dao;

import com.fanaka.protekt.entities.CustomerVerification;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface CustomerVerificationDao {
    CustomerVerification createCustomerVerification(CustomerVerification customerVerification);
    CustomerVerification updateCustomerVerification(CustomerVerification customerVerification);
    CustomerVerification getCustomerVerificationById(Long id);
    CustomerVerification getCustomerVerificationByCustomerId(Long id);
    Page<CustomerVerification> filterCustomerVerifications(String name, String nrc, String email, String gender, String verificationStatus, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, LocalDateTime statusChangedAtStartDate, LocalDateTime statusChangedAtEndDate, Integer page, Integer pageSize);
}
