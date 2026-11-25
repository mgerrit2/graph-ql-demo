package com.graph.ql.graph_ql_demo.repository;

import com.graph.ql.graph_ql_demo.entity.BookEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookRepositoryMockTest {

    @Mock
    private BookRepository bookRepository;

    @Test
    void findByPersonIdIn_whenStubbed_shouldReturnMockedBooks() {
        BookEntity book1 = new BookEntity();
        book1.setId(1L);

        BookEntity book3 = new BookEntity();
        book3.setId(3L);

        List<Long> personIds = List.of(1L, 3L);
        when(bookRepository.findByPersonIdIn(personIds)).thenReturn(List.of(book1, book3));

        List<BookEntity> result = bookRepository.findByPersonIdIn(personIds);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(b -> b.getId() == 1L));
        assertTrue(result.stream().anyMatch(b -> b.getId() == 3L));
        verify(bookRepository).findByPersonIdIn(personIds);
    }

    @Test
    void findByPersonIdIn_whenNoMatches_shouldReturnEmptyList() {
        List<Long> personIds = List.of(99L);
        when(bookRepository.findByPersonIdIn(personIds)).thenReturn(Collections.emptyList());

        List<BookEntity> result = bookRepository.findByPersonIdIn(personIds);

        assertTrue(result.isEmpty());
        verify(bookRepository).findByPersonIdIn(personIds);
    }
}