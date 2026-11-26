package md.tmps.shop.observer;

import md.tmps.shop.domain.Order;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class HighValueOrderAlertObserver implements OrderObserver {
    private final BigDecimal threshold;
    private final PrintStream out;
    private final List<Order> alertedOrders = new ArrayList<>();

    public HighValueOrderAlertObserver(BigDecimal threshold, PrintStream out) {
        Objects.requireNonNull(threshold, "threshold");
        if (threshold.signum() <= 0) {
            throw new IllegalArgumentException("Alert threshold must be positive.");
        }
        this.threshold = threshold.setScale(2, RoundingMode.HALF_UP);
        this.out = Objects.requireNonNull(out, "out");
    }

    @Override
    public String name() {
        return "High-value order alert (>= " + threshold + " MDL)";
    }

    @Override
    public void onOrderCompleted(Order order) {
        if (order.total().compareTo(threshold) >= 0) {
            alertedOrders.add(order);
            out.printf("[Alert] High-value order: %s MDL (threshold: %s MDL).%n",
                    order.total(), threshold);
        }
    }

    public List<Order> alertedOrders() {
        return List.copyOf(alertedOrders);
    }
}
