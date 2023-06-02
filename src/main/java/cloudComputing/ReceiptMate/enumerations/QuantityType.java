package cloudComputing.ReceiptMate.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QuantityType {
    GRAM("그램"), MILLILITER("밀리리터"),
    KILOGRAM("킬로그램"), LITER("리터"),
    OUNCE("온스"), FLUIDOUNCE("플루이드온스"),
    GALLON("갤런"), COUNT("개"),
    NONE("없음");

    private final String value;
}