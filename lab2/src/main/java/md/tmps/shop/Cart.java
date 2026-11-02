package md.tmps.shop;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// SRP: maintains cart contents; knows nothing about input, discounts or receipts.
public final class Cart {
    private final Map<String, CartItem> items = new LinkedHashMap<>();

    public void add(Product product, int quantity) {
        CartItem addition = new CartItem(product, quantity);
        CartItem existing = items.get(product.id());
        if (existing != null) {
            addition = new CartItem(existing.product(), Math.addExact(existing.quantity(), quantity));
        }
        items.put(product.id(), addition);
    }

    public boolean remove(String productId) {
        return items.remove(productId) != null;
    }

    public boolean remove(String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        CartItem existing = items.get(productId);
        if (existing == null) {
            return false;
        }
        if (quantity >= existing.quantity()) {
            items.remove(productId);
        } else {
            items.put(productId, new CartItem(existing.product(), existing.quantity() - quantity));
        }
        return true;
    }

    public List<CartItem> items() {
        return List.copyOf(items.values());
    }

    public void clear() {
        items.clear();
    }
}
