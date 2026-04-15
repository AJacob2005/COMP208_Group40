package com.travelagency.accommodation; 

import java.util.ArrayList; import java.util.List; 

public class AccommodationService { 

    private List<Accommodation> hotels; 
    
    public AccommodationService() { 
        hotels = new ArrayList<>();
        hotels.add(new Accommodation("Faithlegg House Hotel", "Waterford", 4.0, "g3662182-d276042"));

        hotels.add(new Accommodation("Esmeralda Hotel", "Paris", 1.0, "g187147-d235558"));
        hotels.add(new Accommodation("Hotel Le Clement", "Paris", 2.0, "g187147-d197436"));
        hotels.add(new Accommodation("Grand Hotel Malher", "Paris", 3.0, "g187147-d189693"));
        hotels.add(new Accommodation("Hotel George - Asotel", "Paris", 3.0, "g187147-d234640"));
        hotels.add(new Accommodation("Hotel Bonsoir Madame", "Paris", 4.0, "g187147-d258368"));
        hotels.add(new Accommodation("Hôtel des Grands Voyageurs", "Paris", 4.0, "g187147-d207688"));
        hotels.add(new Accommodation("Citadines Les Halles Paris", "Paris", 4.0, "g187147-d197426"));
        hotels.add(new Accommodation("The One Alma Paris", "Paris", 5.0, "g187147-d7593552"));
        hotels.add(new Accommodation("Le Royal Monceau - Raffles Paris", "Paris", 5.0, "g187147-d197528"));
        hotels.add(new Accommodation("Castille Paris – Starhotels Collezione", "Paris", 5.0, "g187147-d188739"));
    } 
        
    public List<Accommodation> searchAccommodations(AccommodationFilter filter) { 
        List<Accommodation> result = new ArrayList<>(); 
        for (Accommodation hotel : hotels) { 
            if (hotel.getLocation().equalsIgnoreCase(filter.getLocation()) && 
                    hotel.getRating() >= filter.getMinRating()) { 
                result.add(hotel); 
            } 
        } 
        return result; 
    }

    public double fetchNightlyRate(Accommodation hotel, String chkIn, String chkOut) { 
    return AccommodationClient.getNightlyRate(hotel.getHotelKey(), chkIn, chkOut); 
    }
} 

 