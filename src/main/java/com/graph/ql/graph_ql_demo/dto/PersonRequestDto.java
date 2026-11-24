package com.graph.ql.graph_ql_demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonRequestDto {

    @NotNull(message = "personId is required")
    private long id;

    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstname;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastname;

    @Builder.Default
    private boolean active = true;

    @Pattern(regexp = "^$|^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;
}