package cloudComputing.ReceiptMate.analysis.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import cloudComputing.ReceiptMate.analysis.enumeration.QuantityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Setter

public class ByProduct {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private Integer year;

    private Integer month;

    private Integer day;

    private Float quantity;

    private QuantityType quantityType;

    private Integer amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id")
    private Analysis analysis;

    private Long originalReceiptId;
}