package com.fanaka.protekt.dto.mapper;

import com.fanaka.protekt.dto.CustomerDto;
import com.fanaka.protekt.dto.CustomerVerificationDto;
import com.fanaka.protekt.dto.FileDto;
import com.fanaka.protekt.entities.Customer;
import com.fanaka.protekt.entities.CustomerVerification;
import com.fanaka.protekt.entities.KycDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomerMapper {

    private final FileMapper fileMapper;

    @Autowired
    public CustomerMapper(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public CustomerDto toCustomerDto(Customer customer) throws Exception {
        if (customer == null) {
            return null;
        }

        List<KycDocument> kycDocuments = customer.getCustomerVerification().getKycDocuments();

        List<FileDto> documents = new ArrayList<>();

        if(!kycDocuments.isEmpty()) {
            for (KycDocument kycDocument : kycDocuments) {
                documents.add(fileMapper.toFileDto(kycDocument.getProtektFile(), kycDocument));
            }
        }

        return CustomerDto.builder()
                .id(customer.getId())
                .memberId(customer.getMember() != null ? customer.getMember().getId() : null)
                .avatar(customer.getAvatar() != null ? fileMapper.toFileDto(customer.getAvatar(), null) : null)
                .firstName(customer.getMember() != null ? customer.getMember().getFirstName() : null)
                .middleName(customer.getMember() != null ? customer.getMember().getMiddleName() : null)
                .otherName(customer.getMember() != null ? customer.getMember().getOtherName() : null)
                .lastName(customer.getMember() != null ? customer.getMember().getLastName() : null)
                .gender(customer.getMember() != null ? customer.getMember().getGender() : null)
                .dob(customer.getMember() != null ? customer.getMember().getDob() : null)
                .idNumber(customer.getMember() != null ? customer.getMember().getIdNumber() : null)
                .provider(customer.getMember() != null ? customer.getMember().getProvider() : null)
                .mobile(customer.getMember() != null ? customer.getMember().getMobile() : null)
                .type(customer.getMember() != null ? customer.getMember().getIdType() : null)
                .verificationStatus(customer.getCustomerVerification() != null ? customer.getCustomerVerification().getVerificationStatus() : null)
                .customerVerificationId(customer.getCustomerVerification() != null ? customer.getCustomerVerification().getId() : null)
                .kycDocuments(documents)
                .customerCreatedAt(customer.getCreatedAt() != null ? customer.getCreatedAt().toLocalDateTime() : null)
                .customerUpdatedAt(customer.getUpdatedAt() != null ? customer.getUpdatedAt().toLocalDateTime() : null)
                .memberCreatedAt(customer.getMember() != null && customer.getMember().getTimestamp() != null ? customer.getMember().getTimestamp().toLocalDateTime() : null)
                .memberUpdatedAt(customer.getMember() != null && customer.getMember().getUpdated() != null ? customer.getMember().getUpdated().toLocalDateTime() : null)
                .build();
    }

    public CustomerVerificationDto toCustomerVerificationDto(CustomerVerification customerVerification) throws Exception {
        List<KycDocument> kycDocuments = customerVerification.getKycDocuments();

        List<FileDto> documents = new ArrayList<>();

        if(!kycDocuments.isEmpty()) {
            for (KycDocument kycDocument : kycDocuments) {
                documents.add(fileMapper.toFileDto(kycDocument.getProtektFile(), kycDocument));
            }
        }

        return CustomerVerificationDto.builder()
                .id(customerVerification.getId())
                .customerId(customerVerification.getCustomer().getId())
                .kycDocuments(documents)
                .verificationNotes(customerVerification.getVerificationNotes())
                .verificationStatus(customerVerification.getVerificationStatus())
                .createdAt(customerVerification.getCreatedAt() != null ? customerVerification.getCreatedAt().toLocalDateTime() : null)
                .updatedAt(customerVerification.getUpdatedAt() != null ? customerVerification.getUpdatedAt().toLocalDateTime() : null)
                .statusChangedAt(customerVerification.getStatusChangedAt() != null ? customerVerification.getStatusChangedAt().toLocalDateTime() : null)
                .build();
    }
}
