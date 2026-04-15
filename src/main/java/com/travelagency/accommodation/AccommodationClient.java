package com.travelagency.accommodation;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser; 

public class AccommodationClient {
        private static final String API_KEY = "ff8db0013dmshf606cc85381cd61p1f2006jsned6b29229d5b"; 
        private static final String API_HOST = "xotelo-hotel-prices.p.rapidapi.com";

        public static double getNightlyRate(String hotelKey, String chkIn, String chkOut) { 
                try { 
                        String url = String.format( 
                                "https://%s/api/rates?hotel_key=%s&chk_in=%s&chk_out=%s", 
                                API_HOST, hotelKey, chkIn, chkOut 
                        ); 
                        
                        HttpRequest request = HttpRequest.newBuilder() 
                                .uri(URI.create(url)) 
                                .header("x-rapidapi-key", API_KEY) 
                                .header("x-rapidapi-host", API_HOST) 
                                .GET() 
                                .build(); 
                        
                        HttpResponse<String> response = HttpClient.newHttpClient() 
                                .send(request, HttpResponse.BodyHandlers.ofString()); 
                        
                        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject(); 
                        
                        if (json.get("error").isJsonNull()) { 
                                JsonObject result = json.getAsJsonObject("result"); 
                                if (result != null && result.has("rates")) { 
                                return result.getAsJsonArray("rates") 
                                        .get(0).getAsJsonObject() 
                                        .get("rate").getAsDouble(); 
                                } 
                        } else { 
                                System.err.println("API Error: " + json.getAsJsonObject("error").get("message").getAsString()); 
                        } 
                        
                } 
                catch (Exception e) { 
                        System.err.println("Error fetching rate for hotel key: " + hotelKey); 
                        e.printStackTrace(); 
                } 
                
                return -1;
        } 
 

}