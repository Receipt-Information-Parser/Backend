package cloudComputing.ReceiptMate.dto;

import lombok.Getter;

@Getter
public class ByPeriodRequest {
    private Integer year;

    private Integer month;

    private Integer day;
}
