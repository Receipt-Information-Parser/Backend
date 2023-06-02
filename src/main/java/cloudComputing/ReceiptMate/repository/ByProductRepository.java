package cloudComputing.ReceiptMate.repository;

import cloudComputing.ReceiptMate.entity.ByPeriod;
import cloudComputing.ReceiptMate.entity.ByProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ByProductRepository extends JpaRepository<ByProduct, Long> {
    List<ByPeriod> findAllByYearAndName(Integer year, String name);

    List<ByPeriod> findAllByYearAndMonthAndName(Integer year, Integer month, String name);

    List<ByPeriod> findAllByYearAndMonthAndDateAndName(Integer year, Integer month, Integer date, String name);

    void deleteAllByOriginalReceiptId(Long originalReceiptId);

    Boolean existsByOriginalReceiptId(Long originalReceiptId);
}
