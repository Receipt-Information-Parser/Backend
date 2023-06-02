package cloudComputing.ReceiptMate.entity;

import cloudComputing.ReceiptMate.enumerations.Authority;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Setter

public class Receipt {

    @Id
    @GeneratedValue
    private Long id;

    private Date createdDate;

    private String detailKey;

    @ElementCollection
    @CollectionTable(
            name = "info",
            joinColumns = {@JoinColumn(name = "receipt_id", referencedColumnName = "id")})
    @MapKeyColumn
    @Column(name = "info_name")
    private Map<String, String> info;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;
}

