package md.tmps.shop.observer;

import md.tmps.shop.domain.Order;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class OrderAuditObserver implements OrderObserver {
    private final PrintStream out;
    private final List<Order> recordedOrders = new ArrayList<>();

    public OrderAuditObserver(PrintStream out) {
        this.out = Objects.requireNonNull(out, "out");
    }

    @Override
    public String name() {
        return "Order audit log (recorded: " + recordedOrders.size() + ")";
    }

    @Override
    public void onOrderCompleted(Order order) {
        recordedOrders.add(order);
        out.printf("[Audit] Recorded order #%d: %s MDL (%d line items).%n",
                recordedOrders.size(), order.total(), order.items().size());
    }

    public List<Order> recordedOrders() {
        return List.copyOf(recordedOrders);
    }
}
