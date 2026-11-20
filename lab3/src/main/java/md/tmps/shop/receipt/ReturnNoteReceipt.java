package md.tmps.shop.receipt;

import md.tmps.shop.domain.Order;

import java.io.PrintStream;

public final class ReturnNoteReceipt extends ReceiptDecorator {
    public ReturnNoteReceipt(Receipt receipt) {
        super(receipt);
    }

    @Override
    public void print(Order order, PrintStream out) {
        super.print(order, out);
        out.println("Keep this receipt for returns.");
    }
}
