package tqs;

import com.google.gson.Gson;

import java.util.Optional;


public class ProductFinderService {
    private final ISimpleHttpClient httpClient;
    private static final String API_PRODUCTS = "https://fakestoreapi.com/products";

    public ProductFinderService(ISimpleHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public Optional<Product> findProductDetails(Integer id) {

        String response = httpClient.doHttpGet(API_PRODUCTS + "/" + id);

        if (response.isEmpty() || response.equals("[]")) {
            return Optional.empty();
        }

        // Parse the JSON response
        try {
            Product product = new Gson().fromJson(response, Product.class);
            return Optional.of(product);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}