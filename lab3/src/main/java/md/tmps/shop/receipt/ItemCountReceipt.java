package md.tmps.shop.receipt;

import md.tmps.shop.domain.CartItem;
import md.tmps.shop.domain.Order;

import java.io.PrintStream;

public final class ItemCountReceipt extends ReceiptDecorator {
    public ItemCountReceipt(Receipt receipt) {
        super(receipt);
    }

    @Override
    public void print(Order order, PrintStream out) {
        super.print(order, out);
        long quantity = order.items().stream().mapToLong(CartItem::quantity).sum();
        out.printf("Items purchased: %d%n", quantity);
    }
}
