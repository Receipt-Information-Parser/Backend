package cloudComputing.ReceiptMate.repository;

import cloudComputing.ReceiptMate.entity.ByPeriod;
import cloudComputing.ReceiptMate.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ByPeriodRepository extends JpaRepository<ByPeriod, Long> {
}
