package cloudComputing.ReceiptMate.user.exception;

import cloudComputing.ReceiptMate.base.exception.BaseException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidKakaoInfoException extends BaseException {
    private final static String message = "카카오 회원가입 시의 정보가 일치하지 않습니다.";

    private final static HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public InvalidKakaoInfoException() {
        super(message, httpStatus);
    }
}