package md.tmps.shop;

import md.tmps.shop.catalog.InMemoryProductCatalog;
import md.tmps.shop.catalog.LazyProductCatalog;
import md.tmps.shop.catalog.ProductCatalog;
import md.tmps.shop.catalog.ProductEditor;
import md.tmps.shop.client.ConsoleShop;
import md.tmps.shop.command.CommandHistory;
import md.tmps.shop.domain.Product;
import md.tmps.shop.facade.Shop;
import md.tmps.shop.facade.ShopFacade;
import md.tmps.shop.observer.HighValueOrderAlertObserver;
import md.tmps.shop.observer.LoyaltyPointsObserver;
import md.tmps.shop.observer.OrderAuditObserver;
import md.tmps.shop.observer.OrderEventBus;
import md.tmps.shop.observer.OrderObserver;
import md.tmps.shop.observer.OrderSubject;
import md.tmps.shop.receipt.ItemCountReceipt;
import md.tmps.shop.receipt.Receipt;
import md.tmps.shop.receipt.ReceiptPrinter;
import md.tmps.shop.receipt.ReturnNoteReceipt;
import md.tmps.shop.strategy.CheckoutService;
import md.tmps.shop.strategy.DiscountStrategy;
import md.tmps.shop.strategy.NoDiscountStrategy;
import md.tmps.shop.strategy.PercentageDiscountStrategy;
import md.tmps.shop.strategy.ThresholdFixedDiscountStrategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public final class Main {
    private static final BigDecimal DISCOUNT_PERCENTAGE = new BigDecimal("10");
    private static final BigDecimal THRESHOLD_SUBTOTAL = new BigDecimal("500.00");
    private static final BigDecimal THRESHOLD_DISCOUNT = new BigDecimal("100.00");
    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("500.00");

    private Main() { }

    public static void main(String[] args) {
        List<String> options = List.of(args);
        ProductCatalog catalog = new LazyProductCatalog(Main::loadCatalog);

        DiscountStrategy percentageDiscount = new PercentageDiscountStrategy(DISCOUNT_PERCENTAGE);
        DiscountStrategy thresholdDiscount = new ThresholdFixedDiscountStrategy(THRESHOLD_SUBTOTAL, THRESHOLD_DISCOUNT);
        DiscountStrategy noDiscount = new NoDiscountStrategy();
        List<DiscountStrategy> strategies = List.of(percentageDiscount, thresholdDiscount, noDiscount);

        DiscountStrategy initialStrategy = options.contains("--regular") ? noDiscount : percentageDiscount;
        CheckoutService checkout = new CheckoutService(initialStrategy);

        Receipt receipt = new ReceiptPrinter();
        if (!options.contains("--plain-receipt")) {
            receipt = new ReturnNoteReceipt(new ItemCountReceipt(receipt));
        }

        OrderSubject eventBus = new OrderEventBus();
        OrderObserver loyalty = new LoyaltyPointsObserver(System.out);
        OrderObserver audit = new OrderAuditObserver(System.out);
        OrderObserver alert = new HighValueOrderAlertObserver(HIGH_VALUE_THRESHOLD, System.out);
        List<OrderObserver> observers = List.of(loyalty, audit, alert);
        observers.forEach(eventBus::subscribe);

        Shop shop = new ShopFacade(catalog, checkout, receipt, new CommandHistory(), eventBus);
        new ConsoleShop(shop, strategies, observers).run(new Scanner(System.in), System.out);
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
