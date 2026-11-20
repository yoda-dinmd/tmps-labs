package md.tmps.shop.catalog;

import md.tmps.shop.domain.Product;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public final class LazyProductCatalog implements ProductCatalog {
    private final Supplier<? extends ProductCatalog> loader;
    private ProductCatalog catalog;

    public LazyProductCatalog(Supplier<? extends ProductCatalog> loader) {
        this.loader = Objects.requireNonNull(loader, "loader");
    }

    @Override
    public List<Product> findAll() {
        return catalog().findAll();
    }

    @Override
    public Optional<Product> findById(String id) {
        return catalog().findById(id);
    }

    private synchronized ProductCatalog catalog() {
        if (catalog == null) {
            catalog = Objects.requireNonNull(loader.get(), "loaded catalog");
        }
        return catalog;
    }
}
