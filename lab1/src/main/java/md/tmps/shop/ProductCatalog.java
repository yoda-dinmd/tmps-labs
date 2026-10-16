package md.tmps.shop;

import java.util.List;
import java.util.Optional;

// ISP: shoppers only need read operations.
public interface ProductCatalog {
    List<Product> findAll();
    Optional<Product> findById(String id);
}
