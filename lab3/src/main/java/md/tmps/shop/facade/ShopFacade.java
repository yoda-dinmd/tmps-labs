package md.tmps.shop.facade;

import md.tmps.shop.catalog.ProductCatalog;
import md.tmps.shop.checkout.CheckoutService;
import md.tmps.shop.domain.Cart;
import md.tmps.shop.domain.CartItem;
import md.tmps.shop.domain.Order;
import md.tmps.shop.domain.Product;
import md.tmps.shop.receipt.Receipt;

import java.io.PrintStream;
import java.util.List;

public final class ShopFacade implements Shop {
    private final ProductCatalog catalog;
    private final CheckoutService checkout;
    private final Receipt receipt;
    private final Cart cart = new Cart();

    public ShopFacade(ProductCatalog catalog, CheckoutService checkout, Receipt receipt) {
        this.catalog = catalog;
        this.checkout = checkout;
        this.receipt = receipt;
    }

    @Override
    public List<Product> products() {
        return catalog.findAll();
    }

    @Override
    public void addToCart(String productId, int quantity) {
        Product product = catalog.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown product ID."));
        cart.add(product, quantity);
    }

    @Override
    public List<CartItem> cartItems() {
        return cart.items();
    }

    @Override
    public boolean removeFromCart(String productId, int quantity) {
        return cart.remove(productId, quantity);
    }

    @Override
    public void checkout(PrintStream out) {
        Order order = checkout.checkout(cart);
        receipt.print(order, out);
        cart.clear();
    }
}
