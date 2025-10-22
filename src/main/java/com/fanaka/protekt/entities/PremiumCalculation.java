package com.fanaka.protekt.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "protekt_premium_calculations")
public class PremiumCalculation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_policy_id")
    private ProductPolicy productPolicy;

    @Column(name = "base_amount")
    private String baseAmount;

    @Column(name = "premium_rate")
    private String premiumRate;

    @Column(name = "gross_premium")
    private String grossPremium;

    @Column(name = "tax_rate")
    private String taxRate;

    @Column(name = "tax_amount")
    private String taxAmount;

    @Column(name = "levy_rate")
    private String levyRate;

    @Column(name = "levy_amount")
    private String levyAmount;

    @Column(name = "admin_fee_rate")
    private String adminFeeRate;

    @Column(name = "admin_fee_amount")
    private String adminFeeAmount;

    @Column(name = "net_premium")
    private String netPremium;

    @Column(name = "total_premium")
    private String totalPremium;

    @Column(name = "calculation_method")
    private String calculationMethod;

    @Column(name = "provider_calculation_ref")
    private String providerCalculationRef;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;
}