package com.pizzaria_system.data.dto;

import com.pizzaria_system.data.enums.PaymentMethod;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public class PaymentEntry {

    @Enumerated(EnumType.STRING)
    private PaymentMethod method; // Ex: CASH, CARD, PIX

    private BigDecimal amount; // O valor pago com este método

    // Construtores
    public PaymentEntry() {}

    public PaymentEntry(PaymentMethod method, BigDecimal amount) {
        this.method = method;
        this.amount = amount;
    }

    // Getters e Setters
    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    // hashCode e equals são recomendados para ElementCollection
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentEntry that)) return false;
        return method == that.method && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, amount);
    }
}