package cloudComputing.ReceiptMate.receipt.dto.response;

import cloudComputing.ReceiptMate.receipt.entity.Receipt;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptResponse {
    private Long id;

    private Date createdDate;

    private String detailKey;

    private Long ownerId;

    public ReceiptResponse(Receipt receipt) {
            id = receipt.getId();
            createdDate = receipt.getCreatedDate();
            detailKey = receipt.getDetailKey();
            ownerId = receipt.getOwner().getId();
    }
}