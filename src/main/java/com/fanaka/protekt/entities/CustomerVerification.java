package com.fanaka.protekt.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "protekt_customer_verifications")
public class CustomerVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "customerVerification")
    private List<KycDocument> kycDocuments;

    @Column(name = "verification_notes")
    private String verificationNotes;

    @Column(name = "verification_status")
    private String verificationStatus; // PENDING, IN_REVIEW, ON_HOLD, REJECTED, FLAGGED, VERIFIED

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "status_changed_at")
    private Timestamp statusChangedAt;

}
