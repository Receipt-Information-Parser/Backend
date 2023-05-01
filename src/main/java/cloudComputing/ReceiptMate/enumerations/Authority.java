package cloudComputing.ReceiptMate.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Authority {
    USER("USER"), ADMIN("ADMIN");

    private final String value;
}