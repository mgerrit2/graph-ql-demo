package com.graph.ql.graph_ql_demo.dto;

import com.graph.ql.graph_ql_demo.dto.book.BookRequestDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BookRequestDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldPassValidationForValidBookRequestDto() {
        BookRequestDto dto = BookRequestDto.builder()
                .personId(1L)
                .title("Clean Architecture")
                .isbn("978-0134494166")
                .build();

        Set<ConstraintViolation<BookRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();

    }

    @Test
    void shouldFailWhenPersonIdIsNull() {
        BookRequestDto dto = BookRequestDto.builder()
                .personId(null)
                .title("Clean Architecture")
                .build();

        Set<ConstraintViolation<BookRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .anyMatch(path -> path.toString().equals("personId"));
    }

    @Test
    void shouldFailWhenTitleIsBlank() {
        BookRequestDto dto = BookRequestDto.builder()
                .personId(1L)
                .title("   ")
                .build();

        Set<ConstraintViolation<BookRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .anyMatch(path -> path.toString().equals("title"));
    }

    @Test
    void shouldFailWhenTitleExceeds150Characters() {
        String longTitle = "a".repeat(151);
        BookRequestDto dto = BookRequestDto.builder()
                .personId(1L)
                .title(longTitle)
                .build();

        Set<ConstraintViolation<BookRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .anyMatch(path -> path.toString().equals("title"));
    }

    @Test
    void shouldFailWhenIsbnExceeds20Characters() {
        String longIsbn = "9".repeat(21);
        BookRequestDto dto = BookRequestDto.builder()
                .personId(1L)
                .title("Domain-Driven Design")
                .isbn(longIsbn)
                .build();

        Set<ConstraintViolation<BookRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .anyMatch(path -> path.toString().equals("isbn"));
    }
}