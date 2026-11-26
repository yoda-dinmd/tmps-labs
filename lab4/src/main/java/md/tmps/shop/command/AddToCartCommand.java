package md.tmps.shop.command;

import md.tmps.shop.domain.Cart;
import md.tmps.shop.domain.Product;

import java.util.Objects;

public final class AddToCartCommand implements ShopCommand {
    private final Cart cart;
    private final Product product;
    private final int quantity;

    public AddToCartCommand(Cart cart, Product product, int quantity) {
        this.cart = Objects.requireNonNull(cart, "cart");
        this.product = Objects.requireNonNull(product, "product");
        this.quantity = quantity;
    }

    @Override
    public boolean execute() {
        cart.add(product, quantity);
        return true;
    }

    @Override
    public void undo() {
        cart.remove(product.id(), quantity);
    }

    @Override
    public String description() {
        return "Add " + product.name() + " x" + quantity;
    }
}
