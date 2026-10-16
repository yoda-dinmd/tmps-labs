package md.tmps.shop;

import java.math.BigDecimal;

// OCP: new policies extend pricing without changing CheckoutService.
public interface DiscountPolicy {
    BigDecimal discountFor(BigDecimal subtotal);
}
