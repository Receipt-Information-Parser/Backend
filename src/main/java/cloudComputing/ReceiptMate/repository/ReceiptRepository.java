package cloudComputing.ReceiptMate.repository;

import cloudComputing.ReceiptMate.entity.Receipt;
import cloudComputing.ReceiptMate.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    Optional<Receipt> findReceiptByOwner(Long ownerId);
}
