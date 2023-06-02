package cloudComputing.ReceiptMate.dto;

import cloudComputing.ReceiptMate.entity.ByPeriod;
import cloudComputing.ReceiptMate.entity.Receipt;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ByPeriodResponse {
    private Date date;

    private Integer amount;

    private Long analysisId;

    public ByPeriodResponse(ByPeriod byPeriod) {
        date = java.sql.Date.valueOf(LocalDate.of(byPeriod.getYear(), byPeriod.getMonth(), byPeriod.getDay()));
        amount = byPeriod.getAmount();
        analysisId = byPeriod.getAnalysis().getId();
    }
}