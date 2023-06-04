package cloudComputing.ReceiptMate.user.dto.response;

import cloudComputing.ReceiptMate.auth.dto.response.TokenResponse;
import cloudComputing.ReceiptMate.user.enumeration.Gender;

import java.util.Date;
import javax.persistence.*;

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

    @Setter
    private TokenResponse tokenResponse;

    private String profileImage;

}
