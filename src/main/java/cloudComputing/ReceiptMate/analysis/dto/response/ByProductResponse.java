package cloudComputing.ReceiptMate.analysis.dto.response;

import cloudComputing.ReceiptMate.analysis.entity.ByProduct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ByProductResponse {
    private String name;

    private Integer amount;

    private Long analysisId;

    private Date date;

    public ByProductResponse(ByProduct byProduct) {
        date = java.sql.Date.valueOf(LocalDate.of(byProduct.getYear(), byProduct.getMonth(), byProduct.getDay()));
        name = byProduct.getName();
        amount = byProduct.getAmount();
        analysisId = byProduct.getAnalysis().getId();
    }
}