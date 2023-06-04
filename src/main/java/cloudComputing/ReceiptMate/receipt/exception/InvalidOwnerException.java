package cloudComputing.ReceiptMate.receipt.exception;

import cloudComputing.ReceiptMate.base.exception.BaseException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidOwnerException extends BaseException {
    private final static String message = "현재 유저가 영수증을 가지고 있지 않습니다.";

    private final static HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    public InvalidOwnerException() {
        super(message, httpStatus);
    }
}
