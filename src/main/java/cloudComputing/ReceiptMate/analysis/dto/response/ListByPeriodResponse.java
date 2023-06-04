package cloudComputing.ReceiptMate.analysis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ListByPeriodResponse {
    @Setter
    List<ByPeriodResponse> byPeriods;
}