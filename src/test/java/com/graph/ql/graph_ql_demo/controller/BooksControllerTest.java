package com.graph.ql.graph_ql_demo.controller;

import com.graph.ql.graph_ql_demo.dto.PersonRequestDto;
import com.graph.ql.graph_ql_demo.dto.book.BookRequestDto;
import com.graph.ql.graph_ql_demo.dto.book.BookResponseDto;
import com.graph.ql.graph_ql_demo.service.BooksService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BooksControllerTest {

    @Mock
    private BooksService booksSv;

    @InjectMocks
    private BooksController booksController;

    @Test
    void batchBooksShouldDelegateToBooksServiceList() {
        PersonRequestDto person1 = PersonRequestDto.builder().id(1L).build();
        PersonRequestDto person2 = PersonRequestDto.builder().id(2L).build();
        List<PersonRequestDto> persons = List.of(person1, person2);

        BookResponseDto book1 = new BookResponseDto();
        book1.setId(10L);

        Map<PersonRequestDto, List<BookResponseDto>> expected = Map.of(
                person1, List.of(book1),
                person2, List.of()
        );
        when(booksSv.list(persons)).thenReturn(expected);

        Map<PersonRequestDto, List<BookResponseDto>> result = booksController.books(persons);

        assertEquals(expected, result);
        verify(booksSv).list(persons);
    }

    @Test
    void booksShouldDelegateToBooksServiceListBooks() {
        BookResponseDto book = new BookResponseDto();
        book.setId(1L);

        List<BookResponseDto> expected = List.of(book);

        when(booksSv.listBooks()).thenReturn(expected);

        List<BookResponseDto> result = booksController.books();

        assertEquals(expected, result);
        verify(booksSv).listBooks();
    }

    @Test
    void bookByIdShouldDelegateToBooksServiceBookById() {
        BookResponseDto expected = new BookResponseDto();
        expected.setId(5L);
        when(booksSv.bookById(5L)).thenReturn(expected);

        BookResponseDto result = booksController.bookById(5L);

        assertEquals(expected, result);
        verify(booksSv).bookById(5L);
    }

    @Test
    void createBookShouldDelegateToBooksServiceCreateBook() {
        BookRequestDto input = new BookRequestDto();
        input.setPersonId(2L);

        BookResponseDto expected = new BookResponseDto();
        expected.setId(100L);
        when(booksSv.createBook(input)).thenReturn(expected);

        BookResponseDto result = booksController.createBook(input);

        assertEquals(expected, result);
        verify(booksSv).createBook(input);
    }

    @Test
    void updateBookShouldDelegateWithInputThenIdToBooksServiceUpdateBook() {
        Long id = 10L;
        BookRequestDto input = new BookRequestDto();
        input.setPersonId(2L);

        BookResponseDto expected = new BookResponseDto();
        expected.setId(id);
        when(booksSv.updateBook(input, id)).thenReturn(expected);

        BookResponseDto result = booksController.updateBook(id, input);

        assertEquals(expected, result);
        verify(booksSv).updateBook(input, id);
    }

    @Test
    void deleteBookShouldDelegateToBooksServiceDelete() {
        when(booksSv.delete(12L)).thenReturn(true);

        boolean result = booksController.deleteBook(12L);

        assertTrue(result);
        verify(booksSv).delete(12L);
    }

    @Test
    void deleteBookWhenServiceReturnsFalse_shouldReturnFalse() {
        when(booksSv.delete(12L)).thenReturn(false);

        boolean result = booksController.deleteBook(12L);

        assertFalse(result);
        verify(booksSv).delete(12L);
    }
}