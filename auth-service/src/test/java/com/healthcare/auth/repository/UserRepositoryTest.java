package com.healthcare.auth.repository;

import com.healthcare.auth.model.Role;
import com.healthcare.auth.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_shouldReturnUser() {

        User user = User.builder()
                .email("patient@example.com")
                .password("encoded-password")
                .role(Role.PATIENT)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        var result = userRepository.findByEmail("patient@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail())
                .isEqualTo("patient@example.com");
    }

    @Test
    void findByEmail_shouldReturnEmptyForUnknownEmail() {

        var result = userRepository.findByEmail("unknown@example.com");

        assertThat(result).isEmpty();
    }

    @Test
    void existsByEmail_shouldReturnTrueForExistingUser() {

        User user = User.builder()
                .email("doctor@example.com")
                .password("encoded-password")
                .role(Role.DOCTOR)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        boolean exists =
                userRepository.existsByEmail("doctor@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_shouldReturnFalseForUnknownEmail() {

        boolean exists =
                userRepository.existsByEmail("unknown@example.com");

        assertThat(exists).isFalse();
    }
}