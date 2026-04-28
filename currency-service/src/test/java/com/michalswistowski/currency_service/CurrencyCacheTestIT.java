package com.michalswistowski.currency_service;

import com.michalswistowski.currency_service.client.ExchangeRatesClient;
import com.michalswistowski.currency_service.dto.CurrencyExchangeRatesResponse;
import com.michalswistowski.currency_service.entity.Currency;
import com.michalswistowski.currency_service.repository.CurrencyRepository;
import com.michalswistowski.currency_service.repository.ExchangeRatesRepository;
import com.michalswistowski.currency_service.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@Testcontainers
public class CurrencyCacheTestIT {

    @Autowired
    private CurrencyService currencyService;

    @MockitoBean
    private CurrencyRepository currencyRepository;

    @MockitoBean
    private ExchangeRatesClient exchangeRatesClient;

    @Autowired
    @MockitoBean
    private ExchangeRatesRepository exchangeRatesRepository;

    @Autowired
    private CacheManager cacheManager;

    @Container
    @ServiceConnection
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:8.4.0"))
            .withExposedPorts(6379);

    @BeforeEach
    void clearCache() {
        cacheManager.getCache("rates");
    }

    @Test
    void shouldHitClientOnceAndCacheSubsequentCalls() {

        String symbol = "USD";
        LocalDateTime now = LocalDateTime.now();

        Currency currency1 = new Currency();
        Currency currency2 = new Currency();

        currency1.setId(1L);
        currency1.setActive(true);
        currency1.setCreatedAt(now);
        currency1.setSymbol("USD");

        currency2.setId(2L);
        currency2.setActive(true);
        currency2.setCreatedAt(now);
        currency2.setSymbol("PLN");

        Map<String, String> map = new LinkedHashMap<>();

        map.put("USD", "1.0");
        map.put("EUR", "0.8543420978402488");
        map.put("PLN", "3.6197295");
        map.put("JPY", "157.6677222233333333");
        map.put("GBPY", "0.746094");
        map.put("CAD", "1.3683363333333333");

        CurrencyExchangeRatesResponse mockResp = new CurrencyExchangeRatesResponse(
                new CurrencyExchangeRatesResponse.Data("USD", map));

        when(exchangeRatesClient.getExchangeRates(symbol)).thenReturn(mockResp);
        when(currencyRepository.findBySymbol(symbol)).thenReturn(Optional.of(currency1));
        when(currencyRepository.findByActiveTrue()).thenReturn(List.of(currency1, currency2));
        when(exchangeRatesRepository.findByBaseCurrency_IdAndTargetCurrency_Id(1L, 1L))
                .thenReturn(Optional.empty());
        when(exchangeRatesRepository.findByBaseCurrency_IdAndTargetCurrency_Id(1L, 2L))
                .thenReturn(Optional.empty());
        CurrencyExchangeRatesResponse response1 = currencyService.getExchangeRates(symbol);
        CurrencyExchangeRatesResponse response2 = currencyService.getExchangeRates(symbol);

        assertEquals(symbol, response1.data().currency());
        assertEquals(symbol, response2.data().currency());

        assertEquals(2, response1.data().rates().size());
        assertEquals(2, response2.data().rates().size());

        verify(exchangeRatesClient, times(1)).getExchangeRates(symbol);
        verify(currencyRepository, times(1)).findBySymbol(symbol);
        verify(currencyRepository, times(1)).findByActiveTrue();
        verify(exchangeRatesRepository, times(2)).findByBaseCurrency_IdAndTargetCurrency_Id(anyLong(), anyLong());
        verify(exchangeRatesRepository, times(1)).saveAll(anyCollection());
    }
}
