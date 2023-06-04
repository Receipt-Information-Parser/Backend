package cloudComputing.ReceiptMate.receipt.repository;

import cloudComputing.ReceiptMate.receipt.entity.Receipt;
import cloudComputing.ReceiptMate.user.entity.User;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    List<Receipt> findAllByOwner(User owner);

    Optional<Receipt> findReceiptByDetailKey(String detailKey);

    Optional<Receipt> findByOwnerAndId(User owner, Long id);
}
