package com.michalswistowski.currency_service;

import com.michalswistowski.currency_service.client.ExchangeRatesClient;
import com.michalswistowski.currency_service.dto.CurrencyResponse;
import com.michalswistowski.currency_service.entity.Currency;
import com.michalswistowski.currency_service.repository.CurrencyRepository;
import com.michalswistowski.currency_service.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class CurrencyServiceTest {

//    @BeforeAll
//    static void setup() {
//
//    }

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private ExchangeRatesClient exchangeRatesClient;

    @InjectMocks
    private CurrencyService currencyService;

    @BeforeEach
    void setup() {


    }

    @Test
    void shouldReturnAllActiveCurrencies() {

        Currency currency1 = new Currency();
        Currency currency2 = new Currency();

        LocalDateTime now = LocalDateTime.now();

        currency1.setId(1L);
        currency1.setActive(true);
        currency1.setCreatedAt(now);
        currency1.setSymbol("USD");

        currency2.setId(2L);
        currency2.setActive(true);
        currency2.setCreatedAt(now);
        currency2.setSymbol("PLN");

        when(currencyRepository.findByActiveTrue()).thenReturn(List.of(currency1, currency2));

        List<CurrencyResponse> result = currencyService.getAllActiveCurrencies();

        assertNotNull(result);
        assertEquals(2, result.size());

        CurrencyResponse currencyResp1 = result.get(0);
        CurrencyResponse currencyResp2 = result.get(1);

        assertAll(
                () -> assertEquals(1L, currencyResp1.id()),
                () -> assertTrue(currencyResp1.active()),
                () -> assertNotNull(currencyResp1.createdAt()),
                () -> assertEquals("USD", currencyResp1.symbol())
        );

        assertAll(
                () -> assertEquals(2L, currencyResp2.id()),
                () -> assertTrue(currencyResp2.active()),
                () -> assertNotNull(currencyResp2.createdAt()),
                () -> assertEquals("PLN", currencyResp2.symbol())
        );

        verify(currencyRepository, times(1)).findByActiveTrue();
    }

    @Test
    void shouldReturnAllInactiveCurrencies() {

        Currency currency1 = new Currency();
        Currency currency2 = new Currency();
        Currency currency3 = new Currency();

        LocalDateTime now = LocalDateTime.now();

        currency1.setId(1L);
        currency1.setActive(false);
        currency1.setCreatedAt(now);
        currency1.setSymbol("USD");

        currency2.setId(2L);
        currency2.setActive(false);
        currency2.setCreatedAt(now);
        currency2.setSymbol("PLN");

        currency3.setId(3L);
        currency3.setActive(false);
        currency3.setCreatedAt(now);
        currency3.setSymbol("EUR");

        when(currencyRepository.findByActiveFalse()).thenReturn(List.of(currency1, currency2, currency3));

        List<CurrencyResponse> result = currencyService.getAllInactiveCurrencies();

        assertNotNull(result);
        assertEquals(3, result.size());

        CurrencyResponse currencyResp1 = result.get(0);
        CurrencyResponse currencyResp2 = result.get(1);
        CurrencyResponse currencyResp3 = result.get(2);

        assertAll(
                () -> assertEquals(1L, currencyResp1.id()),
                () -> assertFalse(currencyResp1.active()),
                () -> assertNotNull(currencyResp1.createdAt()),
                () -> assertEquals("USD", currencyResp1.symbol())
        );

        assertAll(
                () -> assertEquals(2L, currencyResp2.id()),
                () -> assertFalse(currencyResp2.active()),
                () -> assertNotNull(currencyResp2.createdAt()),
                () -> assertEquals("PLN", currencyResp2.symbol())
        );

        assertAll(
                () -> assertEquals(3L, currencyResp3.id()),
                () -> assertFalse(currencyResp3.active()),
                () -> assertNotNull(currencyResp3.createdAt()),
                () -> assertEquals("EUR", currencyResp3.symbol())
        );

        verify(currencyRepository, times(1)).findByActiveFalse();
    }

    @Test
    void shouldReturnAllCurrencies() {

        Currency currency1 = new Currency();
        Currency currency2 = new Currency();
        Currency currency3 = new Currency();

        LocalDateTime now = LocalDateTime.now();

        currency1.setId(1L);
        currency1.setActive(false);
        currency1.setCreatedAt(now);
        currency1.setSymbol("USD");

        currency2.setId(2L);
        currency2.setActive(true);
        currency2.setCreatedAt(now);
        currency2.setSymbol("PLN");

        currency3.setId(3L);
        currency3.setActive(false);
        currency3.setCreatedAt(now);
        currency3.setSymbol("EUR");

        when(currencyRepository.findAll()).thenReturn(List.of(currency1, currency2, currency3));

        List<CurrencyResponse> result = currencyService.getAllCurrencies();

        assertNotNull(result);
        assertEquals(3, result.size());

        CurrencyResponse currencyResp1 = result.get(0);
        CurrencyResponse currencyResp2 = result.get(1);
        CurrencyResponse currencyResp3 = result.get(2);

        assertAll(
                () -> assertEquals(1L, currencyResp1.id()),
                () -> assertFalse(currencyResp1.active()),
                () -> assertNotNull(currencyResp1.createdAt()),
                () -> assertEquals("USD", currencyResp1.symbol())
        );

        assertAll(
                () -> assertEquals(2L, currencyResp2.id()),
                () -> assertTrue(currencyResp2.active()),
                () -> assertNotNull(currencyResp2.createdAt()),
                () -> assertEquals("PLN", currencyResp2.symbol())
        );

        assertAll(
                () -> assertEquals(3L, currencyResp3.id()),
                () -> assertFalse(currencyResp3.active()),
                () -> assertNotNull(currencyResp3.createdAt()),
                () -> assertEquals("EUR", currencyResp3.symbol())
        );

        verify(currencyRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyListOfActiveCurrencies() {
        when(currencyRepository.findByActiveTrue()).thenReturn(List.of());
        List<CurrencyResponse> result = currencyService.getAllActiveCurrencies();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(currencyRepository, times(1)).findByActiveTrue();
    }

    @Test
    void shouldReturnEmptyListOfInactiveCurrencies() {
        when(currencyRepository.findByActiveFalse()).thenReturn(List.of());
        List<CurrencyResponse> result = currencyService.getAllInactiveCurrencies();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(currencyRepository, times(1)).findByActiveFalse();
    }

    @Test
    void shouldReturnEmptyListOfAllCurrencies() {
        when(currencyRepository.findAll()).thenReturn(List.of());
        List<CurrencyResponse> result = currencyService.getAllCurrencies();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(currencyRepository, times(1)).findAll();
    }

//    @Test
//    void shouldReturnCurrencyOnSave() {
//
//        CurrencyRequest currencyRequest = new CurrencyRequest("PLN");
//
//        when(currencyRepository.save(cu))
//    }
}
