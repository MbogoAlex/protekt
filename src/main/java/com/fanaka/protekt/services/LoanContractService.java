package com.fanaka.protekt.services;

import com.fanaka.protekt.dto.LoanContractDto;
import com.fanaka.protekt.dto.PaginationDto;
import com.fanaka.protekt.entities.LoanContract;
import org.springframework.data.domain.Page;

public interface LoanContractService {
    LoanContract getLoanContractById(Long id);
    PaginationDto<LoanContractDto> getLoans(Long customerId, String status, Boolean insured, Integer page, Integer pageSize);
}
