package com.michalswistowski.currency_service.service;

import com.michalswistowski.currency_service.client.ExchangeRatesClient;
import com.michalswistowski.currency_service.dto.CurrencyExchangeRatesResponse;
import com.michalswistowski.currency_service.dto.CurrencyRequest;
import com.michalswistowski.currency_service.dto.CurrencyResponse;
import com.michalswistowski.currency_service.entity.Currency;
import com.michalswistowski.currency_service.entity.ExchangeRate;
import com.michalswistowski.currency_service.exception.CurrencyNotActiveException;
import com.michalswistowski.currency_service.exception.EntityAlreadyExistsException;
import com.michalswistowski.currency_service.exception.NotFoundException;
import com.michalswistowski.currency_service.mapper.CurrencyMapper;
import com.michalswistowski.currency_service.repository.CurrencyRepository;
import com.michalswistowski.currency_service.repository.ExchangeRatesRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    public static final String EXCHANGE_RATES_CACHE = "rates";

    private final CurrencyRepository currencyRepository;
    private final ExchangeRatesRepository exchangeRatesRepository;
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

        Optional<Currency> foundCurrency = currencyRepository.findBySymbol(request.symbol());

        if (foundCurrency.isPresent()) {
            throw new EntityAlreadyExistsException("Currency with symbol %s already exists".formatted(request.symbol()));
        }

        Currency currency = currencyRepository.save(currencyMapper.mapCurrencyRequestToCurrency(request));
        return currencyMapper.mapCurrencyToCurrencyResponse(currency);
    }

    public CurrencyResponse setActiveCurrency(Long id, boolean value) {
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() ->
                new NotFoundException("Currency with id %d not found".formatted(id)));

        currency.setActive(value);
        currency = currencyRepository.save(currency);
        return currencyMapper.mapCurrencyToCurrencyResponse(currency);
    }

//    @CachePut(value = EXCHANGE_RATES_CACHE, key = "#symbol.toUpperCase()")
//    public CurrencyExchangeRatesResponse updateExchangeRates(String symbol, List<>) {
//        return requestAndFilterExchangeRates(symbol);
//    }

    @Cacheable(value = EXCHANGE_RATES_CACHE, key = "#symbol.toUpperCase()")
    public CurrencyExchangeRatesResponse getExchangeRates(String symbol) {

        List<CurrencyResponse> currencies = getAllActiveCurrencies();

        return getAndUpdateRate(symbol, currencies);
    }

//    private CurrencyExchangeRatesResponse requestAndFilterExchangeRates(String symbol) {
//
//        Currency foundCurrency = currencyRepository.findBySymbol(symbol).orElseThrow(() ->
//                new NotFoundException("Currency with symbol %s not found".formatted(symbol)));
//
//        if (!foundCurrency.isActive()) {
//            throw new CurrencyNotActiveException("Currency with symbol %s is inactive".formatted(symbol));
//        }
//
//        List<CurrencyResponse> currencies = getAllActiveCurrencies();
//
//        CurrencyExchangeRatesResponse allRates = exchangeRatesClient.getExchangeRates(symbol);
////      todo implement circuitbreaker
//        Map<String, String> filteredRates = new HashMap<>();
//
//        List<ExchangeRate> ratesToSave = new ArrayList<>();
//
//        currencies.forEach(currency -> {
//            String rateValue = allRates.data().rates().get(currency.symbol());
//
//
//
//            ExchangeRate rate = exchangeRatesRepository
//                    .findByBaseCurrency_IdAndTargetCurrency_Id(foundCurrency.getId(), currency.id()).orElse(new ExchangeRate());
//            rate.setBaseCurrency(foundCurrency);
//            rate.setRate(BigDecimal.valueOf(Double.parseDouble(rateValue)));
//            rate.setTargetCurrency(currencyMapper.mapCurrencyResponseToCurrency(currency));
//            ratesToSave.add(rate);
//        });
//
//        exchangeRatesRepository.saveAll(ratesToSave);
//
//        currencies.forEach(currency ->
//                filteredRates.put(currency.symbol().toUpperCase(), allRates.data().rates().get(currency.symbol().toUpperCase())));
//
//        return new CurrencyExchangeRatesResponse(
//                new CurrencyExchangeRatesResponse.Data(allRates.data().currency(), filteredRates));
//    }


    @CachePut(value = EXCHANGE_RATES_CACHE, key = "#symbol.toUpperCase()")
    public CurrencyExchangeRatesResponse getAndUpdateRate(String symbol, List<CurrencyResponse> activeCurrencies) {

        Currency foundCurrency = currencyRepository.findBySymbol(symbol).orElseThrow(() ->
                new NotFoundException("Currency with symbol %s not found".formatted(symbol)));

        if (!foundCurrency.isActive()) {
            throw new CurrencyNotActiveException("Currency with symbol %s is inactive".formatted(symbol));
        }

        CurrencyExchangeRatesResponse allRates = exchangeRatesClient.getExchangeRates(symbol);
//      todo implement circuitbreaker
        Map<String, String> filteredRates = new HashMap<>();

        List<ExchangeRate> ratesToSave = new ArrayList<>();

        activeCurrencies.forEach(currency -> {
            String rateValue = allRates.data().rates().get(currency.symbol());

            ExchangeRate rate = exchangeRatesRepository
                    .findByBaseCurrency_IdAndTargetCurrency_Id(foundCurrency.getId(), currency.id()).orElse(new ExchangeRate());
            rate.setBaseCurrency(foundCurrency);
            rate.setRate(BigDecimal.valueOf(Double.parseDouble(rateValue)));
            rate.setTargetCurrency(currencyMapper.mapCurrencyResponseToCurrency(currency));
            ratesToSave.add(rate);
        });

        exchangeRatesRepository.saveAll(ratesToSave);

        activeCurrencies.forEach(currency ->
                filteredRates.put(currency.symbol().toUpperCase(), allRates.data().rates().get(currency.symbol().toUpperCase())));

        return new CurrencyExchangeRatesResponse(
                new CurrencyExchangeRatesResponse.Data(allRates.data().currency(), filteredRates));
    }

    public void deleteCurrency(Long id) {
        if (!currencyRepository.existsById(id)) {
            throw new NotFoundException("Currency with id %d not found".formatted(id));
        }
        currencyRepository.deleteById(id);
    }
}
