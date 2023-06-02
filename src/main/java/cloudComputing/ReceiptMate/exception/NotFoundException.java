package cloudComputing.ReceiptMate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends BaseException {
    private final static String message = "알 수 없는 에러가 발생했습니다.";

    private final static HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public NotFoundException() {
        super(message, httpStatus);
    }
}
