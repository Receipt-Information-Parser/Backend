package cloudComputing.ReceiptMate.analysis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ByProductNameResponse {
    private List<String> names;
}