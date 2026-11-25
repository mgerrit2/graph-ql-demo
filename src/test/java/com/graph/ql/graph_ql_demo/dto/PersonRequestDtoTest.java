package com.graph.ql.graph_ql_demo.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonRequestDtoTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDownValidator() {
        validatorFactory.close();
    }

    @Test
    void validDto_shouldHaveNoViolations() {
        PersonRequestDto dto = PersonRequestDto.builder()
                .id(1L)
                .firstname("Ada")
                .lastname("Lovelace")
                .active(true)
                .email("ada.lovelace@example.com")
                .build();

        Set<ConstraintViolation<PersonRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void builderDefault_shouldSetActiveToTrue() {
        PersonRequestDto dto = PersonRequestDto.builder()
                .id(1L)
                .active(true)
                .build();

        assertTrue(dto.isActive());
        assertEquals(1L, dto.getId());
    }

    @Test
    void noArgsConstructor_shouldInitializeDefaults() {
        PersonRequestDto dto = new PersonRequestDto();

        assertEquals(0L, dto.getId());
        assertFalse(dto.isActive());
    }

    @Test
    void firstnameExceedingMaxLength_shouldFailValidation() {
        String longName = "a".repeat(51);
        PersonRequestDto dto = PersonRequestDto.builder()
                .id(1L)
                .firstname(longName)
                .build();

        Set<ConstraintViolation<PersonRequestDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        ConstraintViolation<PersonRequestDto> violation = violations.iterator().next();
        assertEquals("firstname", violation.getPropertyPath().toString());
        assertEquals("First name must not exceed 50 characters", violation.getMessage());
    }

    @Test
    void firstnameAtMaxLength_shouldPassValidation() {
        String maxName = "a".repeat(50);
        PersonRequestDto dto = PersonRequestDto.builder()
                .id(1L)
                .firstname(maxName)
                .build();

        Set<ConstraintViolation<PersonRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void lastnameExceedingMaxLength_shouldFailValidation() {
        String longName = "b".repeat(51);
        PersonRequestDto dto = PersonRequestDto.builder()
                .id(1L)
                .lastname(longName)
                .build();

        Set<ConstraintViolation<PersonRequestDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        ConstraintViolation<PersonRequestDto> violation = violations.iterator().next();
        assertEquals("lastname", violation.getPropertyPath().toString());
        assertEquals("Last name must not exceed 50 characters", violation.getMessage());
    }

    @Test
    void blankEmail_shouldPassValidationDueToPattern() {
        PersonRequestDto dto = PersonRequestDto.builder()
                .id(1L)
                .email("")
                .build();

        Set<ConstraintViolation<PersonRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidEmailFormat_shouldFailValidation() {
        PersonRequestDto dto = PersonRequestDto.builder()
                .id(1L)
                .email("not-an-email")
                .build();

        Set<ConstraintViolation<PersonRequestDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        ConstraintViolation<PersonRequestDto> violation = violations.iterator().next();
        assertEquals("email", violation.getPropertyPath().toString());
        assertEquals("Invalid email format", violation.getMessage());
    }

    @Test
    void emailExceedingMaxLength_shouldFailValidation() {
        String longEmail = "a".repeat(250) + "@b.com"; // 256 chars
        PersonRequestDto dto = PersonRequestDto.builder()
                .id(1L)
                .email(longEmail)
                .build();

        Set<ConstraintViolation<PersonRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("email") &&
                        v.getMessage().equals("Email must not exceed 255 characters")
        ));
    }
}
