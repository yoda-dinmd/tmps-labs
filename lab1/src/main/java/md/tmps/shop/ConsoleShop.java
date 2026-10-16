package md.tmps.shop;

import java.io.PrintStream;
import java.util.Scanner;

public final class ConsoleShop {
    private final ProductCatalog catalog;
    private final CheckoutService checkout;
    private final ReceiptPrinter receipts;
    private final Cart cart = new Cart();

    // ISP: this client receives only the catalog's read interface.
    public ConsoleShop(ProductCatalog catalog, CheckoutService checkout, ReceiptPrinter receipts) {
        this.catalog = catalog;
        this.checkout = checkout;
        this.receipts = receipts;
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
                    case "1" -> catalog.findAll().forEach(product -> out.printf("%s | %s | %s MDL%n",
                            product.id(), product.name(), product.price()));
                    case "2" -> {
                        out.print("Product ID: ");
                        if (!input.hasNextLine()) return;
                        Product product = catalog.findById(input.nextLine().trim())
                                .orElseThrow(() -> new IllegalArgumentException("Unknown product ID."));
                        out.print("Quantity: ");
                        if (!input.hasNextLine()) return;
                        cart.add(product, Integer.parseInt(input.nextLine().trim()));
                        out.println("Added to cart.");
                    }
                    case "3" -> {
                        if (cart.items().isEmpty()) out.println("Your cart is empty.");
                        cart.items().forEach(item -> out.printf("%s | %s x%d | %s MDL%n",
                                item.product().id(), item.product().name(), item.quantity(), item.subtotal()));
                    }
                    case "4" -> {
                        out.print("Product ID to remove: ");
                        if (!input.hasNextLine()) return;
                        out.println(cart.remove(input.nextLine().trim()) ? "Item removed." : "Item not in cart.");
                    }
                    case "5" -> {
                        Order order = checkout.checkout(cart);
                        receipts.print(order, out);
                        cart.clear();
                    }
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
