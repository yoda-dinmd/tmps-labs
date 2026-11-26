package md.tmps.shop.receipt;

import md.tmps.shop.domain.CartItem;
import md.tmps.shop.domain.Order;

import java.io.PrintStream;

// SRP: receipt presentation can change independently of checkout calculations.
public final class ReceiptPrinter implements Receipt {
    @Override
    public void print(Order order, PrintStream out) {
        out.println("\n--- TMPS Shop receipt ---");
        for (CartItem item : order.items()) {
            out.printf("%s x%d: %s MDL%n", item.product().name(), item.quantity(), item.subtotal());
        }
        out.printf("Subtotal: %s MDL%nDiscount: %s MDL%nTotal:    %s MDL%n",
                order.subtotal(), order.discount(), order.total());
        out.println("Thank you for shopping!");
    }
}
