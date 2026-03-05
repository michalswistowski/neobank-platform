package com.michalswistowski.currency_service;

import com.michalswistowski.currency_service.client.ExchangeRatesClient;
import com.michalswistowski.currency_service.dto.CurrencyExchangeRatesResponse;
import com.michalswistowski.currency_service.dto.CurrencyRequest;
import com.michalswistowski.currency_service.dto.CurrencyResponse;
import com.michalswistowski.currency_service.entity.Currency;
import com.michalswistowski.currency_service.exception.CurrencyNotActiveException;
import com.michalswistowski.currency_service.exception.EntityAlreadyExistsException;
import com.michalswistowski.currency_service.exception.NotFoundException;
import com.michalswistowski.currency_service.mapper.CurrencyMapper;
import com.michalswistowski.currency_service.repository.CurrencyRepository;
import com.michalswistowski.currency_service.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    private final CurrencyMapper currencyMapper = Mappers.getMapper(CurrencyMapper.class);

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

    @Test
    void shouldReturnCurrencyOnSave() {

        LocalDateTime now = LocalDateTime.now();

        Currency currency = new Currency();

        currency.setId(1L);
        currency.setSymbol("PLN");
        currency.setActive(false);
        currency.setCreatedAt(now);


        CurrencyRequest currencyRequest = new CurrencyRequest("PLN");

        when(currencyRepository.save(any(Currency.class))).thenReturn(currency);

        CurrencyResponse result = currencyService.addCurrency(currencyRequest);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("PLN", result.symbol());
        assertFalse(result.active());

        ArgumentCaptor<Currency> captor = ArgumentCaptor.forClass(Currency.class);

        verify(currencyRepository).save(captor.capture());

        Currency captured = captor.getValue();

        assertNotNull(captured);
        assertNull(captured.getId());
        assertEquals("PLN", captured.getSymbol());
        assertFalse(captured.isActive());
    }

    @Test
    void shouldThrowExceptionWhenSymbolExists() {

        LocalDateTime now = LocalDateTime.now();

        Currency currency = new Currency();

        currency.setId(1L);
        currency.setSymbol("PLN");
        currency.setActive(false);
        currency.setCreatedAt(now);

        when(currencyRepository.findBySymbol("PLN")).thenReturn(Optional.of(currency));

        CurrencyRequest currencyRequest = new CurrencyRequest("PLN");

        assertThrows(EntityAlreadyExistsException.class, () -> {
            currencyService.addCurrency(currencyRequest);
        });

        verify(currencyRepository).findBySymbol("PLN");
        verify(currencyRepository, times(0)).save(currency);
    }

    @Test
    void shouldSetCurrencyActive() {

        LocalDateTime now = LocalDateTime.now();

        Currency currency = new Currency();

        currency.setId(1L);
        currency.setSymbol("PLN");
        currency.setActive(false);
        currency.setCreatedAt(now);

        boolean value = true;

        when(currencyRepository.findById(1L)).thenReturn(Optional.of(currency));

        currency.setActive(value);

        when(currencyRepository.save(currency)).thenReturn(currency);

        CurrencyResponse result = currencyService.setActiveCurrency(1L, value);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("PLN", result.symbol());
        assertTrue(result.active());

        verify(currencyRepository, times(1)).findById(1L);
        verify(currencyRepository).save(currency);
    }

    @Test
    void shouldSetCurrencyInactive() {

        LocalDateTime now = LocalDateTime.now();

        Currency currency = new Currency();

        currency.setId(1L);
        currency.setSymbol("PLN");
        currency.setActive(true);
        currency.setCreatedAt(now);

        boolean value = false;

        when(currencyRepository.findById(1L)).thenReturn(Optional.of(currency));

        currency.setActive(value);

        when(currencyRepository.save(currency)).thenReturn(currency);

        CurrencyResponse result = currencyService.setActiveCurrency(1L, value);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("PLN", result.symbol());
        assertFalse(result.active());

        verify(currencyRepository, times(1)).findById(1L);
        verify(currencyRepository).save(currency);
    }

    @Test
    void shouldThrowExceptionSetCurrencyActive() {

        LocalDateTime now = LocalDateTime.now();

        Currency currency = new Currency();

        currency.setId(1L);
        currency.setSymbol("PLN");
        currency.setActive(false);
        currency.setCreatedAt(now);

        when(currencyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            currencyService.setActiveCurrency(1L, true);
        });

        verify(currencyRepository, times(1)).findById(1L);
        verify(currencyRepository, times(0)).save(currency);
    }

    @Test
    void shouldReturnExchangeRatesForActiveCurrencies() {

        Currency currency1 = new Currency();
        Currency currency2 = new Currency();
        Currency currency3 = new Currency();
        Currency currency4 = new Currency();

        LocalDateTime now = LocalDateTime.now();

        currency1.setId(1L);
        currency1.setActive(true);
        currency1.setCreatedAt(now);
        currency1.setSymbol("USD");

        currency2.setId(2L);
        currency2.setActive(true);
        currency2.setCreatedAt(now);
        currency2.setSymbol("PLN");

        currency3.setId(3L);
        currency3.setActive(true);
        currency3.setCreatedAt(now);
        currency3.setSymbol("EUR");

        currency4.setId(3L);
        currency4.setActive(true);
        currency4.setCreatedAt(now);
        currency4.setSymbol("JPY");

        Map<String, String> map = new HashMap<>();

        map.put("USD", "1.0");
        map.put("EUR", "0.8543420978402488");
        map.put("PLN", "3.6197295");
        map.put("JPY", "157.6677222233333333");
        map.put("GBPY", "0.746094");
        map.put("CAD", "1.3683363333333333");

        CurrencyExchangeRatesResponse response = new CurrencyExchangeRatesResponse(
                new CurrencyExchangeRatesResponse.Data("USD", map));

        when(currencyRepository.findByActiveTrue()).thenReturn(List.of(currency1, currency2,
                currency3, currency4));
        when(currencyRepository.findBySymbol("USD")).thenReturn(Optional.of(currency1));
        when(exchangeRatesClient.getExchangeRates("USD")).thenReturn(response);

        CurrencyExchangeRatesResponse result = currencyService.getExchangeRates("USD");

        assertNotNull(result);
        assertEquals("USD", result.data().currency());
        assertEquals(4, result.data().rates().size());

        assertAll(
                () -> assertEquals("1.0", result.data().rates().get("USD")),
                () -> assertEquals("0.8543420978402488", result.data().rates().get("EUR")),
                () -> assertEquals("3.6197295", result.data().rates().get("PLN")),
                () -> assertEquals("157.6677222233333333", result.data().rates().get("JPY"))
        );
        verify(currencyRepository, times(1)).findBySymbol("USD");
        verify(currencyRepository, times(1)).findByActiveTrue();
        verify(exchangeRatesClient, times(1)).getExchangeRates("USD");

    }

    @Test
    void shouldThrowExceptionGetExchangeRatesCurrencyNotFound() {

        when(currencyRepository.findBySymbol("PLN")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> currencyService.getExchangeRates("PLN"));

        verify(currencyRepository, times(1)).findBySymbol("PLN");
        verify(currencyRepository, times(0)).findByActiveTrue();
        verify(exchangeRatesClient, times(0)).getExchangeRates("PLN");
    }

    @Test
    void shouldThrowExceptionGetExchangeRatesCurrencyInactive() {

        Currency currency = new Currency();

        currency.setId(1L);
        currency.setActive(false);
        currency.setCreatedAt(LocalDateTime.now());
        currency.setSymbol("EUR");

        when(currencyRepository.findBySymbol("EUR")).thenReturn(Optional.of(currency));

        assertThrows(CurrencyNotActiveException.class, () -> currencyService.getExchangeRates("EUR"));

        verify(currencyRepository, times(1)).findBySymbol("EUR");
        verify(currencyRepository, times(0)).findByActiveTrue();
        verify(exchangeRatesClient, times(0)).getExchangeRates("EUR");
    }

    @Test
    void shouldRunDeleteCurrencyById() {

        when(currencyRepository.existsById(1L)).thenReturn(true);

        currencyService.deleteCurrency(1L);

        verify(currencyRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionDeleteCurrencyByIdNotExists() {

        when(currencyRepository.existsById(1L)).thenReturn(false);


        assertThrows(NotFoundException.class, () -> {
            currencyService.deleteCurrency(1L);
        });

        verify(currencyRepository, times(0)).deleteById(1L);
    }
}
