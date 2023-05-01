package cloudComputing.ReceiptMate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidTokenException extends BaseException {
    private final static String message = "유효하지 않은 사용자 토큰입니다";

    private final static HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    public InvalidTokenException() {
        super(message, httpStatus);
    }
}
