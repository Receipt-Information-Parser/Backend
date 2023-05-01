package cloudComputing.ReceiptMate.repository;

import cloudComputing.ReceiptMate.entity.Analysis;
import cloudComputing.ReceiptMate.entity.ByPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
}
