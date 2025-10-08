package com.fanaka.protekt.dao;

import com.fanaka.protekt.entities.Customer;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface CustomerDao {
    Customer createCustomer(Customer customer);
    Customer updateCustomer(Customer customer);
    Customer getCustomerById(Long id);
    Page<Customer> filterCustomers(String name, String nrc, String email, String gender, String verificationStatus, LocalDateTime createdAtStartDate, LocalDateTime createdAtEndDate, Integer page, Integer pageSize);
}
