package md.tmps.shop;

import java.math.BigDecimal;
import java.util.List;

public record Order(List<CartItem> items, BigDecimal subtotal, BigDecimal discount, BigDecimal total) {
    public Order {
        items = List.copyOf(items);
    }

    // Builder: assembles the order step by step; the discount is optional.
    public static class Builder {
        private List<CartItem> items = List.of();
        private BigDecimal subtotal = new BigDecimal("0.00");
        private BigDecimal discount = new BigDecimal("0.00");

        public Builder items(List<CartItem> items) {
            this.items = items;
            return this;
        }

        public Builder subtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
            return this;
        }

        public Builder discount(BigDecimal discount) {
            this.discount = discount;
            return this;
        }

        public Order build() {
            return new Order(items, subtotal, discount, subtotal.subtract(discount));
        }
    }
}
