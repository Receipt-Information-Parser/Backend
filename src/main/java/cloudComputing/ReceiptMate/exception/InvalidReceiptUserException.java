package cloudComputing.ReceiptMate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidReceiptUserException extends BaseException {
    private final static String message = "현재 유저의 영수증이 아닙니다";

    private final static HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    public InvalidReceiptUserException() {
        super(message, httpStatus);
    }
}
