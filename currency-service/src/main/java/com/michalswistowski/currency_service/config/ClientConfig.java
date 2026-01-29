package com.michalswistowski.currency_service.config;

import com.michalswistowski.currency_service.client.ExchangeRatesClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ClientConfig {

    @Bean
    public ExchangeRatesClient exchangeRatesClient(
            RestClient.Builder builder,
            @Value("${EXCHANGE_RATES_API_URL:https://api.coinbase.com}") String baseUrl) {

        RestClient restClient = builder.baseUrl(baseUrl).build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();

        return factory.createClient(ExchangeRatesClient.class);
    }
}
