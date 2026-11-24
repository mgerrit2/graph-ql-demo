package com.graph.ql.graph_ql_demo.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDto {

    @NotNull
    private Long id;

    @NotBlank
    @Size(max = 150)
    private String title;

    @Size(max = 20)
    private String isbn;

    @Builder.Default
    private boolean active = true;
}