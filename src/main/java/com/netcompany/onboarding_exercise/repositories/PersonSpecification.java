package com.netcompany.onboarding_exercise.repositories;

import com.netcompany.onboarding_exercise.models.Person;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class PersonSpecification {
    public static Specification<Person> buildSearchAndFilterSpecification(
            String searchField,
            String searchValue,
            Integer minAge,
            Integer maxAge,
            Double minTaxDebt,
            Double maxTaxDebt
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Searching
            if (searchField != null && !searchField.trim().isEmpty() && searchValue != null &&
                    !searchValue.trim().isEmpty()) {
                String searchValueLower = searchValue.toLowerCase().trim();

                switch (searchField.toLowerCase()) {
                    case "name":
                        // Search in both firstName and lastName
                        Predicate firstNamePredicate =
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(root.get("firstName")),
                                        "%" + searchValueLower + "%"
                                );
                        Predicate lastNamePredicate = criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("lastName")),
                                "%" + searchValueLower + "%"
                        );
                        predicates.add(criteriaBuilder.or(firstNamePredicate, lastNamePredicate));
                        break;

                    case "taxnumber":
                        predicates.add(criteriaBuilder.like(root.get("taxNumber"), "%" + searchValue.trim() + "%"));
                        break;
                }
            }

            // Age filtering
            if (minAge != null) {
                LocalDate maxBirthDate = LocalDate.now().minusYears(minAge);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dateOfBirth"), maxBirthDate));
            }
            if (maxAge != null) {
                LocalDate minBirthDate = LocalDate.now().minusYears(maxAge + 1).plusDays(1);
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateOfBirth"), minBirthDate));
            }

            // Tax debt filtering
            if (minTaxDebt != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("taxDebt"), minTaxDebt));
            }
            if (maxTaxDebt != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("taxDebt"), maxTaxDebt));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Person> findPersonsWithMiAndOlderThan30() {
        return (root, query, criteriaBuilder) -> {
            // 1. Name starts with "Mi" (case-insensitive)
            Predicate firstNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "mi%");
            Predicate lastNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "mi%");
            Predicate namePredicate = criteriaBuilder.or(firstNamePredicate, lastNamePredicate);

            // 2. Older than 30 years (born before 30 years ago)
            LocalDate cutoffDate = LocalDate.now().minusYears(30);
            Predicate agePredicate = criteriaBuilder.lessThan(root.get("dateOfBirth"), cutoffDate);

            // Combine both conditions
            return criteriaBuilder.and(namePredicate, agePredicate);
        };
    }
}
