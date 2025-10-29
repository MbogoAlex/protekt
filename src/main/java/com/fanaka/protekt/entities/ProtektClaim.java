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
@Table(name = "protekt_claims")
public class ProtektClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "protekt_product_policy_id")
    private ProductPolicy productPolicy;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Member staff;

    @Column(columnDefinition = "TEXT")
    private String incident;

    @Column(name = "date_of_incident")
    private String dateOfIncident;

    @Column(name = "time_of_incident")
    private String timeOfIncident;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "protektClaim")
    private List<ProtektClaimDocument> documents; // evidence files like photo and certificates

}
