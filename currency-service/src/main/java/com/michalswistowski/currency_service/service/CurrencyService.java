package com.michalswistowski.currency_service.service;

import com.michalswistowski.currency_service.client.ExchangeRatesClient;
import com.michalswistowski.currency_service.dto.CurrencyExchangeRatesResponse;
import com.michalswistowski.currency_service.dto.CurrencyRequest;
import com.michalswistowski.currency_service.dto.CurrencyResponse;
import com.michalswistowski.currency_service.entity.Currency;
import com.michalswistowski.currency_service.mapper.CurrencyMapper;
import com.michalswistowski.currency_service.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    public static final String EXCHANGE_RATES_CACHE = "rates";

    private final CurrencyRepository currencyRepository;
    private final ExchangeRatesClient exchangeRatesClient;
    private final CurrencyMapper currencyMapper = Mappers.getMapper(CurrencyMapper.class);

    public List<CurrencyResponse> getAllActiveCurrencies() {
        return currencyRepository.findByActiveTrue().stream()
                .map(currencyMapper::mapCurrencyToCurrencyResponse)
                .collect(Collectors.toList());
    }

    public List<CurrencyResponse> getAllInactiveCurrencies() {
        return currencyRepository.findByActiveFalse().stream()
                .map(currencyMapper::mapCurrencyToCurrencyResponse)
                .collect(Collectors.toList());
    }

    public List<CurrencyResponse> getAllCurrencies() {
        return currencyRepository.findAll().stream()
                .map(currencyMapper::mapCurrencyToCurrencyResponse)
                .collect(Collectors.toList());
    }

    public CurrencyResponse addCurrency(CurrencyRequest request) {
//        Currency currency = new Currency();
//        currency.setActive(false);
//        currency.setSymbol(request.symbol());
//        currency.setCreatedAt(LocalDateTime.now());

        Currency currency = currencyRepository.save(currencyMapper.mapCurrencyRequestToCurrency(request));

        return currencyMapper.mapCurrencyToCurrencyResponse(currency);
    }

    public CurrencyResponse setActiveCurrency(Long id, boolean value) {
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() ->
                new IllegalArgumentException("Currency with id %d not found".formatted(id)));

        currency.setActive(value);
        currency = currencyRepository.save(currency);
        return currencyMapper.mapCurrencyToCurrencyResponse(currency);
    }

    @CachePut(value = EXCHANGE_RATES_CACHE, key = "#symbol.toUpperCase()")
    public CurrencyExchangeRatesResponse updateExchangeRates(String symbol) {
        return requestAndFilterExchangeRates(symbol);
    }

    @Cacheable(value = EXCHANGE_RATES_CACHE, key = "#symbol.toUpperCase()")
    public CurrencyExchangeRatesResponse getExchangeRates(String symbol) {
        return requestAndFilterExchangeRates(symbol);
    }

    private CurrencyExchangeRatesResponse requestAndFilterExchangeRates(String symbol) {
        List<CurrencyResponse> currencies = getAllCurrencies();

        CurrencyExchangeRatesResponse allRates = exchangeRatesClient.getExchangeRates(symbol);
//      todo implement circuitbreaker
        Map<String, String> filteredRates = new HashMap<>();

        currencies.forEach(currency ->
                filteredRates.put(currency.symbol().toUpperCase(), allRates.data().rates().get(currency.symbol().toUpperCase())));

        return new CurrencyExchangeRatesResponse(
                new CurrencyExchangeRatesResponse.Data(allRates.data().currency(), filteredRates));
    }

    public void deleteCurrency(Long id) {
        currencyRepository.deleteById(id);
    }
}
