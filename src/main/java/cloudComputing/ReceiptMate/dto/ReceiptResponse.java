package cloudComputing.ReceiptMate.dto;

import cloudComputing.ReceiptMate.entity.Receipt;
import cloudComputing.ReceiptMate.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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