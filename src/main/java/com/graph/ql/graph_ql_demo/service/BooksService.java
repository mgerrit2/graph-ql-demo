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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BooksService {

    private final BookRepository bookRepository;
    private final PersonRepository personRepository;
    private final BookMapper bookMapper;

    @Transactional(readOnly = true)
    public Map<PersonRequestDto, List<BookResponseDto>> list(List<PersonRequestDto> persons) {

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

    @Transactional(readOnly = true)
    public List<BookResponseDto> listBooks() {
        return bookMapper.toResponseDtoList(bookRepository.findAll());
    }

    @Transactional(readOnly = true)
    public BookResponseDto bookById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
    }

    @Transactional
    public BookResponseDto createBook(@Valid BookRequestDto input) {

        PersonEntity owner = personRepository.findById(input.getPersonId())
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + input.getPersonId()));

        BookEntity entity = bookMapper.toEntity(input);
        entity.setPerson(owner);
        BookEntity saved = bookRepository.save(entity);

        return bookMapper.toResponseDto(saved);
    }

    @Transactional
    public BookResponseDto updateBook(@Valid BookRequestDto input, Long id) {

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

    @Transactional
    public boolean delete(Long id) {
        BookEntity existing = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));

        bookRepository.delete(existing);
        return true;
    }

}
