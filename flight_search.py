import requests
import sqlite3
import sys
import time

#Docs for this api can be found in: https://docs.flightapi.io/flight-price-api/round-trip-api
# your api key can be gotten from here https://www.flightapi.io/login create a new free account you get 15 calls (30 credits each call uses 2 credits) 
# and have 30 days per account


# ── CONFIG ────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
# should just need to change the config section to get differenct airports, nuber of passegers etc

#api_key = '69dceeec4cafe1bd8835ced5'
api_key = '69df93455e1a26f2fe76fedc'
"""
# needs the airport code here
# and here - LHR for  London Heathrow Airport etc
departure_airport_code = 'LHR' 
arrival_airport_code = 'JFK'   

departure_date = '2026-04-16'
arrival_date = '2026-04-30'

# needs a 0 if there is none of a specific type
num_of_adults = '1'   
num_of_children = '0'
num_of_infants = '0'

# Possible Values- "Economy", "Business", "First" or "Premium_Economy"
cabin_class = 'Economy'   

# can use other currencies like USD etc
currency = 'GBP'"""

# name of the database that will be made
db_path = 'flights.db'
# ──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────

# Builds the URL used to call the api
def fetch_flights(
    api_key,
    departure_airport_code,
    arrival_airport_code,  
    departure_date,
    arrival_date,
    num_of_adults,   
    num_of_children,
    num_of_infants,
    cabin_class,
    currency
):
    url = (
        f"https://api.flightapi.io/roundtrip"
        f"/{api_key}"
        f"/{departure_airport_code}"
        f"/{arrival_airport_code}"
        f"/{departure_date}"
        f"/{arrival_date}"
        f"/{num_of_adults}"
        f"/{num_of_children}"
        f"/{num_of_infants}"
        f"/{cabin_class}"
        f"/{currency}"
    )
    print("URL:", url)

    max_attempts = 3
    for attempt in range(1, max_attempts + 1):
        try:
            response = requests.get(url, timeout=90)
        except Exception as e:
            print(f"Request failed (attempt {attempt}/{max_attempts}): {e}")
            if attempt == max_attempts:
                return None
            time.sleep(2 ** (attempt - 1))
            continue

        if response.status_code == 200:
            return response.json()

        print(f"Error (attempt {attempt}/{max_attempts}): {response.status_code}")
        print(response.text)
        if attempt == max_attempts:
            return None
        time.sleep(2 ** (attempt - 1))


def init_db():
    # Creates the table if it doesn't already exist, updates if it is there
    conn = sqlite3.connect(db_path)
    cur = conn.cursor()
    cur.execute("""
        CREATE TABLE IF NOT EXISTS flights (
            price                   REAL,       -- price in GBP, e.g. 769.75
            outbound_airline        TEXT,       -- airline name(s)
            outbound_departure      TEXT,       -- datetime e.g. 2026-04-16T08:30:00
            outbound_arrival        TEXT,
            outbound_stops          INTEGER,    -- number of layovers - 0 is direct, 1+ etc      
            return_airline          TEXT,
            return_departure        TEXT,
            return_arrival          TEXT,
            return_stops            INTEGER,
            origin                  TEXT,
            destination             TEXT,
            cabin_class             TEXT
        )
    """)
    conn.commit()
    conn.close()
 
 # the details of how the api work can be found here: https://docs.flightapi.io/flight-price-api/round-trip-api

def get_leg_details(leg, all_segments, all_carriers):
    """Resolves a leg's ID references into readable fields."""
    if not leg:
        return {}
 
    # The api only stores carrier IDs (e.g. -32385), not names.
    # can grab the caiirer names from the API's top-level "carriers" list,
    carrier_ids = leg.get("marketing_carrier_ids", [])
    airline = ", ".join(
        all_carriers.get(cid, {}).get("name", str(cid))
        for cid in carrier_ids
    )
 
 
    return {
        "airline":        airline,
        "departure":      leg.get("departure", ""),
        "arrival":        leg.get("arrival", ""),
        "stops":          leg.get("stop_count", 0),
    }
 
 
