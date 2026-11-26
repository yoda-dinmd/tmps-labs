package md.tmps.shop.command;

import md.tmps.shop.domain.Cart;
import md.tmps.shop.domain.CartItem;

import java.util.Objects;

public final class RemoveFromCartCommand implements ShopCommand {
    private final Cart cart;
    private final String productId;
    private final int quantity;
    private CartItem removedItem;

    public RemoveFromCartCommand(Cart cart, String productId, int quantity) {
        this.cart = Objects.requireNonNull(cart, "cart");
        this.productId = Objects.requireNonNull(productId, "productId");
        this.quantity = quantity;
    }

    @Override
    public boolean execute() {
        removedItem = cart.take(productId, quantity).orElse(null);
        return removedItem != null;
    }

    @Override
    public void undo() {
        if (removedItem != null) {
            cart.add(removedItem.product(), removedItem.quantity());
        }
    }

    @Override
    public String description() {
        return removedItem == null
                ? "Remove " + productId + " x" + quantity
                : "Remove " + removedItem.product().name() + " x" + removedItem.quantity();
    }
}
