package com.michalswistowski.currency_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table
@EntityListeners(AuditingEntityListener.class)
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String symbol;

    @PrePersist
    @PreUpdate
    public void toUpperCase() {
        if (symbol != null) {
            this.symbol = symbol.toUpperCase();
        }
    }

    @Column
    private boolean active;

    @CreatedDate
    @Column
    private LocalDateTime createdAt;
}
