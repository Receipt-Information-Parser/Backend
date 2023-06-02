package cloudComputing.ReceiptMate.dto;

import cloudComputing.ReceiptMate.auth.TokenResponse;
import cloudComputing.ReceiptMate.entity.Analysis;
import cloudComputing.ReceiptMate.entity.Receipt;
import cloudComputing.ReceiptMate.enumerations.Authority;
import cloudComputing.ReceiptMate.enumerations.Gender;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
