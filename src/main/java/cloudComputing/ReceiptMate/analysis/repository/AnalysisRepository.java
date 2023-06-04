package cloudComputing.ReceiptMate.analysis.repository;

import cloudComputing.ReceiptMate.analysis.entity.Analysis;
import cloudComputing.ReceiptMate.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    Boolean existsByOwner(User owner);

    Optional<Analysis> findByOwner(User owner);
}
