package com.fanaka.protekt.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "protekt_products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String provider;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider providerEntity;

    @Column(name = "provider_product_id")
    private String providerProductId;

    private String name;

    private String description;

    @Column(name = "product_beneficiary_name")
    private String productBeneficiaryType; // FANAKA, CUSTOMER

    @Column(name = "policy_duration")
    private Integer policyDuration;

    @Column(name = "policy_duration_type")
    private String policyDurationType; // DAYS, WEEKS, MONTHS, YEARS

    @Column(name = "premium_calculation_method")
    private String premiumCalculationMethod;

    @Column(name = "requires_complex_calculation")
    private Boolean requiresComplexCalculation;

    @OneToMany(mappedBy = "product")
    private List<ProductTerm> productTerms;

    @OneToMany(mappedBy = "product")
    private List<ProductProperty> productProperties;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

}
