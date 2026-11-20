package md.tmps.shop.client;

import md.tmps.shop.facade.Shop;

import java.io.PrintStream;
import java.util.Scanner;

public final class ConsoleShop {
    private final Shop shop;

    public ConsoleShop(Shop shop) {
        this.shop = shop;
    }

    public void run(Scanner input, PrintStream out) {
        out.println("Welcome to TMPS Shop! All prices are in MDL.");
        while (true) {
            out.println("\n1. Products  2. Add to cart  3. View cart  4. Remove item  5. Checkout  0. Exit");
            out.print("> ");
            if (!input.hasNextLine()) return;
            String choice = input.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> shop.products().forEach(product -> out.printf("%s | %s | %s MDL%n",
                            product.id(), product.name(), product.price()));
                    case "2" -> {
                        out.print("Product ID: ");
                        if (!input.hasNextLine()) return;
                        String productId = input.nextLine().trim();
                        out.print("Quantity: ");
                        if (!input.hasNextLine()) return;
                        shop.addToCart(productId, Integer.parseInt(input.nextLine().trim()));
                        out.println("Added to cart.");
                    }
                    case "3" -> {
                        var items = shop.cartItems();
                        if (items.isEmpty()) out.println("Your cart is empty.");
                        items.forEach(item -> out.printf("%s | %s x%d | %s MDL%n",
                                item.product().id(), item.product().name(), item.quantity(), item.subtotal()));
                    }
                    case "4" -> {
                        out.print("Product ID to remove: ");
                        if (!input.hasNextLine()) return;
                        String productId = input.nextLine().trim();
                        out.print("Quantity: ");
                        if (!input.hasNextLine()) return;
                        int quantity = Integer.parseInt(input.nextLine().trim());
                        out.println(shop.removeFromCart(productId, quantity) ? "Item removed." : "Item not in cart.");
                    }
                    case "5" -> shop.checkout(out);
                    case "0" -> { out.println("Goodbye!"); return; }
                    default -> out.println("Choose a number from the menu.");
                }
            } catch (NumberFormatException | ArithmeticException exception) {
                out.println("Enter a valid whole-number quantity within the supported range.");
            } catch (IllegalArgumentException exception) {
                out.println(exception.getMessage());
            }
        }
    }
}
