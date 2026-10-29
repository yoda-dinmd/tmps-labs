package md.tmps.shop;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Product(String id, String name, BigDecimal price) {
    public Product {
        if (id == null || id.isBlank() || name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product ID and name are required.");
        }
        Objects.requireNonNull(price, "price");
        if (price.signum() < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        price = price.setScale(2, RoundingMode.HALF_UP);
    }
}
