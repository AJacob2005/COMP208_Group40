package com.travelagency.accommodation;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/accommodation")
public class AccommodationController {

    @PostMapping("/search")
    public List<Map<String, Object>> search(@RequestBody Map<String, Object> data) throws Exception {
        AccommodationFilter filter = new AccommodationFilter();
        filter.setLocation((String) data.get("location"));
        filter.setCheckIn(LocalDate.parse((String) data.get("checkIn")));
        filter.setCheckOut(LocalDate.parse((String) data.get("checkOut")));

        int guests = ((Number) data.getOrDefault("guests", 1)).intValue();

        AccommodationService service = new AccommodationService();
        List<Accommodation> hotels = service.searchAccommodations(filter);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Accommodation hotel : hotels) {
            double nightlyRate = AccommodationClient.getNightlyRate(
                    hotel.getHotelKey(),
                    filter.getCheckIn().toString(),
                    filter.getCheckOut().toString()
            );
            Map<String, Object> h = new HashMap<>();
            h.put("name",        hotel.getName());
            h.put("location",    hotel.getLocation());
            h.put("rating",      hotel.getRating());
            h.put("hotelKey",    hotel.getHotelKey());
            h.put("nightlyRate", nightlyRate);
            h.put("totalPrice",  nightlyRate * guests);
            result.add(h);
        }
        return result;
    }
}