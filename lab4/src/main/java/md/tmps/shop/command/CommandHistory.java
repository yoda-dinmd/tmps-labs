package md.tmps.shop.command;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

// Invoker: executes commands through the ShopCommand interface and keeps an undo stack.
public final class CommandHistory {
    private final Deque<ShopCommand> executed = new ArrayDeque<>();

    public boolean execute(ShopCommand command) {
        Objects.requireNonNull(command, "command");
        if (command.execute()) {
            executed.push(command);
            return true;
        }
        return false;
    }

    public Optional<String> undo() {
        if (executed.isEmpty()) {
            return Optional.empty();
        }
        ShopCommand last = executed.pop();
        last.undo();
        return Optional.of(last.description());
    }

    public List<String> descriptions() {
        return executed.stream().map(ShopCommand::description).toList();
    }

    public void clear() {
        executed.clear();
    }
}
