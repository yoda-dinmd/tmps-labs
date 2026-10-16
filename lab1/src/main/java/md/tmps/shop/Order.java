package md.tmps.shop;

import java.math.BigDecimal;
import java.util.List;

public record Order(List<CartItem> items, BigDecimal subtotal, BigDecimal discount, BigDecimal total) {
    public Order {
        items = List.copyOf(items);
    }
}
