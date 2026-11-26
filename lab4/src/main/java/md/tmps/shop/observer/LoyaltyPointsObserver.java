package md.tmps.shop.observer;

import md.tmps.shop.domain.Order;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Objects;

public final class LoyaltyPointsObserver implements OrderObserver {
    private static final BigDecimal MDL_PER_POINT = BigDecimal.TEN;
    private final PrintStream out;
    private int totalPoints;

    public LoyaltyPointsObserver(PrintStream out) {
        this.out = Objects.requireNonNull(out, "out");
    }

    @Override
    public String name() {
        return "Loyalty points tracker (total: " + totalPoints + " pts)";
    }

    @Override
    public void onOrderCompleted(Order order) {
        int earned = order.total().divideToIntegralValue(MDL_PER_POINT).intValueExact();
        totalPoints += earned;
        out.printf("[Loyalty] Earned %d points (total: %d pts).%n", earned, totalPoints);
    }

    public int totalPoints() {
        return totalPoints;
    }
}
