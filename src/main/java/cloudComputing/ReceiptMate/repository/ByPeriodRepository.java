package cloudComputing.ReceiptMate.repository;

import cloudComputing.ReceiptMate.entity.ByPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ByPeriodRepository extends JpaRepository<ByPeriod, Long> {
    List<ByPeriod> findAllByYear(Integer year);

    List<ByPeriod> findAllByYearAndMonth(Integer year, Integer month);

    List<ByPeriod> findAllByYearAndMonthAndDate(Integer year, Integer month, Integer date);

    void deleteAllByOriginalReceiptId(Long originalReceiptId);

    Boolean existsByOriginalReceiptId(Long originalReceiptId);
}