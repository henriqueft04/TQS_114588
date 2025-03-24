package tqs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SimpleHttpClient implements IAsyncHttpClient {

    @Override
    public String doHttpGet(String url) {
        StringBuilder response = new StringBuilder();
        try {
            // Create a URL object from the given URL string
            URL urlObj = new URL(url);
            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);  // Set connection timeout
            connection.setReadTimeout(5000);     // Set read timeout

            // Check if the request was successful
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {  // Success
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } else {
                throw new RuntimeException("HTTP GET request failed with code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";  // Return empty string in case of error
        }

        return response.toString();
    }
}
