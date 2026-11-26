package md.tmps.shop.strategy;

import java.math.BigDecimal;

public final class NoDiscountStrategy implements DiscountStrategy {
    @Override
    public String description() {
        return "No discount";
    }

    @Override
    public BigDecimal discountFor(BigDecimal subtotal) {
        return new BigDecimal("0.00");
    }
}
