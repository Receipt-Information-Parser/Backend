package cloudComputing.ReceiptMate.dto;

import cloudComputing.ReceiptMate.entity.ByProduct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ListByProductResponse {
    @Setter
    List<ByProductResponse> byProducts;
}