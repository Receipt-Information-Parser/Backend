package cloudComputing.ReceiptMate.repository;

import cloudComputing.ReceiptMate.entity.ByPeriod;
import cloudComputing.ReceiptMate.entity.ByProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ByProductRepository extends JpaRepository<ByProduct, Long> {
}
