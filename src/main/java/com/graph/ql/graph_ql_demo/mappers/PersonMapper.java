package com.graph.ql.graph_ql_demo.mappers;

import com.graph.ql.graph_ql_demo.dto.PersonRequestDto;
import com.graph.ql.graph_ql_demo.entity.PersonEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


import java.util.List;
import java.util.Optional;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        // Disables Lombok builder for targets to use standard setters/constructors
        builder = @Builder(disableBuilder = true)
)
public interface PersonMapper {

    // Maps Request DTO to a new Entity
    @Mapping(target = "books", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    PersonEntity toEntity(PersonRequestDto dto);

    // Updates an existing Entity from a Request DTO (ignores audit/system fields)
    @Mapping(target = "books", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromDto(PersonRequestDto dto, @MappingTarget PersonEntity entity);

    // Maps Entity to Response DTO
    // Note: If your entity uses Optional getters (e.g., getFirstname() returning Optional<String>),
    // MapStruct handles unboxing them automatically if standard fields match.
    PersonRequestDto toResponseDto(PersonEntity entity);

    // --- Add this line to map lists automatically ---
    List<PersonRequestDto> toResponseDtoList(List<PersonEntity> entities);

    // --- Helper mapper for Optional types ---
    default <T> T map(Optional<T> value) {
        return value.isPresent() ? value.orElse(null) : null;
    }

}