package com.graph.ql.graph_ql_demo.service;

import com.graph.ql.graph_ql_demo.dto.PersonRequestDto;
import com.graph.ql.graph_ql_demo.dto.book.BookRequestDto;
import com.graph.ql.graph_ql_demo.dto.book.BookResponseDto;
import com.graph.ql.graph_ql_demo.entity.BookEntity;
import com.graph.ql.graph_ql_demo.entity.PersonEntity;
import com.graph.ql.graph_ql_demo.mappers.BookMapper;
import com.graph.ql.graph_ql_demo.repository.BookRepository;
import com.graph.ql.graph_ql_demo.repository.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BooksServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BooksService booksService;

    @Test
    void list_shouldGroupBooksByPersonAndReturnEmptyListWhenNone() {
        PersonRequestDto person1 = PersonRequestDto.builder().id(1L).firstname("Alice").build();
        PersonRequestDto person2 = PersonRequestDto.builder().id(2L).firstname("Bob").build();
        List<PersonRequestDto> persons = List.of(person1, person2);

        PersonEntity owner1 = new PersonEntity();
        owner1.setId(1L);

        BookEntity bookEntity1 = new BookEntity();
        bookEntity1.setId(10L);
        bookEntity1.setPerson(owner1);

        BookResponseDto bookDto1 = new BookResponseDto();
        bookDto1.setId(10L);

        when(bookRepository.findByPersonIdIn(List.of(1L, 2L))).thenReturn(List.of(bookEntity1));
        when(bookMapper.toResponseDto(bookEntity1)).thenReturn(bookDto1);

        Map<PersonRequestDto, List<BookResponseDto>> result = booksService.list(persons);

        assertEquals(Map.of(
                person1, List.of(bookDto1),
                person2, List.of()
        ), result);
        verify(bookRepository).findByPersonIdIn(List.of(1L, 2L));
        verify(bookMapper).toResponseDto(bookEntity1);
    }

    @Test
    void listBooks_shouldReturnMappedBooks() {
        BookEntity entity = new BookEntity();
        entity.setId(1L);
        List<BookEntity> entities = List.of(entity);

        BookResponseDto dto = new BookResponseDto();
        dto.setId(1L);
        List<BookResponseDto> expected = List.of(dto);

        when(bookRepository.findAll()).thenReturn(entities);
        when(bookMapper.toResponseDtoList(entities)).thenReturn(expected);

        List<BookResponseDto> result = booksService.listBooks();

        assertEquals(expected, result);
        verify(bookRepository).findAll();
        verify(bookMapper).toResponseDtoList(entities);
    }

    @Test
    void bookById_whenFound_shouldReturnMappedDto() {
        Long id = 5L;
        BookEntity entity = new BookEntity();
        entity.setId(id);

        BookResponseDto expected = new BookResponseDto();
        expected.setId(id);

        when(bookRepository.findById(id)).thenReturn(Optional.of(entity));
        when(bookMapper.toResponseDto(entity)).thenReturn(expected);

        BookResponseDto result = booksService.bookById(id);

        assertEquals(expected, result);
        verify(bookRepository).findById(id);
        verify(bookMapper).toResponseDto(entity);
    }

    @Test
    void bookById_whenNotFound_shouldThrowEntityNotFoundException() {
        Long id = 99L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> booksService.bookById(id)
        );

        assertTrue(ex.getMessage().contains(String.valueOf(id)));
        verify(bookRepository).findById(id);
        verify(bookMapper, never()).toResponseDto(any());
    }

    @Test
    void createBook_whenOwnerExists_shouldSetOwnerSaveAndReturnDto() {
        Long personId = 2L;
        BookRequestDto input = new BookRequestDto();
        input.setPersonId(personId);
        input.setTitle("Domain-Driven Design");

        PersonEntity owner = new PersonEntity();
        owner.setId(personId);

        BookEntity mappedEntity = new BookEntity();
        BookEntity savedEntity = new BookEntity();
        savedEntity.setId(100L);
        savedEntity.setPerson(owner);

        BookResponseDto expected = new BookResponseDto();
        expected.setId(100L);

        when(personRepository.findById(personId)).thenReturn(Optional.of(owner));
        when(bookMapper.toEntity(input)).thenReturn(mappedEntity);
        when(bookRepository.save(mappedEntity)).thenReturn(savedEntity);
        when(bookMapper.toResponseDto(savedEntity)).thenReturn(expected);

        BookResponseDto result = booksService.createBook(input);

        assertEquals(expected, result);
        assertEquals(owner, mappedEntity.getPerson());
        verify(personRepository).findById(personId);
        verify(bookMapper).toEntity(input);
        verify(bookRepository).save(mappedEntity);
        verify(bookMapper).toResponseDto(savedEntity);
    }

    @Test
    void createBook_whenOwnerNotFound_shouldThrowEntityNotFoundException() {
        Long personId = 99L;
        BookRequestDto input = new BookRequestDto();
        input.setPersonId(personId);

        when(personRepository.findById(personId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> booksService.createBook(input)
        );

        assertTrue(ex.getMessage().contains(String.valueOf(personId)));
        verify(personRepository).findById(personId);
        verify(bookMapper, never()).toEntity(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void updateBook_whenOwnerChangedAndExists_shouldReassignOwnerAndUpdate() {
        Long bookId = 10L;
        Long oldPersonId = 1L;
        Long newPersonId = 2L;

        BookRequestDto input = new BookRequestDto();
        input.setPersonId(newPersonId);
        input.setTitle("Updated Title");

        PersonEntity oldOwner = new PersonEntity();
        oldOwner.setId(oldPersonId);

        PersonEntity newOwner = new PersonEntity();
        newOwner.setId(newPersonId);

        BookEntity existing = new BookEntity();
        existing.setId(bookId);
        existing.setPerson(oldOwner);

        BookEntity saved = new BookEntity();
        saved.setId(bookId);
        saved.setPerson(newOwner);

        BookResponseDto expected = new BookResponseDto();
        expected.setId(bookId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existing));
        when(personRepository.findById(newPersonId)).thenReturn(Optional.of(newOwner));
        when(bookRepository.save(existing)).thenReturn(saved);
        when(bookMapper.toResponseDto(saved)).thenReturn(expected);

        BookResponseDto result = booksService.updateBook(input, bookId);

        assertEquals(expected, result);
        assertEquals(newOwner, existing.getPerson());
        verify(personRepository).findById(newPersonId);
        verify(bookMapper).updateEntityFromDto(input, existing);
        verify(bookRepository).save(existing);
        verify(bookMapper).toResponseDto(saved);
    }

    @Test
    void updateBook_whenOwnerUnchanged_shouldNotQueryPersonRepositoryForNewOwner() {
        Long bookId = 10L;
        Long personId = 1L;

        BookRequestDto input = new BookRequestDto();
        input.setPersonId(personId);
        input.setTitle("Updated Title");

        PersonEntity owner = new PersonEntity();
        owner.setId(personId);

        BookEntity existing = new BookEntity();
        existing.setId(bookId);
        existing.setPerson(owner);

        BookResponseDto expected = new BookResponseDto();
        expected.setId(bookId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existing));
        when(bookRepository.save(existing)).thenReturn(existing);
        when(bookMapper.toResponseDto(existing)).thenReturn(expected);

        BookResponseDto result = booksService.updateBook(input, bookId);

        assertEquals(expected, result);
        verify(personRepository, never()).findById(any());
        verify(bookMapper).updateEntityFromDto(input, existing);
        verify(bookRepository).save(existing);
    }

    @Test
    void updateBook_whenBookNotFound_shouldThrowEntityNotFoundException() {
        Long bookId = 99L;
        BookRequestDto input = new BookRequestDto();

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> booksService.updateBook(input, bookId)
        );

        assertTrue(ex.getMessage().contains(String.valueOf(bookId)));
        verify(personRepository, never()).findById(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void updateBook_whenNewOwnerNotFound_shouldThrowEntityNotFoundException() {
        Long bookId = 10L;
        Long oldPersonId = 1L;
        Long missingPersonId = 99L;

        BookRequestDto input = new BookRequestDto();
        input.setPersonId(missingPersonId);

        PersonEntity oldOwner = new PersonEntity();
        oldOwner.setId(oldPersonId);

        BookEntity existing = new BookEntity();
        existing.setId(bookId);
        existing.setPerson(oldOwner);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existing));
        when(personRepository.findById(missingPersonId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> booksService.updateBook(input, bookId)
        );

        assertTrue(ex.getMessage().contains(String.valueOf(missingPersonId)));
        verify(bookMapper, never()).updateEntityFromDto(any(), any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void delete_whenFound_shouldDeleteAndReturnTrue() {
        Long id = 12L;
        BookEntity existing = new BookEntity();
        existing.setId(id);

        when(bookRepository.findById(id)).thenReturn(Optional.of(existing));

        boolean deleted = booksService.delete(id);

        assertTrue(deleted);
        verify(bookRepository).findById(id);
        verify(bookRepository).delete(existing);
    }

    @Test
    void delete_whenNotFound_shouldThrowEntityNotFoundException() {
        Long id = 99L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> booksService.delete(id)
        );

        assertTrue(ex.getMessage().contains(String.valueOf(id)));
        verify(bookRepository).findById(id);
        verify(bookRepository, never()).delete(any());
    }
}