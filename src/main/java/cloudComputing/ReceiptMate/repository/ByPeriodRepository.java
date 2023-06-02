package cloudComputing.ReceiptMate.repository;

import cloudComputing.ReceiptMate.entity.Analysis;
import cloudComputing.ReceiptMate.entity.ByPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ByPeriodRepository extends JpaRepository<ByPeriod, Long> {
    List<ByPeriod> findAllByYearAndAnalysis(Integer year, Analysis analysis);

    List<ByPeriod> findAllByYearAndMonthAndAnalysis(Integer year, Integer month, Analysis analysis);

    List<ByPeriod> findAllByYearAndMonthAndDayAndAnalysis(Integer year, Integer month, Integer day, Analysis analysis);

    void deleteAllByOriginalReceiptId(Long originalReceiptId);

    Boolean existsByOriginalReceiptId(Long originalReceiptId);
}