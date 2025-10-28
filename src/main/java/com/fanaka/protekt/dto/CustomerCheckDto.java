package com.fanaka.protekt.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CustomerCheckDto {
    private Boolean isProtektCustomer;
    private Boolean isMember;
    private Boolean hasActiveLoan;
    private Boolean activeLoanIsInsured;
    private Long customerId;
    private Long memberId;
}