package u5w2d5.etm.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class ExceptionHandlerClass {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionMessage> handleIllegalArgumentException(IllegalArgumentException e) {
        ExceptionMessage exceptionMessage = new ExceptionMessage();
        exceptionMessage.setMessage(e.getMessage());
        exceptionMessage.setStatus("400");
        exceptionMessage.setError("Bad Request");
        return new ResponseEntity<>(exceptionMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { EntityNotFoundException.class })
    protected ResponseEntity<ExceptionMessage> handleEntityNotFoundException(EntityNotFoundException e) {

        ExceptionMessage exceptionMessage = new ExceptionMessage();
        exceptionMessage.setMessage(e.getMessage());
        exceptionMessage.setStatus("404");
        exceptionMessage.setError("Not Found");

        return new ResponseEntity<ExceptionMessage>(exceptionMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = EntityExistsException.class)
    protected ResponseEntity<ExceptionMessage> handleEntityExistsException(EntityExistsException e) {

        ExceptionMessage exceptionMessage = new ExceptionMessage();
        exceptionMessage.setMessage(e.getMessage());
        exceptionMessage.setStatus("409");
        exceptionMessage.setError("Conflict");

        return new ResponseEntity<ExceptionMessage>(exceptionMessage, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionMessage> handleConsraintViolationException(ConstraintViolationException e,
            HttpServletRequest request) {

        Map<String, String> errors = new HashMap<String, String>();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();

            if (fieldName.contains(".")) {
                fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
            }
            errors.put(fieldName, violation.getMessage());
        }

        ExceptionMessage exceptionMessage = new ExceptionMessage();
        exceptionMessage.setMessage("Validation failed");
        exceptionMessage.setStatus("400");
        exceptionMessage.setError(errors);

        return new ResponseEntity<ExceptionMessage>(exceptionMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    protected ResponseEntity<String> AccessDenied(AccessDeniedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    // @ExceptionHandler(value = JwtTokenMissingException.class)
    // protected ResponseEntity<String>
    // JwtTokenMissingException(JwtTokenMissingException ex) {
    // return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    // }

}
