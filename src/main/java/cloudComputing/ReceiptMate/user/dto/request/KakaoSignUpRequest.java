package cloudComputing.ReceiptMate.user.dto.request;

import cloudComputing.ReceiptMate.user.enumeration.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

@Getter
@AllArgsConstructor
public class KakaoSignUpRequest {
    private String kakaoToken;

    private String nickname;

    private Date birthday;
}