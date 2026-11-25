package com.graph.ql.graph_ql_demo.controller;

import com.graph.ql.graph_ql_demo.dto.PersonRequestDto;
import com.graph.ql.graph_ql_demo.service.PersonsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@GraphQlTest(PersonsController.class)
class PersonsControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private PersonsService personsSv;

    @Test
    void getPersons_shouldReturnListOfPersons() {
        PersonRequestDto p1 = new PersonRequestDto(/* populate fields */);
        PersonRequestDto p2 = new PersonRequestDto(/* populate fields */);
        given(personsSv.findAll()).willReturn(List.of(p1, p2));

        String query = """
                query {
                    persons {
                        id
                        firstname
                    }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("persons")
                .entityList(PersonRequestDto.class)
                .hasSize(2);

        verify(personsSv).findAll();
    }

    @Test
    void personById_shouldReturnPersonWhenFound() {
        Long id = 1L;
        PersonRequestDto person = new PersonRequestDto(/* populate fields */);
        given(personsSv.personById(id)).willReturn(person);

        String query = """
                query($id: ID!) {
                    personById(id: $id) {
                        id
                        firstname
                    }
                }
                """;

        graphQlTester.document(query)
                .variable("id", id)
                .execute()
                .path("personById")
                .entity(PersonRequestDto.class)
                .isEqualTo(person);

        verify(personsSv).personById(id);
    }

    @Test
    void createPerson_shouldCreateAndReturnPerson() {
        PersonRequestDto saved = new PersonRequestDto(/* populate fields */);
        given(personsSv.create(any(PersonRequestDto.class))).willReturn(saved);

        String mutation = """
                mutation($input: PersonInput!) {
                    createPerson(input: $input) {
                        id
                        firstname
                    }
                }
                """;

        graphQlTester.document(mutation)
                .variable("input", java.util.Map.of("firstname", "Alice"))
                .execute()
                .path("createPerson")
                .entity(PersonRequestDto.class)
                .isEqualTo(saved);

        verify(personsSv).create(any(PersonRequestDto.class));
    }

    @Test
    void updatePerson_shouldUpdateAndReturnPerson() {
        Long id = 1L;
        PersonRequestDto updated = new PersonRequestDto(/* populate fields */);
        given(personsSv.update(any(PersonRequestDto.class), eq(id))).willReturn(updated);

        String mutation = """
                mutation($id: ID!, $input: PersonInput!) {
                    updatePerson(id: $id, input: $input) {
                        id
                        firstname
                    }
                }
                """;

        graphQlTester.document(mutation)
                .variable("id", id)
                .variable("input", java.util.Map.of("firstname", "Alice Updated"))
                .execute()
                .path("updatePerson")
                .entity(PersonRequestDto.class)
                .isEqualTo(updated);

        verify(personsSv).update(any(PersonRequestDto.class), eq(id));
    }

    @Test
    void deletePerson_shouldReturnTrueWhenDeleted() {
        Long id = 1L;
        given(personsSv.delete(id)).willReturn(true);

        String mutation = """
                mutation($id: ID!) {
                    deletePerson(id: $id)
                }
                """;

        graphQlTester.document(mutation)
                .variable("id", id)
                .execute()
                .path("deletePerson")
                .entity(Boolean.class)
                .isEqualTo(true);

        verify(personsSv).delete(id);
    }
}
