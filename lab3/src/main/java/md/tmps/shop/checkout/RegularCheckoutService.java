package md.tmps.shop.checkout;

public final class RegularCheckoutService extends CheckoutService {
    @Override
    protected DiscountPolicy createDiscountPolicy() {
        return new NoDiscount();
    }
}
