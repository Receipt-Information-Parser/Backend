package cloudComputing.ReceiptMate.picture.exception;

import cloudComputing.ReceiptMate.base.exception.BaseException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidProfileException extends BaseException {
    private final static String message = "현재 유저의 프로필이 아닙니다";

    private final static HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    public InvalidProfileException() {
        super(message, httpStatus);
    }
}
