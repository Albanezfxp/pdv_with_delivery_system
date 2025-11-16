package com.pizzaria_system.data.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class OrderPayRequestDto {

    private Set<PaymentEntry> paymentEntries;
    private BigDecimal subtotal;
    private BigDecimal addition;
    private BigDecimal discount;
    private BigDecimal total;


    public Set<PaymentEntry> getPaymentEntries() {
        return paymentEntries;
    }

    public void setPaymentEntries(Set<PaymentEntry> paymentEntries) {
        this.paymentEntries = paymentEntries;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getAddition() {
        return addition;
    }

    public void setAddition(BigDecimal addition) {
        this.addition = addition;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
