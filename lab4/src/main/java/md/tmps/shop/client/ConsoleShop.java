package md.tmps.shop.client;

import md.tmps.shop.facade.Shop;
import md.tmps.shop.observer.OrderObserver;
import md.tmps.shop.strategy.DiscountStrategy;

import java.io.PrintStream;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public final class ConsoleShop {
    private final Shop shop;
    private final List<DiscountStrategy> availableStrategies;
    private final List<OrderObserver> availableObservers;

    public ConsoleShop(Shop shop,
                       List<DiscountStrategy> availableStrategies,
                       List<OrderObserver> availableObservers) {
        this.shop = Objects.requireNonNull(shop, "shop");
        this.availableStrategies = List.copyOf(Objects.requireNonNull(availableStrategies, "availableStrategies"));
        this.availableObservers = List.copyOf(Objects.requireNonNull(availableObservers, "availableObservers"));
    }

    public void run(Scanner input, PrintStream out) {
        out.println("Welcome to TMPS Shop! All prices are in MDL.");
        out.printf("Active discount: %s%n", shop.activeDiscount().description());
        while (true) {
            out.println("\n1. Products  2. Add to cart  3. View cart  4. Remove item  5. Undo");
            out.println("6. Change discount  7. Manage observers  8. Checkout  0. Exit");
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
                        out.printf("Active discount: %s%n", shop.activeDiscount().description());
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
                    case "5" -> shop.undoLastAction().ifPresentOrElse(
                            undone -> out.printf("Undone: %s%n", undone),
                            () -> out.println("Nothing to undo."));
                    case "6" -> chooseDiscountStrategy(input, out);
                    case "7" -> toggleObserver(input, out);
                    case "8" -> shop.checkout(out);
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

    private void chooseDiscountStrategy(Scanner input, PrintStream out) {
        DiscountStrategy active = shop.activeDiscount();
        for (int i = 0; i < availableStrategies.size(); i++) {
            DiscountStrategy strategy = availableStrategies.get(i);
            String marker = strategy.equals(active) ? " (active)" : "";
            out.printf("%d. %s%s%n", i + 1, strategy.description(), marker);
        }
        out.print("Select discount strategy: ");
        if (!input.hasNextLine()) return;
        int index = Integer.parseInt(input.nextLine().trim()) - 1;
        if (index < 0 || index >= availableStrategies.size()) {
            out.println("Invalid strategy number.");
            return;
        }
        DiscountStrategy chosen = availableStrategies.get(index);
        shop.setDiscountStrategy(chosen);
        out.printf("Active discount set to: %s%n", chosen.description());
    }

    private void toggleObserver(Scanner input, PrintStream out) {
        for (int i = 0; i < availableObservers.size(); i++) {
            OrderObserver observer = availableObservers.get(i);
            String status = shop.isObserverSubscribed(observer) ? "ON" : "OFF";
            out.printf("%d. [%s] %s%n", i + 1, status, observer.name());
        }
        out.print("Toggle observer number (0 to cancel): ");
        if (!input.hasNextLine()) return;
        int index = Integer.parseInt(input.nextLine().trim()) - 1;
        if (index == -1) return;
        if (index < 0 || index >= availableObservers.size()) {
            out.println("Invalid observer number.");
            return;
        }
        OrderObserver chosen = availableObservers.get(index);
        if (shop.isObserverSubscribed(chosen)) {
            shop.unsubscribeObserver(chosen);
            out.printf("Unsubscribed: %s%n", chosen.name());
        } else {
            shop.subscribeObserver(chosen);
            out.printf("Subscribed: %s%n", chosen.name());
        }
    }
}
