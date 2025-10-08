package com.fanaka.protekt.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 10)
    private String title;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "other_name", length = 200)
    private String otherName;

    @Column(name = "gender", nullable = false, length = 40)
    private String gender;

    @Column(name = "avatar", length = 255, unique = true)
    private String avatar;

    @Column(name = "dob", nullable = false)
    private LocalDate dob;

    @Column(name = "country", nullable = false, length = 60, columnDefinition = "varchar(60) default 'ZAMBIA'")
    private String country = "ZAMBIA";

    @Column(name = "id_type", nullable = false, length = 60, columnDefinition = "varchar(60) default 'NATIONAL'")
    private String idType = "NATIONAL";

    @Column(name = "id_number", nullable = false, length = 60)
    private String idNumber;

    @Column(name = "staff")
    private Long staff;

    @Column(name = "reference", unique = true)
    private Long reference;

    @Column(name = "provider", nullable = false, length = 60)
    private String provider;

    @Column(name = "mobile", nullable = false, length = 25, unique = true)
    private String mobile;

    @Column(name = "type", nullable = false, length = 60, columnDefinition = "varchar(60) default 'CUSTOMER'")
    private String type = "CUSTOMER";

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "other", columnDefinition = "JSON")
    private String other;

    @Column(name = "status", nullable = false, length = 60, columnDefinition = "varchar(60) default 'PENDING'")
    private String status = "PENDING";

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private Timestamp timestamp;

    @Column(name = "branch")
    private Long branch;

    @Column(name = "updated")
    private Timestamp updated;

    @Column(name = "old_user_id")
    private Long oldUserId;
}
