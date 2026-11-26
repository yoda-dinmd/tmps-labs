package md.tmps.shop.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class ThresholdFixedDiscountStrategy implements DiscountStrategy {
    private final BigDecimal minimumSubtotal;
    private final BigDecimal fixedDiscount;

    public ThresholdFixedDiscountStrategy(BigDecimal minimumSubtotal, BigDecimal fixedDiscount) {
        Objects.requireNonNull(minimumSubtotal, "minimumSubtotal");
        Objects.requireNonNull(fixedDiscount, "fixedDiscount");
        if (minimumSubtotal.signum() <= 0) {
            throw new IllegalArgumentException("Minimum subtotal must be positive.");
        }
        if (fixedDiscount.signum() < 0) {
            throw new IllegalArgumentException("Fixed discount cannot be negative.");
        }
        this.minimumSubtotal = minimumSubtotal.setScale(2, RoundingMode.HALF_UP);
        this.fixedDiscount = fixedDiscount.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String description() {
        return fixedDiscount + " MDL off orders >= " + minimumSubtotal + " MDL";
    }

    @Override
    public BigDecimal discountFor(BigDecimal subtotal) {
        if (subtotal.compareTo(minimumSubtotal) < 0) {
            return new BigDecimal("0.00");
        }
        return fixedDiscount.min(subtotal).setScale(2, RoundingMode.HALF_UP);
    }
}
