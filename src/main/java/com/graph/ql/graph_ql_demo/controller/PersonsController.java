package com.graph.ql.graph_ql_demo.controller;

import com.graph.ql.graph_ql_demo.dto.PersonRequestDto;
import com.graph.ql.graph_ql_demo.mappers.PersonMapper;
import com.graph.ql.graph_ql_demo.repository.PersonRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Operation(summary = "get the list of persons",responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = PersonRequestDto.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "4XX", // Catches 400, 401, 403, 404, etc.
                    description = "Client Error (e.g., Bad Request, Unauthorized, Not Found, Validation)",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "default",
                    description = "Unexpected Error",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            )
    })
    @GetMapping(value = "" ,
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_PROBLEM_JSON_VALUE
            }
    )
    public ResponseEntity<List<PersonRequestDto>> getPersons(){

        var result = personRs.findAll();

        return ResponseEntity.ok(this.personMapper.toResponseDtoList(result));
    }

}
