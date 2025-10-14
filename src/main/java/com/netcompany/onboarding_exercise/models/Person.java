package com.netcompany.onboarding_exercise.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "person")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "tax_number", nullable = false, unique = true, updatable = false)
    private String taxNumber;

    @Column(name = "tax_debt", nullable = false)
    private Double taxDebt = 0.0;

    @Column(name = "created_at", updatable = false)
    private LocalDate createAt = LocalDate.now();

    @Column(name = "updated_at")
    private LocalDate updatedAt = LocalDate.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDate.now();
    }
}

