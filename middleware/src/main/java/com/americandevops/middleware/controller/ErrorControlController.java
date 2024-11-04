package com.americandevops.middleware.controller;

import com.americandevops.middleware.persistence.entity.ErrorControlEntity;
import com.americandevops.middleware.service.model.ErrorControlDto;
import com.americandevops.middleware.service.IErrorControlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/error")
@RequiredArgsConstructor
public class ErrorControlController {

    private final IErrorControlService _iErrorControlService;

    @Operation(description = "SEACH ERRORS CONTROL BY Id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/list/id", produces = "application/json")
    public ResponseEntity<String> findById(@RequestBody String encode) {
        return _iErrorControlService.findById(encode);
    }

    @Operation(description = "SEACH ERRORS CONTROL BY ERRORID")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/list/errorId", produces = "application/json")
    public ResponseEntity<String> findByErrorId(@RequestBody String encode) {
        return _iErrorControlService.findByErrorId(encode);
    }

    @Operation(description = "LIST ALL ERRORS CONTROL")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/list/all", produces = "application/json")
    public List<ErrorControlEntity> findAll() {
        return _iErrorControlService.findAll();
    }

    @Operation(description = "LIST ALL ERRORS CONTROL")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/list/paginated", produces = "application/json")
    public ResponseEntity<String> findAll(@RequestBody String encode) {
        return _iErrorControlService.findAll(encode);
    }

    @Operation(description = "INSERT A NEW ERROR CONTROL")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/addRecord", produces = "application/json")
    public ResponseEntity<String> addNew(@RequestBody String encode) {
        return _iErrorControlService.addNew(encode);
    }

    @Operation(description = "UPDATE A ERROR CONTROL")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/updateRecord", produces = "application/json")
    public ResponseEntity<String> updateData(@RequestBody String encode) {
        return _iErrorControlService.updateData(encode);
    }
}
