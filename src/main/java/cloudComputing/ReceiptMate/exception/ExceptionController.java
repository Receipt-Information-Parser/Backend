package cloudComputing.ReceiptMate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalTime;

@ControllerAdvice
@Slf4j
public class ExceptionController {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> BadRequestException(final BaseException e) {
        log.warn("error", e);
        return ResponseEntity.badRequest().body(
            new ErrorResponse(LocalTime.now(), e.getMessage(), e.getHttpStatus().value(),
                e.getHttpStatus().name()
            ));
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleAll(final Exception ex) {
        log.info(ex.getClass().getName());
        log.error("error", ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
