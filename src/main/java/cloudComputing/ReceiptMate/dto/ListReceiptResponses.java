package cloudComputing.ReceiptMate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ListReceiptResponses {
    @Setter
    List<ReceiptResponse> receipts;
}
