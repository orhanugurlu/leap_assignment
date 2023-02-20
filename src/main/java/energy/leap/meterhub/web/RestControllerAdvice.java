package energy.leap.meterhub.web;

import energy.leap.meterhub.service.exception.IllegalMeterBatchReadingContentException;
import energy.leap.meterhub.service.exception.IllegalMeterBatchReadingXmlException;
import energy.leap.meterhub.service.exception.MeterNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
@Slf4j
public class RestControllerAdvice {
    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<String> handleRunTimeException(RuntimeException e) {
        return error(INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler({IllegalMeterBatchReadingXmlException.class})
    public ResponseEntity<String> handleIllegalMeterBatchReadingXmlException(IllegalMeterBatchReadingXmlException e) {
        return error(INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler({IllegalMeterBatchReadingContentException.class})
    public ResponseEntity<String> handleIllegalMeterBatchReadingContentException(IllegalMeterBatchReadingContentException e){
        return error(INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler({MeterNotFoundException.class})
    public ResponseEntity<String> handleMeterNotFoundException(MeterNotFoundException e){
        return error(NOT_FOUND, e);
    }

    private ResponseEntity<String> error(HttpStatus status, Exception e) {
        log.error("Exception : ", e);
        return ResponseEntity.status(status).body(e.getMessage());
    }
}
