package tqs;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Optional;

class ProductFinderServiceIT {

    @Test
    void testFindProductDetailsValidProduct() throws IOException {
        ISimpleHttpClient realHttpClient = new SimpleHttpClient();

        ProductFinderService service = new ProductFinderService(realHttpClient);

        Optional<Product> product = service.findProductDetails(3);

        assertTrue(product.isPresent(), "Product should be present but was not.");
        assertEquals(3, product.get().getId(), "Product ID should be 3.");
        assertEquals("Mens Cotton Jacket", product.get().getTitle(), "Product title should be 'Mens Cotton Jacket'.");
    }

    @Test
    void testFindProductDetailsNoProduct() throws IOException {
        ISimpleHttpClient realHttpClient = new SimpleHttpClient();

        ProductFinderService service = new ProductFinderService(realHttpClient);

        Optional<Product> product = service.findProductDetails(300);

        assertFalse(product.isPresent(), "Product should not be present.");
    }
}
