package md.tmps.shop;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class InMemoryProductCatalog implements ProductCatalog, ProductEditor {

    private static InMemoryProductCatalog instance;
    private InMemoryProductCatalog(){}

    public static InMemoryProductCatalog getInstance(){
        if (instance == null) {
            instance = new InMemoryProductCatalog();
        }
        return instance;
    }

    private final Map<String, Product> products = new LinkedHashMap<>();

    @Override
    public List<Product> findAll() {
        return List.copyOf(products.values());
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public void save(Product product) {
        products.put(product.id(), product);
    }
}
