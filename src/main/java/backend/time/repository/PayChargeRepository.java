package backend.time.repository;

import backend.time.model.pay.PayCharge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayChargeRepository extends JpaRepository<PayCharge, Long> {
    public Long countByImpuidContainsIgnoreCase(String impuid);
}

