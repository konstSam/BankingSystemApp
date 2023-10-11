package BankingSystemApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class Converter {
    public static BigDecimal[] getExchangeRates(BigDecimal amount, String sourceCurrency, String targetCurrency,
            Scanner scanner) {
        // Set the API key
        String apiKey = "8da49a1d86d663e01bdd15d8";

        // Define the API endpoint URL
        String apiUrl = "https://v6.exchangerate-api.com/v6/" + apiKey + "/pair/" + sourceCurrency + "/"
                + targetCurrency + "/" + amount;

        StringBuilder jsonresponse = apiCall(apiUrl);

        // extract data from json
        Gson gson = new Gson();
        TypeToken<Map<String, String>> mapType = new TypeToken<Map<String, String>>() {
        };

        Map<String, String> ratesJson = gson.fromJson(jsonresponse.toString(), mapType);
        BigDecimal exchangeRate = new BigDecimal(ratesJson.get("conversion_rate"));
        BigDecimal conversionResult = new BigDecimal(ratesJson.get("conversion_result"));

        BigDecimal rates[] = new BigDecimal[2];
        rates[0] = exchangeRate;
        rates[1] = conversionResult;

        return rates;
    }

    public static String validateCurrency(Scanner scanner) {
        String apiKey = "8da49a1d86d663e01bdd15d8";

        // Define the API endpoint URL
        String apiURL = "https://v6.exchangerate-api.com/v6/" + apiKey + "/codes";
        StringBuilder jsonresponse = apiCall(apiURL);

        String jsonString = jsonresponse.toString();

        // extract data from json
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        JsonArray supportedCodesArray = jsonObject.getAsJsonArray("supported_codes");

        boolean currencyExists = false;
        List<String> supportedCurrencyCodes = new ArrayList<>();
        while (!currencyExists) {
            // Convert to uppercase for case-insensitive check
            String userCurrency = scanner.nextLine().toUpperCase();

            for (int i = 0; i < supportedCodesArray.size(); i++) {
                JsonArray currencyCode = supportedCodesArray.get(i).getAsJsonArray();
                supportedCurrencyCodes.add(currencyCode.get(0).getAsString());
                if (supportedCurrencyCodes.contains(userCurrency)) {
                    System.out.println(userCurrency + " is valid currency code.");
                    currencyExists = true;

                    return userCurrency;
                }
            }
            if (!currencyExists) {
                System.out.println("Invalid currency code. Please try again.");
            }

        }

        return null;
    }

    public static StringBuilder apiCall(String apiURL) {
        try {
            URL url = new URL(apiURL);
            // Create a URL object and open a connection to the API
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

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

                return response;

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

}
