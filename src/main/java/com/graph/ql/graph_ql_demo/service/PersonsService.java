package com.graph.ql.graph_ql_demo.service;

import com.graph.ql.graph_ql_demo.dto.PersonRequestDto;
import com.graph.ql.graph_ql_demo.entity.PersonEntity;
import com.graph.ql.graph_ql_demo.mappers.PersonMapper;
import com.graph.ql.graph_ql_demo.repository.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PersonsService {

    private final PersonRepository personRs;
    private final PersonMapper personMapper;

    @Transactional(readOnly = true)
    public List<PersonRequestDto>  findAll() {
        return this.personMapper.toResponseDtoList(personRs.findAll());
    }

    @Transactional(readOnly = true)
    public PersonEntity findById(Long id) {
        return personRs.findById(id).orElse(null);
    }

    @Transactional
    public PersonRequestDto create(@Valid PersonRequestDto input) {

        PersonEntity entity = this.personMapper.toEntity(input);

        personRs.save(entity);

        return this.personMapper.toResponseDto(entity);
    }

    @Transactional
    public PersonRequestDto update(@Valid PersonRequestDto input, Long  id) {

        PersonEntity existing = personRs.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + id));

        this.personMapper.updateEntityFromDto(input, existing);
        PersonEntity saved = personRs.save(existing);

        return this.personMapper.toResponseDto(saved);
    }

    @Transactional
    public boolean delete(Long id) {

        PersonEntity existing = personRs.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + id));

        personRs.delete(existing);

        return true;
    }

    @Transactional(readOnly = true)
    public PersonRequestDto personById(Long id) {
        return this.personMapper.toResponseDto(personRs.findById(id).orElse(null));
    }
}
