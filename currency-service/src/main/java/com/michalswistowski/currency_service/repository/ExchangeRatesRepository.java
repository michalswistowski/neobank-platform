package com.michalswistowski.currency_service.repository;

import com.michalswistowski.currency_service.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeRatesRepository extends JpaRepository<ExchangeRate, Long> {

    List<ExchangeRate> findByBaseCurrencySymbolAndTargetCurrencySymbol(String baseSymbol, String targetSymbol);

    List<ExchangeRate> findAllByBaseCurrencySymbol(String symbol);

    Optional<ExchangeRate> findByBaseCurrency_IdAndTargetCurrency_Id(Long baseCurrency_id, Long targetCurrency_id);
}
