package cloudComputing.ReceiptMate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidObjectException extends BaseException {
    private final static String message = "존재하지 않는 파일입니다";

    private final static HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    public InvalidObjectException() {
        super(message, httpStatus);
    }
}
