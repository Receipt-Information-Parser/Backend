package cloudComputing.ReceiptMate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DuplicateNicknameException extends BaseException {
    private final static String message = "사용중인 닉네임입니다";

    private final static HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    public DuplicateNicknameException() {
        super(message, httpStatus);
    }
}