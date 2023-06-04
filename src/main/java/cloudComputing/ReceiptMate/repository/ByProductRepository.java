package cloudComputing.ReceiptMate.repository;

import cloudComputing.ReceiptMate.entity.Analysis;
import cloudComputing.ReceiptMate.entity.ByPeriod;
import cloudComputing.ReceiptMate.entity.ByProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ByProductRepository extends JpaRepository<ByProduct, Long> {
    List<ByProduct> findAllByYearAndName(Integer year, String name);

    List<ByProduct> findAllByYearAndMonthAndName(Integer year, Integer month, String name);

    List<ByProduct> findAllByYearAndMonthAndDayAndName(Integer year, Integer month, Integer day, String name);

    List<ByProduct> findAllByNameAndAnalysis(String name, Analysis analysis);

    @Query("select p.name from ByProduct p where p.analysis = ?1")
    List<String> getAllNamesByAnalysis(Analysis analysis);

    void deleteAllByOriginalReceiptId(Long originalReceiptId);

    Boolean existsByOriginalReceiptId(Long originalReceiptId);
}
