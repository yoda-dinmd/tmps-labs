package md.tmps.shop.facade;

import md.tmps.shop.domain.CartItem;
import md.tmps.shop.domain.Product;

import java.io.PrintStream;
import java.util.List;

public interface Shop {
    List<Product> products();
    void addToCart(String productId, int quantity);
    List<CartItem> cartItems();
    boolean removeFromCart(String productId, int quantity);
    void checkout(PrintStream out);
}
