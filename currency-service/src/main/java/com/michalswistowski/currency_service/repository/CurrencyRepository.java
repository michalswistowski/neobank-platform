package com.michalswistowski.currency_service.repository;

import com.michalswistowski.currency_service.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    List<Currency> findByActiveTrue();
    List<Currency> findByActiveFalse();
    Optional<Currency> findBySymbol(String symbol);
}
