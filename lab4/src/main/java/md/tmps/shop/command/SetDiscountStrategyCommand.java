package md.tmps.shop.command;

import md.tmps.shop.strategy.CheckoutService;
import md.tmps.shop.strategy.DiscountStrategy;

import java.util.Objects;

public final class SetDiscountStrategyCommand implements ShopCommand {
    private final CheckoutService checkout;
    private final DiscountStrategy nextStrategy;
    private DiscountStrategy previousStrategy;

    public SetDiscountStrategyCommand(CheckoutService checkout, DiscountStrategy nextStrategy) {
        this.checkout = Objects.requireNonNull(checkout, "checkout");
        this.nextStrategy = Objects.requireNonNull(nextStrategy, "nextStrategy");
    }

    @Override
    public boolean execute() {
        previousStrategy = checkout.discountStrategy();
        checkout.setDiscountStrategy(nextStrategy);
        return true;
    }

    @Override
    public void undo() {
        if (previousStrategy != null) {
            checkout.setDiscountStrategy(previousStrategy);
        }
    }

    @Override
    public String description() {
        return "Set discount to " + nextStrategy.description();
    }
}
