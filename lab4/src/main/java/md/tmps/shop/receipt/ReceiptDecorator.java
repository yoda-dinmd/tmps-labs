package md.tmps.shop.receipt;

import md.tmps.shop.domain.Order;

import java.io.PrintStream;
import java.util.Objects;

public abstract class ReceiptDecorator implements Receipt {
    private final Receipt receipt;

    protected ReceiptDecorator(Receipt receipt) {
        this.receipt = Objects.requireNonNull(receipt, "receipt");
    }

    @Override
    public void print(Order order, PrintStream out) {
        receipt.print(order, out);
    }
}
