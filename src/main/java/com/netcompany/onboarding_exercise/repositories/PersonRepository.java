package com.netcompany.onboarding_exercise.repositories;

import com.netcompany.onboarding_exercise.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    /**
     * Find person by tax number
     *
     * @param taxNumber: Tax number of the person
     * @return Person or null
     */
    Optional<Person> findByTaxNumber(String taxNumber);

    /**
     * Check if the tax number exists
     *
     * @param taxNumber: Tax number of the person
     * @return boolean
     */
    boolean existsByTaxNumber(String taxNumber);
}
