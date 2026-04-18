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
        hotels.add(new Accommodation("Even Hotel", "New York", 4.0, "g60763-d7778097"));
        hotels.add(new Accommodation("The Peninsula", "New York", 5.0, "g60763-d113311"));
        hotels.add(new Accommodation("The Ritz-Carlton, Central Park", "New York", 5.0, "g60763-d224224"));
        hotels.add(new Accommodation("The Plaza New York", "New York", 5.0, "g60763-d675616"));

        // California
        hotels.add(new Accommodation("SeaCrest OceanFront Hotel", "California", 3.0, "g32894-d79297"));
        hotels.add(new Accommodation("Hilton Universal City", "California", 4.0, "g32655-d78694"));
        hotels.add(new Accommodation("L'Ermitage Beverly Hills", "California", 5.0, "g32070-d76065"));
        hotels.add(new Accommodation("Claremont Resort & Club", "California", 5.0, "g32810-d80965"));
        
        // London
        hotels.add(new Accommodation("Travelodge Hotel", "London", 1.0, "g186338-d630310"));
        hotels.add(new Accommodation("Crestfield Hotel", "London", 2.0, "g186338-d263679"));
        hotels.add(new Accommodation("Hotel Motel One London-Tower Hill", "London", 3.0, "g186338-d7288418"));
        hotels.add(new Accommodation("Chesham Hotel Belgravia", "London", 3.0, "g186338-d193622"));
        hotels.add(new Accommodation("The Montague on the Gardens", "London", 4.0, "g186338-d192036"));
        hotels.add(new Accommodation("Lancaster Gate Hotel", "London", 4.0, "g186338-d195206"));
        hotels.add(new Accommodation("Strand Palace", "London", 4.0, "g186338-d193112"));
        hotels.add(new Accommodation("Hotel 41", "London", 5.0, "g186338-d188961"));
        hotels.add(new Accommodation("The Egerton House Hotel", "London", 5.0, "g186338-d192122"));
        hotels.add(new Accommodation("The Bloomsbury Hotel", "London", 5.0, "g186338-d209229"));

        // Tokyo
        hotels.add(new Accommodation("Chapter Two Tokyo", "Tokyo", 1.0, "g1066461-d13819464"));
        hotels.add(new Accommodation("The Millennials Shibuya", "Tokyo", 2.0, "g1066456-d13398497"));
        hotels.add(new Accommodation("Koko Hotel", "Tokyo", 3.0, "g1066444-d16736885"));
        hotels.add(new Accommodation("Hotel Sunroute Plaza Shinjuku", "Tokyo", 3.0, "g14133713-d320581"));
        hotels.add(new Accommodation("Miyashita Park", "Tokyo", 4.0, "g1066456-d19990197"));
        hotels.add(new Accommodation("Hotel Gracery Shinjuku", "Tokyo", 4.0, "g14133667-d6987624"));
        hotels.add(new Accommodation("Keio Plaza Hotel", "Tokyo", 4.0, "g14133673-d304305"));
        hotels.add(new Accommodation("Intercontinental The Strings", "Tokyo", 5.0, "g1066451-d300471"));
        hotels.add(new Accommodation("Four Seasons Hotel", "Tokyo", 5.0, "g14129477-d20057286"));
        hotels.add(new Accommodation("Palace Hotel Tokyo", "Tokyo", 5.0, "g14129528-d2528953"));

        // Spain
        hotels.add(new Accommodation("Hotel Gaudi", "Barcelona", 3.0, "g187497-d237157"));
        hotels.add(new Accommodation("Andante Hotel", "Barcelona", 3.0, "g187497-d2545026"));
        hotels.add(new Accommodation("Barcelo Torre de Madrid", "Madrid", 5.0, "g187514-d10847573"));
        hotels.add(new Accommodation("Hotel Urban", "Madrid", 5.0, "g187514-d296957"));
        hotels.add(new Accommodation("Eurostars Centrum", "Alicante", 4.0, "g1064230-d483592"));
        hotels.add(new Accommodation("Hotel Kramer", "Valencia", 3.0, "g187529-d503546"));

        // Italy
        hotels.add(new Accommodation("The Carlton, a Rocco Forte", "Milan", 5.0, "g187849-d34027601"));
        hotels.add(new Accommodation("Glam Hotel", "Milan", 4.0, "g187849-d8638714"));
        hotels.add(new Accommodation("Hotel Artemide", "Rome", 4.0, "g187791-d205044"));
        hotels.add(new Accommodation("Hotel Colosseum", "Rome", 3.0, "g187791-d230612"));
        hotels.add(new Accommodation("Carnival Palace Hotel", "Venice", 4.0, "g187870-d2558563"));
        hotels.add(new Accommodation("Grand Hotel Vesuvio", "Naples", 4.0, "g187782-d277289"));

        // Germany
        hotels.add(new Accommodation("Platzl Hotel", "Munich", 4.0, "g187309-d228386"));
        hotels.add(new Accommodation("NYX Hotel", "Munich", 5.0, "g187309-d13341530"));
        hotels.add(new Accommodation("Havellandhalle Resort", "Berlin", 4.0, "g187323-d3159001"));
        hotels.add(new Accommodation("Hotel Am Borsigturm", "Berlin", 4.0, "g187323-d202454"));
        hotels.add(new Accommodation("Steigenberger Icon Hof", "Frankfurt", 5.0, "g187337-d202275"));

        // Portugal
        hotels.add(new Accommodation("Corpo Santo Historical Hotel", "Lisbon", 5.0, "g189158-d12659702"));
        hotels.add(new Accommodation("Epic Sana Lisboa Hotel", "Lisbon", 5.0, "g189158-d3874679"));
        hotels.add(new Accommodation("Reid's Palace", "Madeira", 4.0, "g189167-d196072"));
        hotels.add(new Accommodation("Moov Hotel Porto Centro", "Porto", 2.0, "g189180-d2522678"));
        hotels.add(new Accommodation("Porto Mare", "Porto", 4.0, "g189167-d296287"));

        // China
        hotels.add(new Accommodation("Jw Marriott Hotel", "Beijing", 5.0, "g294212-d813649"));
        hotels.add(new Accommodation("The Purple Horse Hotel", "Beijing", 4.0, "g294212-d606463"));
        hotels.add(new Accommodation("Legendale Hotel", "Beijing", 5.0, "g294212-d1200091"));

        // Canada
        hotels.add(new Accommodation("SoHo Hotel", "Toronto", 5.0, "g155019-d259397"));
        hotels.add(new Accommodation("Novotel Toronto Centre", "Toronto", 4.0, "g155019-d183075"));
        hotels.add(new Accommodation("Life Suites Loft", "Toronto", 3.0, "g155019-d12034975"));

        // Hungary
        hotels.add(new Accommodation("Prestige Hotel", "Budapest", 4.0, "g274887-d7818402"));
        hotels.add(new Accommodation("Three Corners Hotel Anna", "Budapest", 3.0, "g274887-d286521"));

        // Czechia
        hotels.add(new Accommodation("Kings Court Deluxe Hotel", "Prague", 5.0, "g274707-d1485015"));
        hotels.add(new Accommodation("Occidental Praha", "Prague", 4.0, "g274707-d276697"));

        // Netherlands
        hotels.add(new Accommodation("Monet Garden Hotel", "Amsterdam", 4.0, "g188590-d12967879"));
        hotels.add(new Accommodation("Volkshotel", "Amsterdam", 3.0, "g188590-d6599284"));
        hotels.add(new Accommodation("Jaz in the City", "Amsterdam", 5.0, "g188590-d8095316"));

        // Ireland
        hotels.add(new Accommodation("Temple Bar Hotel", "Dublin", 4.0, "g186605-d214554"));
        hotels.add(new Accommodation("Dublin One Hotel", "Dublin", 4.0, "g186605-d24050834"));
        hotels.add(new Accommodation("Marlin Hotel Stephen's Green", "Dublin", 4.0, "g186605-d17512363"));

        // Australia
        hotels.add(new Accommodation("The Grace", "Sydney", 4.0, "g255060-d257296"));
        hotels.add(new Accommodation("Sydney Harbour Hotel", "Sydney", 4.0, "g255060-d255392"));
        hotels.add(new Accommodation("Rendezvous Hotel", "Sydney", 4.0, "g255060-d256655"));
        hotels.add(new Accommodation("The Great Southern Hotel", "Perth", 4.0, "g255103-d8433703"));
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

 
