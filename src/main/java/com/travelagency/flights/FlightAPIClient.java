package com.travelagency.flights;

import java.net.URI;
import java.net.http.*;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FlightAPIClient {

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final java.lang.reflect.Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
    private final String apiKey;

    public FlightAPIClient(String apiKey) {
        this.apiKey = apiKey;
    }

    public Map<String, Object> fetchFlights(
        String departure,
        String arrival,
        String depDate,
        String retDate,
        String adults,
        String children,
        String infants,
        String cabin,
        String currency
    ) {
        String url = String.format(
            "https://api.flightapi.io/roundtrip/%s/%s/%s/%s/%s/%s/%s/%s/%s/%s",
            apiKey, departure, arrival, depDate, retDate,
            adults, children, infants, cabin, currency
        );

        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return gson.fromJson(response.body(), mapType);
                }

                System.out.println("API error: " + response.statusCode());

            } catch (Exception e) {
                System.out.println("Attempt " + attempt + " failed: " + e.getMessage());
            }

            try {
                Thread.sleep(1000L * attempt);
            } catch (InterruptedException ignored) {}
        }

        return null;
    }
}