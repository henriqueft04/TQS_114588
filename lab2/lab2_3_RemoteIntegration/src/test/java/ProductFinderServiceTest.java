// Test class with Mockito

import org.testng.annotations.Test;
import tqs.IAsyncHttpClient;
import tqs.Product;
import tqs.ProductFinderService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ProductFinderServiceTest {

    @Test
    void testFindProductDetails() {
        IAsyncHttpClient mockHttpClient = mock(IAsyncHttpClient.class);

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
        IAsyncHttpClient mockHttpClient = mock(IAsyncHttpClient.class);

        when(mockHttpClient.doHttpGet(anyString())).thenReturn("[]"); // Empty response for no product

        ProductFinderService service = new ProductFinderService(mockHttpClient);

        Optional<Product> product = service.findProductDetails(300);

        assertFalse(product.isPresent());
    }

}
