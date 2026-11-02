package md.tmps.shop;

public final class RegularCheckoutService extends CheckoutService {
    @Override
    protected DiscountPolicy createDiscountPolicy() {
        return new NoDiscount();
    }
}
