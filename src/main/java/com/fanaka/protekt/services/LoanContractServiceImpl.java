package com.fanaka.protekt.services;

import com.fanaka.protekt.dao.LoanContractDao;
import com.fanaka.protekt.dto.LoanContractDto;
import com.fanaka.protekt.dto.PaginationDto;
import com.fanaka.protekt.entities.LoanContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class LoanContractServiceImpl implements  LoanContractService {

    private final LoanContractDao loanContractDao;

    @Autowired
    public LoanContractServiceImpl(LoanContractDao loanContractDao) {
        this.loanContractDao = loanContractDao;
    }

    @Override
    public LoanContract getLoanContractById(Long id) {
        return loanContractDao.getLoanContractById(id);
    }

    @Override
    public PaginationDto<LoanContractDto> getLoans(Long customerId, String status, Boolean insured, Integer page, Integer pageSize) {
        try {
            // Call DAO to get paginated results
            Page<LoanContractDto> pageResult = loanContractDao.getLoans(customerId, status, insured, page, pageSize);

            // Convert Spring Data Page to PaginationDto
            // Convert back to 1-based page numbering for user response
            return new PaginationDto<>(
                pageResult.getContent(),           // List<LoanContractDto>
                pageResult.getNumber() + 1,       // Current page number (convert 0-based to 1-based)
                pageResult.getSize(),              // Page size
                pageResult.getTotalElements()      // Total number of elements
            );
        } catch (Exception e) {
            // Log error and return empty pagination result
            e.printStackTrace();
            return new PaginationDto<>(
                java.util.Collections.emptyList(),
                1,  // Return page 1 for error case (1-based pagination)
                10,
                0L
            );
        }
    }
}
