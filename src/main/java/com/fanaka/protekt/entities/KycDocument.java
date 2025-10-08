package com.fanaka.protekt.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "protekt_kyc_documents")
public class KycDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "customer_verification_id")
    private CustomerVerification customerVerification;

    private Boolean verified;

    @Column(name = "document_type")
    private String documentType; // NATIONAL_ID, PASSPORT, BIRTH_CERTIFICATE, DRIVING_LICENSE, VOTER_CARD, HUDUMA_CARD, MILITARY_ID, ALIEN_ID, REFUGEE_ID,

    @Column(name = "other_document_type")
    private String otherDocumentType;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @ManyToOne
    @JoinColumn(name = "protekt_file_id")
    private ProtektFile protektFile;
}
