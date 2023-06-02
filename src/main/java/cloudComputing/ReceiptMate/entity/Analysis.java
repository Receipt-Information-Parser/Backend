package cloudComputing.ReceiptMate.entity;

import cloudComputing.ReceiptMate.enumerations.Authority;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

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

public class Analysis {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @OneToMany(mappedBy = "analysis")
    private List<ByPeriod> byPeriods = new ArrayList<>();

    @OneToMany(mappedBy = "analysis")
    private List<ByProduct> byProducts = new ArrayList<>();

}

