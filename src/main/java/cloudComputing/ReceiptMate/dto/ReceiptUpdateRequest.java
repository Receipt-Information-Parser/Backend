package cloudComputing.ReceiptMate.dto;

import cloudComputing.ReceiptMate.enumerations.Gender;
import java.util.Date;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ReceiptUpdateRequest {
    private Long id;

    private List<ByProductDTO> byProductDTOList;

    private Integer amount;

}
