package com.graph.ql.graph_ql_demo.controller;


import com.graph.ql.graph_ql_demo.dto.PersonRequestDto;
import com.graph.ql.graph_ql_demo.dto.book.BookRequestDto;
import com.graph.ql.graph_ql_demo.dto.book.BookResponseDto;
import com.graph.ql.graph_ql_demo.entity.BookEntity;
import com.graph.ql.graph_ql_demo.service.BooksService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

/**
 * GraphQL controller handling book queries, mutations, Redis cache updates,
 * and batched resolution of {@code Person.books}.
 */
@Log4j2
@Controller
@RequiredArgsConstructor
public class BooksController {

    private static final String BOOKS_FIELD = "books";

    private final BooksService booksSv;

    /**
     * Batch-loads books for all {@link PersonRequestDto} items in a single GraphQL execution
     * to avoid N+1 database queries when resolving {@code Person.books}.
     */
    @BatchMapping(typeName = "Person", field = BOOKS_FIELD)
    public Map<PersonRequestDto, List<BookResponseDto>> books(List<PersonRequestDto> persons) {
        return booksSv.list(persons);
    }

    /**
     * Returns all active books (filtered by {@code @SQLRestriction("active = true")} on {@link BookEntity}).
     */
    @QueryMapping
    public List<BookResponseDto> books() {
        return booksSv.listBooks();
    }

    /**
     * Returns a single book by ID, caching the response DTO in Redis under {@code books::<id>}.
     */
    @QueryMapping
    @Cacheable(value = BOOKS_FIELD, key = "#id")
    public BookResponseDto bookById(@Argument Long id) {
        return booksSv.bookById(id);
    }

    /**
     * Creates a new book linked to an existing person and caches the result under {@code books::<id>}.
     */
    @MutationMapping
    @CachePut(value = BOOKS_FIELD, key = "#result.id")
    public BookResponseDto createBook(@Argument("input") @Valid BookRequestDto input) {
        return booksSv.createBook(input);
    }

    /**
     * Updates an existing book (including reassigning its owner if changed) and refreshes its Redis cache entry.
     */
    @MutationMapping
    @CachePut(value = BOOKS_FIELD, key = "#id")
    public BookResponseDto updateBook(
            @Argument Long id,
            @Argument("input") @Valid BookRequestDto input) {
        return booksSv.updateBook(input,id);
    }

    /**
     * Soft-deletes a book via {@code @SQLDelete} on {@link BookEntity} and evicts {@code books::<id>} from Redis.
     */
    @MutationMapping
    @CacheEvict(value = BOOKS_FIELD, key = "#id")
    public boolean deleteBook(@Argument Long id) {
        return booksSv.delete(id);
    }
}
