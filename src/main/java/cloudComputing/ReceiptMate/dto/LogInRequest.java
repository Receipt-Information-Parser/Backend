package cloudComputing.ReceiptMate.dto;


import lombok.Getter;

@Getter
public class LogInRequest {

    //유효성 검사는 용관이와 상의 후 결정

    private String email;
    private String password;
}
