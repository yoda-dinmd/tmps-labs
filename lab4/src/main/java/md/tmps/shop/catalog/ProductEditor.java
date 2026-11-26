package md.tmps.shop.catalog;

import md.tmps.shop.domain.Product;

// ISP: catalog maintenance is a separate capability.
public interface ProductEditor {
    void save(Product product);
}
