package md.tmps.shop;

import java.math.BigDecimal;
import java.util.Scanner;

public final class Main {
    // Configure the shop's percentage discount here (0 through 100).
    private static final BigDecimal DISCOUNT_PERCENTAGE = new BigDecimal("10");

    private Main() { }

    public static void main(String[] args) {
        InMemoryProductCatalog catalog = new InMemoryProductCatalog();
        seedProducts(catalog);
        DiscountPolicy discount = new PercentageDiscount(DISCOUNT_PERCENTAGE);
        System.out.printf("Today's promotion: %s%% off every order.%n", DISCOUNT_PERCENTAGE);
        ConsoleShop shop = new ConsoleShop(catalog, new CheckoutService(discount), new ReceiptPrinter());
        shop.run(new Scanner(System.in), System.out);
    }

    // ISP: initial setup only needs the write interface.
    private static void seedProducts(ProductEditor editor) {
        editor.save(new Product("P1", "Mechanical keyboard", new BigDecimal("850.00")));
        editor.save(new Product("P2", "Wireless mouse", new BigDecimal("350.00")));
        editor.save(new Product("P3", "USB-C cable", new BigDecimal("120.00")));
    }
}
