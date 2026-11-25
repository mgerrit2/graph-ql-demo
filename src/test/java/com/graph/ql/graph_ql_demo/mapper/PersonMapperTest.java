package com.graph.ql.graph_ql_demo.mapper;


import com.graph.ql.graph_ql_demo.dto.PersonRequestDto;
import com.graph.ql.graph_ql_demo.entity.PersonEntity;
import com.graph.ql.graph_ql_demo.mappers.PersonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PersonMapperTest {

    private PersonMapper personMapper;

    @BeforeEach
    void setUp() {
        personMapper = Mappers.getMapper(PersonMapper.class);
    }

    @Test
    void toEntity_shouldMapFieldsAndIgnoreSystemAndAuditFields() {

        PersonRequestDto dto = PersonRequestDto.builder()
                .id(99L)
                .firstname("Alice")
                .lastname("Smith")
                .email("alice@example.com")
                .build();

        PersonEntity entity = personMapper.toEntity(dto);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertTrue(entity.getBooks() == null || entity.getBooks().isEmpty());;
        assertNull(entity.getCreatedBy().orElse(null));
        assertNull(entity.getUpdatedAt().orElse(null));
        assertNull(entity.getLastModifiedBy().orElse(null));
        assertEquals(entity.getVersion(),0L);
        assertEquals("Alice", entity.getFirstname().orElse(""));
        assertEquals("Smith", entity.getLastname().orElse(""));
        assertEquals("alice@example.com", entity.getEmail().orElse(""));
    }

    @Test
    void toEntity_whenNull_shouldReturnNull() {
        assertNull(personMapper.toEntity(null));
    }

    @Test
    void updateEntityFromDto_shouldUpdateMutableFieldsAndPreserveIgnoredFields() {
        OffsetDateTime createdAt = OffsetDateTime.parse("2025-01-01T10:00:00Z");
        OffsetDateTime updatedAt = OffsetDateTime.parse("2025-01-02T10:00:00Z");

        PersonEntity target = new PersonEntity();
        target.setId(1L);
        target.setFirstname("OldFirst");
        target.setLastname("OldLast");
        target.setEmail("old@example.com");
        target.setCreatedAt(createdAt);
        target.setCreatedBy("system");
        target.setUpdatedAt(updatedAt);
        target.setLastModifiedBy("admin");
        target.setVersion(3L);

        PersonRequestDto dto = PersonRequestDto.builder()
                .id(999L)
                .firstname("NewFirst")
                .lastname("NewLast")
                .email("new@example.com")
                .build();

        personMapper.updateEntityFromDto(dto, target);

        assertEquals(1L, target.getId());
        assertEquals("NewFirst", target.getFirstname().orElse(""));
        assertEquals("NewLast", target.getLastname().orElse(""));
        assertEquals("new@example.com", target.getEmail().orElse(""));
        assertEquals(createdAt, target.getCreatedAt().orElse(OffsetDateTime.MIN));
        assertEquals("system", target.getCreatedBy().orElse(""));
        assertEquals(updatedAt, target.getUpdatedAt().orElse(OffsetDateTime.MIN));
        assertEquals("admin", target.getLastModifiedBy().orElse(""));
        assertEquals(3L, target.getVersion());
    }

    @Test
    void toResponseDto_shouldMapEntityFields() {
        PersonEntity entity = new PersonEntity();
        entity.setId(1L);
        entity.setFirstname("Alice");
        entity.setLastname("Smith");
        entity.setEmail("alice@example.com");

        PersonRequestDto dto = personMapper.toResponseDto(entity);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Alice", dto.getFirstname());
        assertEquals("Smith", dto.getLastname());
        assertEquals("alice@example.com", dto.getEmail());
    }

    @Test
    void toResponseDto_whenNull_shouldReturnNull() {
        assertNull(personMapper.toResponseDto(null));
    }

    @Test
    void toResponseDtoList_shouldMapAllEntities() {
        PersonEntity e1 = new PersonEntity();
        e1.setId(1L);
        e1.setFirstname("Alice");

        PersonEntity e2 = new PersonEntity();
        e2.setId(2L);
        e2.setFirstname("Bob");

        List<PersonRequestDto> dtos = personMapper.toResponseDtoList(List.of(e1, e2));

        assertEquals(2, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals("Alice", dtos.get(0).getFirstname());
        assertEquals(2L, dtos.get(1).getId());
        assertEquals("Bob", dtos.get(1).getFirstname());
    }

    @Test
    void mapOptional_shouldUnwrapPresentOrReturnNullWhenEmpty() {
        assertEquals("value", personMapper.map(Optional.of("value")));
        assertNull(personMapper.map(Optional.empty()));
    }
}
