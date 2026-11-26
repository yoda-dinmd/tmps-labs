package md.tmps.shop.strategy;

import java.math.BigDecimal;

// Strategy: defines a common contract for interchangeable discount algorithms.
public interface DiscountStrategy {
    String description();
    BigDecimal discountFor(BigDecimal subtotal);
}
