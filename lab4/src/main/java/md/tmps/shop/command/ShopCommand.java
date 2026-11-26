package md.tmps.shop.command;

// Command: encapsulates a reversible shop action.
public interface ShopCommand {
    boolean execute();
    void undo();
    String description();
}
