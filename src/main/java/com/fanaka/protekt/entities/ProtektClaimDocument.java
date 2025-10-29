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
@Table(name = "protekt_claim_document")
public class ProtektClaimDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "protekt_claim_id")
    private ProtektClaim protektClaim;

    private Boolean verified;

    @Column(name = "document_type")
    private String documentType; // POLICE_REPORT, DEATH_CERTIFICATE etc,

    @Column(name = "other_document_type")
    private String otherDocumentType;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToOne
    @JoinColumn(name = "protekt_file_id")
    private ProtektFile protektFile;
}
