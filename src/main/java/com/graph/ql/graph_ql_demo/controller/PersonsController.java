package com.graph.ql.graph_ql_demo.controller;

import com.graph.ql.graph_ql_demo.dto.PersonRequestDto;
import com.graph.ql.graph_ql_demo.service.PersonsService;


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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PersonsController {

    private final static String PERSONS_STRING = "persons";
    private final static String PERSONS_LIST = "personsList";

    private final PersonsService personsSv;

    @QueryMapping(name = PERSONS_STRING)
    @Cacheable(value = PERSONS_LIST, key = "'all'")
    public List<PersonRequestDto> getPersons() {
        return personsSv.findAll();
    }

    @QueryMapping()
    @Cacheable(value = PERSONS_STRING, key = "#id")
    public PersonRequestDto personById(@Argument Long id) {
        return personsSv.personById(id);
    }

    @MutationMapping
    @CacheEvict(value = PERSONS_LIST, allEntries = true)
    public PersonRequestDto createPerson(@Argument("input") @Valid PersonRequestDto input) {
        return personsSv.create(input);
    }

    @MutationMapping
    @Caching(
            put = @CachePut(value = PERSONS_STRING, key = "#id"),
            evict = @CacheEvict(value = PERSONS_LIST, allEntries = true)
    )
    public PersonRequestDto updatePerson(
            @Argument Long id,
            @Argument("input") @Valid PersonRequestDto input) {

        return personsSv.update(input, id);
    }

    @MutationMapping
    @Caching(evict = {
            @CacheEvict(value = PERSONS_STRING, key = "#id"),
            @CacheEvict(value = PERSONS_LIST, allEntries = true)
    })
    public boolean deletePerson(@Argument Long id) {
        return personsSv.delete(id);
    }

}
