package com.fanaka.protekt.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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

    private String providerProductId;

    private String name;

    private String description;

    @Column(name = "product_beneficiary_name")
    private String productBeneficiaryType; // FANAKA, CUSTOMER

    private Integer policyDuration;

    private String policyDurationType; // DAYS, WEEKS, MONTHS, YEARS

}
