package com.michalswistowski.currency_service.repository;

import com.michalswistowski.currency_service.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    List<Currency> findByActiveTrue();
    List<Currency> findByActiveFalse();
}
