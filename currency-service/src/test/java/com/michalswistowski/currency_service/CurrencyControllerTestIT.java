package com.michalswistowski.currency_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.michalswistowski.currency_service.dto.CurrencyExchangeRatesResponse;
import com.michalswistowski.currency_service.dto.CurrencyRequest;
import com.michalswistowski.currency_service.dto.CurrencyResponse;
import com.michalswistowski.currency_service.exception.CurrencyNotActiveException;
import com.michalswistowski.currency_service.exception.EntityAlreadyExistsException;
import com.michalswistowski.currency_service.exception.NotFoundException;
import com.michalswistowski.currency_service.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
//@AutoConfigureMockMvc
//@SpringBootTest
public class CurrencyControllerTestIT {

    @MockitoBean
    private CurrencyService currencyService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnListOfAllCurrencies() throws Exception {

        LocalDateTime now = LocalDateTime.now();

        CurrencyResponse currency1 = new CurrencyResponse(1L, "PLN", true, now);
        CurrencyResponse currency2 = new CurrencyResponse(2L, "USD", true, now);
        CurrencyResponse currency3 = new CurrencyResponse(3L, "EUR", true, now);
        CurrencyResponse currency4 = new CurrencyResponse(4L, "JPY", true, now);

        List<CurrencyResponse> list = new ArrayList<>(List.of(
                currency1, currency2, currency3, currency4
        ));

        when(currencyService.getAllCurrencies()).thenReturn(list);

        mockMvc.perform(get("/api/currency"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    void shouldReturnEmptyList() throws Exception {

        when(currencyService.getAllCurrencies()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/currency"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnInternalServerError() throws Exception {

        when(currencyService.getAllCurrencies()).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/currency"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.INTERNAL_SERVER_ERROR.value())))
                .andExpect(jsonPath("$.message", is("DB error")));
    }

    @Test
    void shouldReturnMethodNotAllowedError() throws Exception {

        mockMvc.perform(put("/api/currency"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.METHOD_NOT_ALLOWED.value())));
    }

    @Test
    void shouldReturnCurrency() throws Exception {

        CurrencyRequest request = new CurrencyRequest("PLN");
        CurrencyResponse response = new CurrencyResponse(1L, "PLN", true, LocalDateTime.now());

        when(currencyService.addCurrency(request)).thenReturn(response);

        mockMvc.perform(post("/api/currency")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.symbol", is("PLN")))
                .andExpect(jsonPath("$.active", is(true)));
    }

    @Test
    void shouldReturnNotValidErrorSizeInvalid() throws Exception {

        CurrencyRequest request = new CurrencyRequest("PL");

        mockMvc.perform(post("/api/currency")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldThrowNotValidErrorSymbolNull() throws Exception {

        CurrencyRequest request = new CurrencyRequest(null);

        mockMvc.perform(post("/api/currency")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldReturnCurrencyAlreadyExistsError() throws Exception {

        CurrencyRequest request = new CurrencyRequest("PLN");

        when(currencyService.addCurrency(request))
                .thenThrow(new EntityAlreadyExistsException("Currency with symbol PLN already exists"));

        mockMvc.perform(post("/api/currency")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode", is(409)))
                .andExpect(jsonPath("$.message", is("Currency with symbol PLN already exists")));
    }

    @Test
    void shouldReturnExchangeRates() throws Exception {

        Map<String, String> rates = new LinkedHashMap<>();

        rates.put("USD", "1.0");
        rates.put("EUR", "0.8543420978402488");
        rates.put("PLN", "3.6197295");
        rates.put("JPY", "157.6677222233333333");
        rates.put("GBPY", "0.746094");
        rates.put("CAD", "1.3683363333333333");

        CurrencyExchangeRatesResponse resp = new CurrencyExchangeRatesResponse(
                new CurrencyExchangeRatesResponse.Data("USD", rates));

        when(currencyService.getExchangeRates("USD")).thenReturn(resp);

        mockMvc.perform(get("/api/currency/exchangeRates/USD"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.currency", is("USD")))
                .andExpect(jsonPath("$.data.rates.length()").value(6));
    }

    @Test
    void shouldReturnEmptyExchangeRates() throws Exception {

        Map<String, String> rates = new LinkedHashMap<>();

        CurrencyExchangeRatesResponse resp = new CurrencyExchangeRatesResponse(
                new CurrencyExchangeRatesResponse.Data("USD", rates));

        when(currencyService.getExchangeRates("USD")).thenReturn(resp);

        mockMvc.perform(get("/api/currency/exchangeRates/USD"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.currency", is("USD")))
                .andExpect(jsonPath("$.data.rates.length()").value(0));
    }

    @Test
    void shouldReturnNotFoundError() throws Exception {

        when(currencyService.getExchangeRates("USD"))
                .thenThrow(new NotFoundException("Currency with symbol USD not found"));

        mockMvc.perform(get("/api/currency/exchangeRates/USD"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode", is(404)))
                .andExpect(jsonPath("$.message", is("Currency with symbol USD not found")));
    }

    @Test
    void shouldReturnInactiveError() throws Exception {

        when(currencyService.getExchangeRates("USD"))
                .thenThrow(new CurrencyNotActiveException("Currency with symbol USD is inactive"));

        mockMvc.perform(get("/api/currency/exchangeRates/USD"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode", is(422)))
                .andExpect(jsonPath("$.message", is("Currency with symbol USD is inactive")));
    }
}
