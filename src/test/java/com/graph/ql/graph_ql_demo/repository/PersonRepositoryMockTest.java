package com.graph.ql.graph_ql_demo.repository;

import com.graph.ql.graph_ql_demo.entity.PersonEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonRepositoryMockTest {

    @Mock
    private PersonRepository personRepository;

    @Test
    void findById_whenFound_shouldReturnPerson() {
        PersonEntity person = new PersonEntity();
        person.setId(1L);
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));

        Optional<PersonEntity> result = personRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(personRepository).findById(1L);
    }

    @Test
    void findById_whenNotFound_shouldReturnEmpty() {
        when(personRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<PersonEntity> result = personRepository.findById(99L);

        assertTrue(result.isEmpty());
        verify(personRepository).findById(99L);
    }

    @Test
    void findAll_shouldReturnAllMockedPersons() {
        PersonEntity person1 = new PersonEntity();
        person1.setId(1L);
        PersonEntity person2 = new PersonEntity();
        person2.setId(2L);
        when(personRepository.findAll()).thenReturn(List.of(person1, person2));

        List<PersonEntity> result = personRepository.findAll();

        assertEquals(2, result.size());
        verify(personRepository).findAll();
    }

    @Test
    void save_shouldReturnSavedPerson() {
        PersonEntity person = new PersonEntity();
        person.setId(1L);
        when(personRepository.save(any(PersonEntity.class))).thenReturn(person);

        PersonEntity saved = personRepository.save(person);

        assertEquals(1L, saved.getId());
        verify(personRepository).save(person);
    }

    @Test
    void existsById_shouldReturnTrueWhenPresent() {
        when(personRepository.existsById(1L)).thenReturn(true);

        assertTrue(personRepository.existsById(1L));
        assertFalse(personRepository.existsById(2L));
        verify(personRepository).existsById(1L);
        verify(personRepository).existsById(2L);
    }

    @Test
    void deleteById_shouldInvokeRepository() {
        personRepository.deleteById(1L);

        verify(personRepository).deleteById(1L);
    }
}