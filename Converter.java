package BankingSystemApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import BankingSystemApp.CustomExceptions.CurrencyNotSupportedException;

public class Converter {
    public static BigDecimal[] getExchangeRates(BigDecimal amount, String sourceCurrency, String targetCurrency,
            Scanner scanner) {
        // Set the API key
        String apiKey = "8da49a1d86d663e01bdd15d8";

        // Define the API endpoint URL
        String apiUrl = "https://v6.exchangerate-api.com/v6/" + apiKey + "/pair/" + sourceCurrency + "/"
                + targetCurrency + "/" + amount;

        Map<String, String> ratesJson = apiConnection(apiUrl);
        BigDecimal exchangeRate = new BigDecimal(ratesJson.get("conversion_rate"));
        BigDecimal conversionResult = new BigDecimal(ratesJson.get("conversion_result"));

        BigDecimal rates[] = new BigDecimal[2];
        rates[0] = exchangeRate;
        rates[1] = conversionResult;

        return rates;
    }

    public static Map<String, String> apiConnection(String apiUrl) {
        try {
            // Create a URL object and open a connection to the API
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            // connection.setRequestMethod("GET");
            // connection.setRequestProperty("apikey", apiKey);

            // Get the response code e.g. 200 ok
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response from the API
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // extract data from json
                Gson gson = new Gson();
                TypeToken<Map<String, String>> mapType = new TypeToken<Map<String, String>>() {
                };
                Map<String, String> stringMap = gson.fromJson(response.toString(), mapType);

                return stringMap;

            } else {
                System.out.println("API request failed with response code: " + responseCode);
            }

            // Close the connection
            connection.disconnect();

        } catch (IOException e) {
            System.out.println("Error while making API request: " + e.getMessage());
        }
        return null;
    }

    public static Map<String, String> fetchExchangeRateAPI(String sourceCurrency, String targetCurrency) {
        String apiKey = "8da49a1d86d663e01bdd15d8";

        // Define the API endpoint URL
        String apiUrl = "https://v6.exchangerate-api.com/v6/" + apiKey + "/pair/" + sourceCurrency + "/"
                + targetCurrency + "/";

        Map<String, String> ratesJson = apiConnection(apiUrl);
        return ratesJson;
    }

    public static boolean isCurrencySupported(String sourceCurrency, String targetCurrency)
            throws CurrencyNotSupportedException {
        // Fetch the API response
        Map<String, String> apiResponse = fetchExchangeRateAPI(sourceCurrency, targetCurrency);

        // Check if the target currency exists in the Map
        if (apiResponse.get("result").equals("error")) {
            throw new CurrencyNotSupportedException("Currency " + targetCurrency + " is not supported.");
        }

        // If the currency is found in the Map, it is supported
        return true;
    }
}