package cloudComputing.ReceiptMate.receipt.dto.response;

import cloudComputing.ReceiptMate.receipt.dto.response.ReceiptResponse;
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
