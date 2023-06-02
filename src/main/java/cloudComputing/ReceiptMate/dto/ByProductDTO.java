package cloudComputing.ReceiptMate.dto;

import cloudComputing.ReceiptMate.enumerations.QuantityType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ByProductDTO {

    private String name;

    private Float quantity;

    private QuantityType quantityType;

    private Integer amount;

    @Override
    public String toString() {
        return name + " " + quantity.toString() + " " + quantityType.toString() + " " + amount.toString();
    }
}
