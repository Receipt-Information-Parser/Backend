package cloudComputing.ReceiptMate.analysis.dto.response;

import cloudComputing.ReceiptMate.analysis.entity.ByProduct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ByProductResponse {
    private String name;

    private Integer amount;

    private Long analysisId;

    public ByProductResponse(ByProduct byProduct) {
        name = byProduct.getName();
        amount = byProduct.getAmount();
        analysisId = byProduct.getAnalysis().getId();
    }
}