package cloudComputing.ReceiptMate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class IllegalTokenException extends BaseException {
    private final static String message = "부적절한 형식의 토큰입니다";

    private final static HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    public IllegalTokenException() {
        super(message, httpStatus);
    }
}