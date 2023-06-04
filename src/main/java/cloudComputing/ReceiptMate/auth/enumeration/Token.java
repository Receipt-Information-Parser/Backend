package cloudComputing.ReceiptMate.auth.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Token {
    ACCESS_TOKEN("ACCESS_TOKEN"), REFRESH_TOKEN("REFRESH_TOKEN");

    private final String value;
}