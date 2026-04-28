package com.michalswistowski.currency_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_rates")
@Getter
@Setter
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "base_currency_id")
    private Currency baseCurrency;

    @ManyToOne(fetch = FetchType.EAGER) // default
    @JoinColumn(name = "target_currency_id")
    private Currency targetCurrency;

    @Column(precision = 19, scale = 6) // precision - whole + decimal digits, scale - decimal digits
    private BigDecimal rate;

    @Column
    @UpdateTimestamp
    private LocalDateTime timestamp;
}
