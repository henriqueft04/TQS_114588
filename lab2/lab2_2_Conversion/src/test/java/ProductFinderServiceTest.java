// Test class with Mockito
import org.junit.jupiter.api.Test;
import tqs.ISimpleHttpClient;
import tqs.Product;
import tqs.ProductFinderService;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProductFinderServiceTest {

    @Test
    void testFindProductDetails() {
        ISimpleHttpClient mockHttpClient = mock(ISimpleHttpClient.class);

        String mockResponse = "{\"id\": 3, \"image\": \"some_image_url\", \"description\": \"A cotton jacket\", \"price\": 29.99, \"title\": \"Mens Cotton Jacket\", \"category\": \"clothing\"}";
        when(mockHttpClient.doHttpGet(anyString())).thenReturn(mockResponse);

        ProductFinderService service = new ProductFinderService(mockHttpClient);

        Optional<Product> product = service.findProductDetails(3);

        assertTrue(product.isPresent());
        assertEquals(3, product.get().getId());
        assertEquals("Mens Cotton Jacket", product.get().getTitle());
    }

    @Test
    void testFindProductDetailsNoProduct() {
        ISimpleHttpClient mockHttpClient = mock(ISimpleHttpClient.class);

        when(mockHttpClient.doHttpGet(anyString())).thenReturn("[]"); // Empty response for no product

        ProductFinderService service = new ProductFinderService(mockHttpClient);

        Optional<Product> product = service.findProductDetails(300);

        assertFalse(product.isPresent());
    }

}
