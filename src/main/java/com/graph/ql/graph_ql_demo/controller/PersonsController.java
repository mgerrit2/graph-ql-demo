package com.graph.ql.graph_ql_demo.controller;

import com.graph.ql.graph_ql_demo.dto.PersonRequestDto;
import com.graph.ql.graph_ql_demo.entity.PersonEntity;
import com.graph.ql.graph_ql_demo.mappers.PersonMapper;
import com.graph.ql.graph_ql_demo.repository.PersonRepository;
import jakarta.persistence.EntityNotFoundException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PersonsController {

    private final PersonRepository personRs;
    private final PersonMapper personMapper;

    @QueryMapping(name = "persons")
    @Transactional(readOnly = true)
    @Cacheable(value = "personsList", key = "'all'")
    public List<PersonRequestDto> getPersons() {
        var result = personRs.findAll();
        return this.personMapper.toResponseDtoList(result);
    }

    @QueryMapping()
    @Cacheable(value = "persons", key = "#id")
    public PersonRequestDto personById(@Argument Long id) {
        var result = personRs.findById(id);
        return this.personMapper.toResponseDto(result.get());
    }

    @MutationMapping
    @Transactional
    @CacheEvict(value = "personsList", allEntries = true)
    public PersonRequestDto createPerson(@Argument("input") @Valid PersonRequestDto input) {
        PersonEntity entity = this.personMapper.toEntity(input);
        PersonEntity saved = personRs.save(entity);
        return this.personMapper.toResponseDto(saved);
    }

    @MutationMapping
    @Transactional
    @Caching(
            put = @CachePut(value = "persons", key = "#id"),
            evict = @CacheEvict(value = "personsList", allEntries = true)
    )
    public PersonRequestDto updatePerson(
            @Argument Long id,
            @Argument("input") @Valid PersonRequestDto input) {
        PersonEntity existing = personRs.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + id));

        this.personMapper.updateEntityFromDto(input, existing);
        PersonEntity saved = personRs.save(existing);
        return this.personMapper.toResponseDto(saved);
    }

    @MutationMapping
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "persons", key = "#id"),
            @CacheEvict(value = "personsList", allEntries = true)
    })
    public boolean deletePerson(@Argument Long id) {
        PersonEntity existing = personRs.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + id));

        personRs.delete(existing);
        return true;
    }

}
