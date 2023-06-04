package cloudComputing.ReceiptMate.user.exception;

import cloudComputing.ReceiptMate.base.exception.BaseException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DuplicateEmailException extends BaseException {
    private final static String message = "사용중인 계정입니다";

    private final static HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    public DuplicateEmailException() {
        super(message, httpStatus);
    }
}