package com.graph.ql.graph_ql_demo.controller;

import com.graph.ql.graph_ql_demo.dto.PersonRequestDto;
import com.graph.ql.graph_ql_demo.dto.book.BookRequestDto;
import com.graph.ql.graph_ql_demo.dto.book.BookResponseDto;
import com.graph.ql.graph_ql_demo.service.BooksService;
import com.graph.ql.graph_ql_demo.service.PersonsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@GraphQlTest({BooksController.class, PersonsController.class})
class BooksControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private BooksService booksSv;

    @MockitoBean
    private PersonsService personsSv;

    @Test
    void batchBooks_shouldResolveBooksForEachPerson() {
        PersonRequestDto person1 = PersonRequestDto.builder().id(1L).firstname("Alice").build();
        PersonRequestDto person2 = PersonRequestDto.builder().id(2L).firstname("Bob").build();
        List<PersonRequestDto> persons = List.of(person1, person2);

        BookResponseDto book1 = new BookResponseDto();
        book1.setId(10L);

        given(personsSv.findAll()).willReturn(persons);
        given(booksSv.list(persons)).willReturn(Map.of(
                person1, List.of(book1),
                person2, List.of()
        ));

        String query = """
                query {
                    persons {
                        id
                        books {
                            id
                        }
                    }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("persons[0].books")
                .entityList(BookResponseDto.class)
                .hasSize(1)
                .path("persons[1].books")
                .entityList(BookResponseDto.class)
                .hasSize(0);

        verify(booksSv).list(any());
    }

    @Test
    void books_shouldReturnListOfBooks() {
        BookResponseDto book = new BookResponseDto();
        book.setId(1L);

        given(booksSv.listBooks()).willReturn(List.of(book));

        String query = """
                query {
                    books {
                        id
                    }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("books")
                .entityList(BookResponseDto.class)
                .hasSize(1);

        verify(booksSv).listBooks();
    }

    @Test
    void bookById_shouldReturnBookWhenFound() {
        Long id = 5L;
        BookResponseDto expected = new BookResponseDto();
        expected.setId(id);

        given(booksSv.bookById(id)).willReturn(expected);

        String query = """
                query($id: ID!) {
                    bookById(id: $id) {
                        id
                    }
                }
                """;

        graphQlTester.document(query)
                .variable("id", id)
                .execute()
                .path("bookById")
                .entity(BookResponseDto.class)
                .isEqualTo(expected);

        verify(booksSv).bookById(id);
    }

    @Test
    void createBook_shouldCreateAndReturnBook() {
        BookResponseDto expected = new BookResponseDto();
        expected.setId(100L);

        given(booksSv.createBook(any(BookRequestDto.class))).willReturn(expected);

        String mutation = """
            mutation($input: BookInput!) {
                createBook(input: $input) {
                    id
                }
            }
            """;

        graphQlTester.document(mutation)
                .variable("input", Map.of(
                        "title", "New Book Title",
                        "personId", 2L
                ))
                .execute()
                .path("createBook")
                .entity(BookResponseDto.class)
                .isEqualTo(expected);

        verify(booksSv).createBook(any(BookRequestDto.class));
    }

    @Test
    void updateBook_shouldUpdateAndReturnBook() {
        Long id = 10L;
        BookResponseDto expected = new BookResponseDto();
        expected.setId(id);

        given(booksSv.updateBook(any(BookRequestDto.class), eq(id))).willReturn(expected);

        String mutation = """
            mutation($id: ID!, $input: BookInput!) {
                updateBook(id: $id, input: $input) {
                    id
                }
            }
            """;

        graphQlTester.document(mutation)
                .variable("id", id)
                .variable("input", Map.of(
                        "title", "Updated Book Title",
                        "personId", 2L
                ))
                .execute()
                .path("updateBook")
                .entity(BookResponseDto.class)
                .isEqualTo(expected);

        verify(booksSv).updateBook(any(BookRequestDto.class), eq(id));
    }

    @Test
    void deleteBook_shouldReturnTrueWhenDeleted() {
        Long id = 12L;
        given(booksSv.delete(id)).willReturn(true);

        String mutation = """
                mutation($id: ID!) {
                    deleteBook(id: $id)
                }
                """;

        graphQlTester.document(mutation)
                .variable("id", id)
                .execute()
                .path("deleteBook")
                .entity(Boolean.class)
                .isEqualTo(true);

        verify(booksSv).delete(id);
    }

    @Test
    void deleteBook_shouldReturnFalseWhenNotDeleted() {
        Long id = 12L;
        given(booksSv.delete(id)).willReturn(false);

        String mutation = """
                mutation($id: ID!) {
                    deleteBook(id: $id)
                }
                """;

        graphQlTester.document(mutation)
                .variable("id", id)
                .execute()
                .path("deleteBook")
                .entity(Boolean.class)
                .isEqualTo(false);

        verify(booksSv).delete(id);
    }
}