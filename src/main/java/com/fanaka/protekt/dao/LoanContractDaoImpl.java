package com.fanaka.protekt.dao;

import com.fanaka.protekt.dto.LoanContractDto;
import com.fanaka.protekt.dto.ProductPolicyDto;
import com.fanaka.protekt.entities.LoanContract;
import com.fanaka.protekt.entities.ProductPolicy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class LoanContractDaoImpl implements LoanContractDao {

    private final EntityManager entityManager;

    @Autowired
    public LoanContractDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public LoanContract getLoanContractById(Long id) {
        try {
            TypedQuery<LoanContract> query = entityManager.createQuery("from LoanContract where application = :id", LoanContract.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Page<LoanContractDto> getLoans(Long customerId, String status, Boolean insured, Integer page, Integer pageSize) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<LoanContract> criteriaQuery = criteriaBuilder.createQuery(LoanContract.class);
            Root<LoanContract> loanContractRoot = criteriaQuery.from(LoanContract.class);

            // Build predicates for dynamic filtering
            List<Predicate> predicates = new ArrayList<>();

            // Add customerId filter - use native query since no direct entity relationship exists
            if (customerId != null) {
                // Create a native query to find applications for this customer
                String nativeQuery = """
                    SELECT lc FROM LoanContract lc WHERE lc.application IN (
                        SELECT la.id FROM lms_loan_applications la
                        JOIN protekt_customers pc ON la.member = pc.member_id
                        WHERE pc.id = :customerId
                    )
                """;
                // For now, we'll handle this in a separate query since Criteria Builder is complex for this case
                // This is a temporary solution until proper entity relationships are established
                predicates.add(criteriaBuilder.in(loanContractRoot.get("application"))
                    .value(createCustomerApplicationSubquery(criteriaBuilder, criteriaQuery, customerId)));
            }

            // Add status filter
            if (status != null && !status.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(loanContractRoot.get("status"), status));
            }

            // Add insured filter - check if ProductPolicy exists for this loan contract
            if (insured != null) {
                Subquery<ProductPolicy> subquery = criteriaQuery.subquery(ProductPolicy.class);
                Root<ProductPolicy> productPolicyRoot = subquery.from(ProductPolicy.class);
                subquery.select(productPolicyRoot);
                subquery.where(
                    criteriaBuilder.equal(
                        productPolicyRoot.get("loanContract").get("application"),
                        loanContractRoot.get("application")
                    )
                );

                if (insured) {
                    // Loan must have insurance policy
                    predicates.add(criteriaBuilder.exists(subquery));
                } else {
                    // Loan must NOT have insurance policy
                    predicates.add(criteriaBuilder.not(criteriaBuilder.exists(subquery)));
                }
            }

            // Apply all predicates
            if (!predicates.isEmpty()) {
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
            }

            // Add ordering
            criteriaQuery.orderBy(criteriaBuilder.desc(loanContractRoot.get("application")));

            // Set default pagination values if not provided
            // Convert 1-based user pagination to 0-based internal pagination
            int pageNumber = (page != null && page > 0) ? page - 1 : 0;
            int size = (pageSize != null && pageSize > 0) ? pageSize : 10;

            // Create count query to get total number of records
            CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
            Root<LoanContract> countRoot = countQuery.from(LoanContract.class);
            countQuery.select(criteriaBuilder.count(countRoot));

            // Apply the same filters to count query
            if (!predicates.isEmpty()) {
                // Need to recreate predicates for count query
                List<Predicate> countPredicates = new ArrayList<>();

                if (customerId != null) {
                    countPredicates.add(criteriaBuilder.in(countRoot.get("application"))
                        .value(createCustomerApplicationSubquery(criteriaBuilder, countQuery, customerId)));
                }
                if (status != null && !status.trim().isEmpty()) {
                    countPredicates.add(criteriaBuilder.equal(countRoot.get("status"), status));
                }
                if (insured != null) {
                    Subquery<ProductPolicy> countSubquery = countQuery.subquery(ProductPolicy.class);
                    Root<ProductPolicy> countProductPolicyRoot = countSubquery.from(ProductPolicy.class);
                    countSubquery.select(countProductPolicyRoot);
                    countSubquery.where(
                        criteriaBuilder.equal(
                            countProductPolicyRoot.get("loanContract").get("application"),
                            countRoot.get("application")
                        )
                    );

                    if (insured) {
                        countPredicates.add(criteriaBuilder.exists(countSubquery));
                    } else {
                        countPredicates.add(criteriaBuilder.not(criteriaBuilder.exists(countSubquery)));
                    }
                }

                countQuery.where(criteriaBuilder.and(countPredicates.toArray(new Predicate[0])));
            }

            // Execute count query
            Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

            // Execute main query with pagination
            TypedQuery<LoanContract> query = entityManager.createQuery(criteriaQuery);
            query.setFirstResult(pageNumber * size);
            query.setMaxResults(size);
            List<LoanContract> loanContracts = query.getResultList();

            // Convert to DTOs
            List<LoanContractDto> loanContractDtos = new ArrayList<>();
            for (LoanContract lc : loanContracts) {
                LoanContractDto dto = convertToDto(lc);
                loanContractDtos.add(dto);
            }

            // Return properly paginated result
            return new PageImpl<>(loanContractDtos, PageRequest.of(pageNumber, size), totalElements);

        } catch (Exception e) {
            // Log error and return empty page
            e.printStackTrace();
            return new PageImpl<>(new ArrayList<>(), PageRequest.of(0, 1), 0);
        }
    }

    /**
     * Convert LoanContract entity to LoanContractDto
     * Maps old entity field names to new DTO field names for clarity
     */
    private LoanContractDto convertToDto(LoanContract loanContract) {
        return LoanContractDto.builder()
                .application(loanContract.getApplication())
                .productId(loanContract.getProductId())
                .approvedBy(loanContract.getApprovedBy())
                .signedBy(loanContract.getSignedBy())
                .branch(loanContract.getBranch())
                .justification(loanContract.getJustification())
                .results(loanContract.getResults())

                // Map principal to principalAmount for clarity
                .principalAmount(loanContract.getPrincipal() != null ? loanContract.getPrincipal().toString() : null)
                .principalPaidAmount(loanContract.getPrincipalPaid() != null ? loanContract.getPrincipalPaid().toString() : null)

                // Interest fields
                .interestType(loanContract.getInterestType())
                .interestValue(loanContract.getInterestValue() != null ? loanContract.getInterestValue().toString() : null)
                .interestAmount(loanContract.getInterestAmount() != null ? loanContract.getInterestAmount().toString() : null)
                .interestPaidAmount(loanContract.getInterestPaid() != null ? loanContract.getInterestPaid().toString() : null)

                // Processing fee fields
                .processingFeeType(loanContract.getProcessingFeeType())
                .processingFeeValue(loanContract.getProcessingFeeValue() != null ? loanContract.getProcessingFeeValue().toString() : null)
                .processingFeeAmount(loanContract.getProcessingFee() != null ? loanContract.getProcessingFee().toString() : null)
                .processingFeePaidAmount(loanContract.getProcessingPaid() != null ? loanContract.getProcessingPaid().toString() : null)

                // Insurance/Premium fields - map insuranceFee to premiumAmount for clarity
                .insuranceType(loanContract.getInsuranceType())
                .insuranceValue(loanContract.getInsuranceValue() != null ? loanContract.getInsuranceValue().toString() : null)
                .premiumAmount(loanContract.getInsuranceFee() != null ? loanContract.getInsuranceFee().toString() : null)
                .premiumPaidAmount(loanContract.getInsurancePaid() != null ? loanContract.getInsurancePaid().toString() : null)

                // Penalty fields
                .penaltyInterestRate(loanContract.getPenaltyInterestRate() != null ? loanContract.getPenaltyInterestRate().toString() : null)
                .penaltyFeeAmount(loanContract.getPenaltyFee() != null ? loanContract.getPenaltyFee().toString() : null)
                .penaltyPaidAmount(loanContract.getPenaltyPaid() != null ? loanContract.getPenaltyPaid().toString() : null)

                // Repayment terms
                .repaymentMode(loanContract.getRepaymentMode())
                .paymentDaysPerWeek(loanContract.getPaymentDaysPerWeek())
                .maturityDays(loanContract.getMaturityDays())
                .gracePeriodDays(loanContract.getGracePeriodDays())
                .installmentCount(loanContract.getInstallmentCount())
                .installmentAmount(loanContract.getInstallmentAmount() != null ? loanContract.getInstallmentAmount().toString() : null)

                // Critical money flow fields with clear naming
                .disbursedAmount(loanContract.getTotalDisbursed() != null ? loanContract.getTotalDisbursed().toString() : null)
                .totalPayableAmount(loanContract.getTotalPayable() != null ? loanContract.getTotalPayable().toString() : null)
                .totalPaidAmount(loanContract.getTotalPaid() != null ? loanContract.getTotalPaid().toString() : null)

                // Dates and status
                .disbursedAt(loanContract.getDisbursedAt() != null ? loanContract.getDisbursedAt().toLocalDateTime() : null)
                .maturityDate(loanContract.getMaturityDate() != null ? loanContract.getMaturityDate().toLocalDateTime() : null)
                .status(loanContract.getStatus())

                // Load and convert ProductPolicy if it exists
                .productPolicy(loadProductPolicyForLoan(loanContract.getApplication()))

                .build();
    }

    /**
     * Creates a subquery to find loan applications for a given customer ID
     * This is needed because there's no direct entity relationship between LoanContract and Customer
     */
    private Subquery<Long> createCustomerApplicationSubquery(CriteriaBuilder cb, CriteriaQuery<?> mainQuery, Long customerId) {
        // For now, use a simple approach - get the applications via native query
        // This is a temporary solution until proper entity relationships are established
        try {
            List<Long> applicationIds = entityManager.createNativeQuery(
                "SELECT la.id FROM lms_loan_applications la " +
                "JOIN protekt_customers pc ON la.member = pc.member_id " +
                "WHERE pc.id = ?", Long.class)
                .setParameter(1, customerId)
                .getResultList();

            // Create a simple subquery that returns these application IDs
            Subquery<Long> subquery = mainQuery.subquery(Long.class);
            Root<LoanContract> subRoot = subquery.from(LoanContract.class);
            subquery.select(subRoot.get("application"));

            if (!applicationIds.isEmpty()) {
                subquery.where(subRoot.get("application").in(applicationIds));
            } else {
                // No applications found for this customer - return empty subquery
                subquery.where(cb.equal(cb.literal(1), 0)); // Always false
            }

            return subquery;
        } catch (Exception e) {
            // If query fails, return empty subquery
            Subquery<Long> subquery = mainQuery.subquery(Long.class);
            Root<LoanContract> subRoot = subquery.from(LoanContract.class);
            subquery.select(subRoot.get("application"));
            subquery.where(cb.equal(cb.literal(1), 0)); // Always false
            return subquery;
        }
    }

    /**
     * Loads and converts ProductPolicy for a given loan contract application ID
     * Returns null if no policy exists for this loan
     */
    private ProductPolicyDto loadProductPolicyForLoan(Long applicationId) {
        try {
            // Query ProductPolicy by loan_contract_id (which maps to application ID)
            TypedQuery<ProductPolicy> query = entityManager.createQuery(
                "FROM ProductPolicy pp WHERE pp.loanContract.application = :applicationId",
                ProductPolicy.class
            );
            query.setParameter("applicationId", applicationId);

            List<ProductPolicy> policies = query.getResultList();

            if (policies.isEmpty()) {
                return null; // No policy for this loan
            }

            // Convert the first policy to DTO (should only be one per loan)
            ProductPolicy policy = policies.get(0);
            return convertProductPolicyToDto(policy);

        } catch (Exception e) {
            // Log error and return null if policy loading fails
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts ProductPolicy entity to ProductPolicyDto
     */
    private ProductPolicyDto convertProductPolicyToDto(ProductPolicy policy) {
        return ProductPolicyDto.builder()
            .policyId(policy.getId())
            .productName(policy.getProduct() != null ? policy.getProduct().getName() : null)
            .productId(policy.getProduct() != null ? policy.getProduct().getId().intValue() : null)
            .customerId(policy.getCustomer() != null ? policy.getCustomer().getId() : null)
            .customerName(getCustomerName(policy.getCustomer()))
            .loanId(policy.getLoanContract() != null ? policy.getLoanContract().getApplication() : null)
            .loanPrincipal(policy.getLoanContract() != null ? policy.getLoanContract().getPrincipal().toString() : null)
            .loanDisbursed(policy.getLoanContract() != null ? policy.getLoanContract().getTotalDisbursed().toString() : null)
            .premiumPercentage(policy.getPremiumPercentage() != null ? policy.getPremiumPercentage().toString() : null)
            .premiumValue(policy.getPremiumValue() != null ? policy.getPremiumValue().toString() : null)
            .premiumCalculations(null) // TODO: Load if needed
            .createdAt(policy.getCreatedAt() != null ? policy.getCreatedAt().toLocalDateTime() : null)
            .updatedAt(policy.getUpdatedAt() != null ? policy.getUpdatedAt().toLocalDateTime() : null)
            .policyStartDate(policy.getPolicyStartDate() != null ? policy.getPolicyStartDate() : null)
            .policyEndDate(policy.getPolicyEndDate() != null ? policy.getPolicyEndDate(): null)
            .build();
    }

    /**
     * Helper method to get customer name from Customer entity
     */
    private String getCustomerName(com.fanaka.protekt.entities.Customer customer) {
        if (customer == null || customer.getMember() == null) {
            return null;
        }

        com.fanaka.protekt.entities.Member member = customer.getMember();
        StringBuilder name = new StringBuilder();

        if (member.getFirstName() != null) {
            name.append(member.getFirstName());
        }
        if (member.getLastName() != null) {
            if (name.length() > 0) name.append(" ");
            name.append(member.getLastName());
        }

        return name.length() > 0 ? name.toString() : null;
    }
}