def save_to_db(data, params=None):
    if params is None:
        params = {}
    departure_airport_code = params.get("departure_airport_code", "UNKNOWN")
    arrival_airport_code = params.get("arrival_airport_code", "UNKNOWN")
    cabin_class = params.get("cabin_class", "Economy")

    # The API response uses IDs throughout — lookup dicts to resolve them
    all_legs     = {x["id"]: x for x in data.get("legs", [])}
    all_segments = {x["id"]: x for x in data.get("segments", [])}
    all_carriers = {x["id"]: x for x in data.get("carriers", [])}
 
    itineraries = data.get("itineraries", [])
    # Limit to first 50 results to avoid timeout
    itineraries = itineraries[:50]
    print(f"Found {len(itineraries)} itineraries (limited to 50)")
 
    conn = sqlite3.connect(db_path)
    cur = conn.cursor()
    saved = 0
 
    for flight in itineraries:
        price = flight.get("cheapest_price", {}).get("amount")
 
        # Each itinerary has 2 legs: [0] outbound, [1] return
        leg_ids = flight.get("leg_ids", [])
        outbound = get_leg_details(
            all_legs.get(leg_ids[0]) if len(leg_ids) > 0 else {},
            all_segments, all_carriers
        )
        ret = get_leg_details(
            all_legs.get(leg_ids[1]) if len(leg_ids) > 1 else {},
            all_segments, all_carriers
        )
 
        cur.execute("""
            INSERT INTO flights VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
        """, (
            price,
            outbound.get("airline"),    
            outbound.get("departure"),
            outbound.get("arrival"),    
            outbound.get("stops"),
            ret.get("airline"),         
            ret.get("departure"),
            ret.get("arrival"),         
            ret.get("stops"),
            departure_airport_code,
            arrival_airport_code,
            cabin_class
        ))
 
        # prints to terminal each row saved to database
        
        # print(f"  £{price:>8.2f} | {outbound.get('airline', '?'):30} | "
        #       f"{outbound.get('stops', '?')} stop(s) out, "
        #       f"{ret.get('stops', '?')} stop(s) back")
        saved += 1
 
    conn.commit()
    conn.close()
    
    # prints to terminal the number of rows saved to database
    print(f"\nSaved {saved} flights to {db_path}")
 
 
 
def main(
    departure_airport_code = "LHR",
    arrival_airport_code = 'JFK',  
    departure_date = '2026-04-16',
    arrival_date = '2026-04-30',
    num_of_adults = '1',   
    num_of_children = '0',
    num_of_infants = '0',
    cabin_class = 'Economy',
    currency = 'GBP'
):
    init_db()
    data = fetch_flights(
        api_key,
        departure_airport_code,
        arrival_airport_code,  
        departure_date,
        arrival_date,
        num_of_adults,   
        num_of_children,
        num_of_infants,
        cabin_class,
        currency
    )
    if not data:
        print("No data received.")
        return False
    save_to_db(data, {"departure_airport_code": departure_airport_code, "arrival_airport_code": arrival_airport_code, "cabin_class": cabin_class})

    return True
    print("Done.")

# Ensures that main() is ran only when this file is executed directly,
# not when imported as a module
if __name__ == "__main__":
    if len(sys.argv) > 1:
        success = main(
            departure_airport_code=sys.argv[1],
            arrival_airport_code=sys.argv[2],
            departure_date=sys.argv[3],
            arrival_date=sys.argv[4],
            num_of_adults=sys.argv[5],
            num_of_children=sys.argv[6],
            num_of_infants=sys.argv[7],
            cabin_class=sys.argv[8],
            currency=sys.argv[9],
        )
    else:
        success = main()
    print(success)

#For this to run in the another file just import it, call it and check if it returned true or false
#      import flight_search
#      success = flight_search.main() # executes main and stores the resulting bool 

 