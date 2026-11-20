package md.tmps.shop.catalog;

import md.tmps.shop.domain.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LazyProductCatalogTest {
    private final Product cable = new Product("P3", "USB-C cable", new BigDecimal("120.00"));
    private final ProductCatalog subject = new ProductCatalog() {
        @Override
        public List<Product> findAll() {
            return List.of(cable);
        }

        @Override
        public Optional<Product> findById(String id) {
            return findAll().stream().filter(product -> product.id().equals(id)).findFirst();
        }
    };

    @Test
    void loadsOnFirstLookupAndReusesTheCatalog() {
        AtomicInteger loads = new AtomicInteger();
        ProductCatalog proxy = new LazyProductCatalog(() -> {
            loads.incrementAndGet();
            return subject;
        });

        assertEquals(0, loads.get());
        proxy.findById("P3");
        assertEquals(1, loads.get());
        proxy.findAll();
        proxy.findById("missing");
        assertEquals(1, loads.get());
    }

    @Test
    void forwardsListingAndLookupResults() {
        ProductCatalog proxy = new LazyProductCatalog(() -> subject);

        assertEquals(subject.findAll(), proxy.findAll());
        assertSame(cable, proxy.findById("P3").orElseThrow());
        assertTrue(proxy.findById("missing").isEmpty());
    }
}
