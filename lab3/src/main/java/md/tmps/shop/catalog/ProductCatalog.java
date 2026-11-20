package md.tmps.shop.catalog;

import md.tmps.shop.domain.Product;

import java.util.List;
import java.util.Optional;

// ISP: shoppers only need read operations.
public interface ProductCatalog {
    List<Product> findAll();
    Optional<Product> findById(String id);
}
