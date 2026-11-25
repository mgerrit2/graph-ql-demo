package com.graph.ql.graph_ql_demo.service;

import com.graph.ql.graph_ql_demo.dto.PersonRequestDto;
import com.graph.ql.graph_ql_demo.entity.PersonEntity;
import com.graph.ql.graph_ql_demo.mappers.PersonMapper;
import com.graph.ql.graph_ql_demo.repository.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonsServiceTest {

    @Mock
    private PersonRepository personRs;

    @Mock
    private PersonMapper personMapper;

    @InjectMocks
    private PersonsService personsService;

    @Test
    void findAll_shouldReturnMappedDtos() {
        PersonEntity entity = new PersonEntity();
        entity.setId(1L);
        List<PersonEntity> entities = List.of(entity);

        PersonRequestDto dto = PersonRequestDto.builder().id(1L).firstname("Alice").build();
        List<PersonRequestDto> expected = List.of(dto);

        when(personRs.findAll()).thenReturn(entities);
        when(personMapper.toResponseDtoList(entities)).thenReturn(expected);

        List<PersonRequestDto> result = personsService.findAll();

        assertEquals(expected, result);
        verify(personRs).findAll();
        verify(personMapper).toResponseDtoList(entities);
    }

    @Test
    void findById_whenFound_shouldReturnEntity() {
        Long id = 1L;
        PersonEntity entity = new PersonEntity();
        entity.setId(id);

        when(personRs.findById(id)).thenReturn(Optional.of(entity));

        PersonEntity result = personsService.findById(id);

        assertEquals(entity, result);
        verify(personRs).findById(id);
    }

    @Test
    void findById_whenNotFound_shouldReturnNull() {
        Long id = 99L;
        when(personRs.findById(id)).thenReturn(Optional.empty());

        PersonEntity result = personsService.findById(id);

        assertNull(result);
        verify(personRs).findById(id);
    }

    @Test
    void create_shouldMapSaveAndReturnDto() {
        PersonRequestDto input = PersonRequestDto.builder().firstname("Alice").build();
        PersonEntity entity = new PersonEntity();
        entity.setId(1L);
        PersonRequestDto expected = PersonRequestDto.builder().id(1L).firstname("Alice").build();

        when(personMapper.toEntity(input)).thenReturn(entity);
        when(personRs.save(entity)).thenReturn(entity);
        when(personMapper.toResponseDto(entity)).thenReturn(expected);

        PersonRequestDto result = personsService.create(input);

        assertEquals(expected, result);
        verify(personMapper).toEntity(input);
        verify(personRs).save(entity);
        verify(personMapper).toResponseDto(entity);
    }

    @Test
    void update_whenFound_shouldUpdateEntitySaveAndReturnDto() {
        Long id = 1L;
        PersonRequestDto input = PersonRequestDto.builder().firstname("Alice Updated").build();
        PersonEntity existing = new PersonEntity();
        existing.setId(id);
        PersonEntity saved = new PersonEntity();
        saved.setId(id);
        PersonRequestDto expected = PersonRequestDto.builder().id(id).firstname("Alice Updated").build();

        when(personRs.findById(id)).thenReturn(Optional.of(existing));
        when(personRs.save(existing)).thenReturn(saved);
        when(personMapper.toResponseDto(saved)).thenReturn(expected);

        PersonRequestDto result = personsService.update(input, id);

        assertEquals(expected, result);
        verify(personRs).findById(id);
        verify(personMapper).updateEntityFromDto(input, existing);
        verify(personRs).save(existing);
        verify(personMapper).toResponseDto(saved);
    }

    @Test
    void update_whenNotFound_shouldThrowEntityNotFoundException() {
        Long id = 99L;
        PersonRequestDto input = PersonRequestDto.builder().firstname("Nobody").build();

        when(personRs.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> personsService.update(input, id)
        );

        assertTrue(ex.getMessage().contains(String.valueOf(id)));
        verify(personRs).findById(id);
        verify(personMapper, never()).updateEntityFromDto(any(), any());
        verify(personRs, never()).save(any());
    }

    @Test
    void delete_whenFound_shouldDeleteAndReturnTrue() {
        Long id = 1L;
        PersonEntity existing = new PersonEntity();
        existing.setId(id);

        when(personRs.findById(id)).thenReturn(Optional.of(existing));

        boolean deleted = personsService.delete(id);

        assertTrue(deleted);
        verify(personRs).findById(id);
        verify(personRs).delete(existing);
    }

    @Test
    void delete_whenNotFound_shouldThrowEntityNotFoundException() {
        Long id = 99L;
        when(personRs.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> personsService.delete(id)
        );

        assertTrue(ex.getMessage().contains(String.valueOf(id)));
        verify(personRs).findById(id);
        verify(personRs, never()).delete(any());
    }

    @Test
    void personById_whenFound_shouldReturnMappedDto() {
        Long id = 1L;
        PersonEntity entity = new PersonEntity();
        entity.setId(id);
        PersonRequestDto expected = PersonRequestDto.builder().id(id).firstname("Alice").build();

        when(personRs.findById(id)).thenReturn(Optional.of(entity));
        when(personMapper.toResponseDto(entity)).thenReturn(expected);

        PersonRequestDto result = personsService.personById(id);

        assertEquals(expected, result);
        verify(personRs).findById(id);
        verify(personMapper).toResponseDto(entity);
    }

    @Test
    void personById_whenNotFound_shouldMapNullAndReturnNull() {
        Long id = 99L;
        when(personRs.findById(id)).thenReturn(Optional.empty());
        when(personMapper.toResponseDto(null)).thenReturn(null);

        PersonRequestDto result = personsService.personById(id);

        assertNull(result);
        verify(personRs).findById(id);
        verify(personMapper).toResponseDto(null);
    }
}