package cloudComputing.ReceiptMate.dto;

import cloudComputing.ReceiptMate.entity.Analysis;
import cloudComputing.ReceiptMate.entity.ByPeriod;
import cloudComputing.ReceiptMate.entity.ByProduct;
import cloudComputing.ReceiptMate.enumerations.QuantityType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.Date;

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