package cloudComputing.ReceiptMate.receipt.dto.request;

import cloudComputing.ReceiptMate.analysis.dto.ByProductDTO;

import java.util.List;

import lombok.Getter;

@Getter
public class ReceiptUpdateRequest {
    private Long id;

    private List<ByProductDTO> byProductDTOList;

    private Integer amount;

}
