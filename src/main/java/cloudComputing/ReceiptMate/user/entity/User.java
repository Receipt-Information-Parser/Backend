package cloudComputing.ReceiptMate.user.entity;

import cloudComputing.ReceiptMate.analysis.entity.Analysis;
import cloudComputing.ReceiptMate.receipt.entity.Receipt;
import cloudComputing.ReceiptMate.user.enumeration.Authority;
import cloudComputing.ReceiptMate.user.enumeration.Gender;

import java.util.*;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Setter

public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String email;

    @Setter
    private String password;

    private String name;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Date birthday;

    private String profileImage;

    @Enumerated(EnumType.STRING)
    private Authority authority = Authority.USER;

    @OneToMany(mappedBy = "owner")
    private List<Receipt> receipts = new ArrayList<>();

    @OneToOne(mappedBy = "owner")
    private Analysis analysis;

    private String kakaoID;

    private Boolean isKakao = false;
}
