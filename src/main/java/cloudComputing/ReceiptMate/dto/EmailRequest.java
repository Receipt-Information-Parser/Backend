package cloudComputing.ReceiptMate.dto;

import lombok.Getter;

import javax.validation.constraints.Email;

@Getter
public class EmailRequest {

    @Email(message = "올바른 형식의 이메일을 입력해주세요")
    private String email;
}
