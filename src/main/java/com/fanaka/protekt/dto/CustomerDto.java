package com.fanaka.protekt.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CustomerDto {
    private Long id;
    private Long memberId;
    private FileDto avatar;
    private String firstName;
    private String middleName;
    private String otherName;
    private String lastName;
    private String gender;
    private LocalDate dob;
    private String idNumber;
    private String provider;
    private String mobile;
    private String type;
    private String verificationStatus;
    private Long customerVerificationId;
    private List<FileDto> kycDocuments;
    private LocalDateTime customerCreatedAt;
    private LocalDateTime customerUpdatedAt;
    private LocalDateTime memberCreatedAt;
    private LocalDateTime memberUpdatedAt;
}
