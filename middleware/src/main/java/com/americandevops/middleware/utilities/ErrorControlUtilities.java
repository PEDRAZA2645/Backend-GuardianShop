package com.americandevops.middleware.utilities;

import com.americandevops.middleware.service.implement.consultations.ErrorControlConsultations;
import com.americandevops.middleware.service.model.BasicResponseDto;
import com.americandevops.middleware.service.model.ErrorControlDto;
import com.americandevops.middleware.persistence.entity.ErrorControlEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Utility class for handling error responses and success responses.
 * Provides methods for searching error details and formatting responses.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ErrorControlUtilities {

    private final ErrorControlConsultations _errorControlConsultations;

    /**
     * Searches for an ErrorResponseDto by its ID.
     *
     * @param desiredId the ID of the error to search for
     * @return an Optional containing the ErrorResponseDto if found, otherwise empty
     */
    public Optional<ErrorControlDto> searchById(Long desiredId) {
        return _errorControlConsultations.findByErrorId(desiredId)
                .map(this::parse); // Convierte ErrorControlEntity a ErrorControlDto usando el método parse
    }

    /**
     * Converts an ErrorControlEntity to an ErrorControlDto.
     *
     * @param entity the ErrorControlEntity to convert
     * @return an ErrorControlDto containing the relevant information
     */
    private ErrorControlDto parse(ErrorControlEntity entity) {
        return new ErrorControlDto(
                entity.getId(),
                entity.getErrorId(),
                entity.getErrorName(),
                entity.getCreatedBy(),
                entity.getUpdatedBy()
        );
    }

    /**
     * Handles a successful operation by formatting a response with the provided object and error ID.
     * If the error ID is found, a response with status OK (200) is returned.
     * If the error ID is not found, it delegates to the handleGeneral method.
     *
     * @param object the object to include in the response
     * @param id the ID of the error to look up
     * @return a ResponseEntity containing the encoded response
     */
    public ResponseEntity<String> handleSuccess(Object object, Long id) {
        Optional<ErrorControlDto> errorResponseDto = searchById(id);
        if (errorResponseDto.isPresent()) {
            ErrorControlDto errorDto = errorResponseDto.get();
            BasicResponseDto<?> response = new BasicResponseDto<>(
                    errorDto.getErrorId(),
                    errorDto.getErrorName(),
                    object
            );
            String encodeResponse = EncoderUtilities.encodeResponse(response);
            return ResponseEntity.status(HttpStatus.OK).body(encodeResponse);
        } else {
            return handleGeneral(object, id);
        }
    }

    /**
     * Handles a general operation when the error ID is not found.
     * Returns a response with status BAD REQUEST (400) and includes a default error message.
     *
     * @param object the object to include in the response
     * @param id the ID of the error to look up
     * @return a ResponseEntity containing the encoded response
     */
    public ResponseEntity<String> handleGeneral(Object object, Long id) {
        Optional<ErrorControlDto> errorResponseDto = searchById(id);
        BasicResponseDto<?> responseDto = new BasicResponseDto<>(
                errorResponseDto.map(ErrorControlDto::getErrorId).orElse(-1L),
                errorResponseDto.map(ErrorControlDto::getErrorName).orElse("UNKNOWN ERROR"),
                object
        );
        String encodeResponse = EncoderUtilities.encodeResponse(responseDto);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(encodeResponse);
    }
}
