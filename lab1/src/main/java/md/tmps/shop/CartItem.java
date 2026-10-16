package md.tmps.shop;

import java.math.BigDecimal;
import java.util.Objects;

public record CartItem(Product product, int quantity) {
    public CartItem {
        Objects.requireNonNull(product, "product");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
    }

    public BigDecimal subtotal() {
        return product.price().multiply(BigDecimal.valueOf(quantity));
    }
}
