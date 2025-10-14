package com.fanaka.protekt.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "protekt_product_policies")
public class ProductPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "protekt_product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "loan_amount")
    private String loanAmount;

    private String asset; // asset being insured

    @Column(name = "asset_value")
    private String assetValue;

    @Column(name = "premium_percentage")
    private String premiumPercentage;

    @Column(name = "premium_value")
    private String premiumValue;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "policy_start_date")
    private LocalDateTime policyStartDate;

    @Column(name = "policy_end_date")
    private LocalDateTime policyEndDate;

    @OneToOne
    @JoinColumn(name = "loan_contract_id")
    private LoanContract loanContract;
}
