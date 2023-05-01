package cloudComputing.ReceiptMate.dto;

import cloudComputing.ReceiptMate.enumerations.Gender;
import java.util.Date;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
public class SignUpRequest {

    private String email;

    @Setter
    private String password;

    private String name;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Date birthday;
}
