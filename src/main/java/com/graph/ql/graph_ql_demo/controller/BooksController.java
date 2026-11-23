package com.graph.ql.graph_ql_demo.controller;


import com.graph.ql.graph_ql_demo.dto.PersonRequestDto;
import com.graph.ql.graph_ql_demo.dto.book.BookRequestDto;
import com.graph.ql.graph_ql_demo.dto.book.BookResponseDto;
import com.graph.ql.graph_ql_demo.entity.BookEntity;
import com.graph.ql.graph_ql_demo.entity.PersonEntity;
import com.graph.ql.graph_ql_demo.mappers.BookMapper;
import com.graph.ql.graph_ql_demo.repository.BookRepository;
import com.graph.ql.graph_ql_demo.repository.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequiredArgsConstructor
public class BooksController {

    private final BookRepository bookRepository;
    private final PersonRepository personRepository;
    private final BookMapper bookMapper;

    @BatchMapping(typeName = "Person", field = "books")
    @Transactional(readOnly = true)
    public Map<PersonRequestDto, List<BookResponseDto>> books(List<PersonRequestDto> persons) {
        List<Long> personIds = persons.stream()
                .map(PersonRequestDto::getId)
                .toList();

        Map<Long, List<BookResponseDto>> booksByPersonId = bookRepository.findByPersonIdIn(personIds)
                .stream()
                .collect(Collectors.groupingBy(
                        book -> book.getPerson().getId(),
                        Collectors.mapping(bookMapper::toResponseDto, Collectors.toList())
                ));

        return persons.stream()
                .collect(Collectors.toMap(
                        person -> person,
                        person -> booksByPersonId.getOrDefault(person.getId(), List.of())
                ));
    }

    @QueryMapping
    @Transactional(readOnly = true)
    public BookResponseDto bookById(@Argument Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
    }

    @MutationMapping
    @Transactional
    public BookResponseDto createBook(@Argument("input") @Valid BookRequestDto input) {
        PersonEntity owner = personRepository.findById(input.getPersonId())
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + input.getPersonId()));

        BookEntity entity = bookMapper.toEntity(input);
        entity.setPerson(owner);
        BookEntity saved = bookRepository.save(entity);
        return bookMapper.toResponseDto(saved);
    }

    @MutationMapping
    @Transactional
    public BookResponseDto updateBook(
            @Argument Long id,
            @Argument("input") @Valid BookRequestDto input) {
        BookEntity existing = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));

        if (input.getPersonId() != null && input.getPersonId() != existing.getPerson().getId()) {
            PersonEntity newOwner = personRepository.findById(input.getPersonId())
                    .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + input.getPersonId()));
            existing.setPerson(newOwner);
        }

        bookMapper.updateEntityFromDto(input, existing);
        BookEntity saved = bookRepository.save(existing);
        return bookMapper.toResponseDto(saved);
    }

    @MutationMapping
    @Transactional
    public boolean deleteBook(@Argument Long id) {
        BookEntity existing = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));

        bookRepository.delete(existing);
        return true;
    }
}
