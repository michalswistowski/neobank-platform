package com.michalswistowski.currency_service.mapper;

import com.michalswistowski.currency_service.dto.CurrencyRequest;
import com.michalswistowski.currency_service.dto.CurrencyResponse;
import com.michalswistowski.currency_service.entity.Currency;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {

    CurrencyResponse mapCurrencyToCurrencyResponse(Currency currency);

    Currency mapCurrencyRequestToCurrency(CurrencyRequest currencyRequest);
}
