package md.tmps.shop;

import md.tmps.shop.Product;

// ISP: catalog maintenance is a separate capability.
public interface ProductEditor {
    void save(Product product);
}
