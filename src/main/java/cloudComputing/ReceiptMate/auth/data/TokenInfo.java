package cloudComputing.ReceiptMate.auth.data;

import cloudComputing.ReceiptMate.user.enumeration.Authority;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenInfo {
    private Long id;
    private String email;
    private Authority authority;
}
