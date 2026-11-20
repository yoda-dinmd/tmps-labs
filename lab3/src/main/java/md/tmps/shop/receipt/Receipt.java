package md.tmps.shop.receipt;

import md.tmps.shop.domain.Order;

import java.io.PrintStream;

public interface Receipt {
    void print(Order order, PrintStream out);
}
