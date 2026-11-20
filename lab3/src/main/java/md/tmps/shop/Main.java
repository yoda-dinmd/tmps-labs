package md.tmps.shop;

import md.tmps.shop.catalog.InMemoryProductCatalog;
import md.tmps.shop.catalog.LazyProductCatalog;
import md.tmps.shop.catalog.ProductCatalog;
import md.tmps.shop.catalog.ProductEditor;
import md.tmps.shop.checkout.CheckoutService;
import md.tmps.shop.checkout.PromotionalCheckoutService;
import md.tmps.shop.checkout.RegularCheckoutService;
import md.tmps.shop.client.ConsoleShop;
import md.tmps.shop.domain.Product;
import md.tmps.shop.facade.Shop;
import md.tmps.shop.facade.ShopFacade;
import md.tmps.shop.receipt.ItemCountReceipt;
import md.tmps.shop.receipt.Receipt;
import md.tmps.shop.receipt.ReceiptPrinter;
import md.tmps.shop.receipt.ReturnNoteReceipt;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public final class Main {
    private static final BigDecimal DISCOUNT_PERCENTAGE = new BigDecimal("10");

    private Main() { }

    public static void main(String[] args) {
        List<String> options = List.of(args);
        ProductCatalog catalog = new LazyProductCatalog(Main::loadCatalog);

        boolean regular = options.contains("--regular");
        CheckoutService checkout = regular
                ? new RegularCheckoutService()
                : new PromotionalCheckoutService(DISCOUNT_PERCENTAGE);
        if (regular) {
            System.out.println("Regular checkout: no discount.");
        } else {
            System.out.printf("Today's promotion: %s%% off every order.%n", DISCOUNT_PERCENTAGE);
        }

        Receipt receipt = new ReceiptPrinter();
        if (!options.contains("--plain-receipt")) {
            receipt = new ReturnNoteReceipt(new ItemCountReceipt(receipt));
        }
        Shop shop = new ShopFacade(catalog, checkout, receipt);
        new ConsoleShop(shop).run(new Scanner(System.in), System.out);
    }

    private static ProductCatalog loadCatalog() {
        System.out.println("Loading product catalog...");
        InMemoryProductCatalog catalog = InMemoryProductCatalog.getInstance();
        seedProducts(catalog);
        return catalog;
    }

    private static void seedProducts(ProductEditor editor) {
        editor.save(new Product("P1", "Mechanical keyboard", new BigDecimal("850.00")));
        editor.save(new Product("P2", "Wireless mouse", new BigDecimal("350.00")));
        editor.save(new Product("P3", "USB-C cable", new BigDecimal("120.00")));
    }
}
