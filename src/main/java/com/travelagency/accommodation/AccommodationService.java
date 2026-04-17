package com.travelagency.accommodation; 

import java.util.ArrayList; import java.util.List; 

public class AccommodationService { 

    private List<Accommodation> hotels; 
    
    public AccommodationService() { 
        hotels = new ArrayList<>();
        hotels.add(new Accommodation("Faithlegg House Hotel", "Waterford", 4.0, "g3662182-d276042"));

        // Paris
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

        // New York
        hotels.add(new Accommodation("U.S Pacific Hotel", "New York", 1.0, "g60763-d8722421"));
        hotels.add(new Accommodation("Americana Inn", "New York", 2.0, "g60763-d121981"));
        hotels.add(new Accommodation("Holiday Inn New York City", "New York", 3.0, "g60763-d1234559"));
        hotels.add(new Accommodation("The Cloud One Hotel", "New York", 3.0, "g60763-d24075420"));
        hotels.add(new Accommodation("Hotel Indigo Lower East Side", "New York", 4.0, "g60763-d7352347"));
        hotels.add(new Accommodation("Hotel Riu Plaza Manhatten Times Square", "New York", 4.0, "g60763-d23124360"));
        hotels.add(new Accommodation("Even Hotel", "New York", 4.0, "g60763-d7778097-"));
        hotels.add(new Accommodation("The Peninsula", "New York", 5.0, "g60763-d113311"));
        hotels.add(new Accommodation("The Ritz-Carlton, Central Park", "New York", 5.0, "g60763-d224224"));
        hotels.add(new Accommodation("The Plaza New York", "New York", 5.0, "g60763-d675616"));

        // London
        hotels.add(new Accommodation("Travelodge Hotel", "London", 1.0, "g186338-d630310"));
        hotels.add(new Accommodation("Crestfield Hotel", "London", 2.0, "g186338-d263679"));
        hotels.add(new Accommodation("Hotel Motel One London-Tower Hill", "London", 3.0, "g186338-d7288418"));
        hotels.add(new Accommodation("Chesham Hotel Belgravia", "London", 3.0, "g186338-d193622"));
        hotels.add(new Accommodation("The Monague on the Gardens", "London", 4.0, "g186338-d192036"));
        hotels.add(new Accommodation("Lancaster Gate Hotel", "London", 4.0, "g186338-d195206"));
        hotels.add(new Accommodation("Strand Palace", "London", 4.0, "g186338-d193112"));
        hotels.add(new Accommodation("Hotel 41", "London", 5.0, "g186338-d188961"));
        hotels.add(new Accommodation("The Egerton House Hotel", "London", 5.0, "g186338-d192122"));
        hotels.add(new Accommodation("The Bloomsbury Hotel", "London", 5.0, "g186338-d209229"));
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

 
