package explore.with.me.statistics.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    private static final String ERROR = "error";

    private static final String MESSAGE = "message";

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<Map<String, String>> handleValidationException(HttpMessageNotReadableException ex) {
        log.debug("Validation error: {}", ex.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR, "Validation doesn't pass");
        errorResponse.put(MESSAGE, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}



