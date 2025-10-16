package com.netcompany.onboarding_exercise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class OnboardingExerciseApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnboardingExerciseApplication.class, args);
    }

}
