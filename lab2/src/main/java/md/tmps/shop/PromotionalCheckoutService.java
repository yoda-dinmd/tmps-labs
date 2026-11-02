package md.tmps.shop;

import java.math.BigDecimal;

public final class PromotionalCheckoutService extends CheckoutService {
    private final BigDecimal percentage;

    public PromotionalCheckoutService(BigDecimal percentage) {
        this.percentage = percentage;
    }

    @Override
    protected DiscountPolicy createDiscountPolicy() {
        return new PercentageDiscount(percentage);
    }
}
