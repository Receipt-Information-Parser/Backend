package cloudComputing.ReceiptMate.dto;

import cloudComputing.ReceiptMate.auth.TokenResponse;
import cloudComputing.ReceiptMate.enumerations.Gender;
import java.util.Date;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String email;

    private String name;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Date birthday;

    private Integer prepoint;

    private Integer accpoint;

    @Setter
    private TokenResponse tokenResponse;
}
