package com.fanaka.protekt.dao;

import com.fanaka.protekt.dto.LoanContractDto;
import com.fanaka.protekt.entities.LoanContract;
import org.springframework.data.domain.Page;

public interface LoanContractDao {
    LoanContract getLoanContractById(Long id);
    Page<LoanContractDto> getLoans(Long customerId, String status, Boolean insured, Integer page, Integer pageSize);
}
