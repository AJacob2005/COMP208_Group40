-- 1. USERS TABLE
CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    email_verified BOOLEAN DEFAULT 0,
    registered_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. SAVED TRIPS TABLE
CREATE TABLE IF NOT EXISTS saved_trips (
    trip_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    trip_name VARCHAR(100),
    destination VARCHAR(100),
    total_price DECIMAL(10,2),
    saved_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 3. BOOKINGS TABLE 
CREATE TABLE IF NOT EXISTS bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    payment_status VARCHAR(20) DEFAULT 'UNPAID',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 4. BOOKING ITEMS TABLE (stores flights and hotels in one table)
CREATE TABLE IF NOT EXISTS booking_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    item_type VARCHAR(20) NOT NULL,
    
    -- Flight
    flight_number VARCHAR(20),
    airline VARCHAR(100),
    origin VARCHAR(10),
    destination VARCHAR(10),
    departure_date DATE,
    return_date DATE,
    flight_price DECIMAL(10,2),
    
    -- Accommodation 
    hotel_name VARCHAR(200),
    location VARCHAR(100),
    check_in_date DATE,
    check_out_date DATE,
    nightly_rate DECIMAL(10,2),
    accommodation_price DECIMAL(10,2),
    
    selected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 5. CURRENT SELECTIONS 
CREATE TABLE IF NOT EXISTS current_selections (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    item_type VARCHAR(20) NOT NULL,
    
    -- Flight data
    flight_number VARCHAR(20),
    airline VARCHAR(100),
    origin VARCHAR(10),
    destination VARCHAR(10),
    departure_date DATE,
    return_date DATE,
    flight_price DECIMAL(10,2),
    
    -- Accommodation data
    hotel_name VARCHAR(200),
    location VARCHAR(100),
    check_in_date DATE,
    check_out_date DATE,
    nightly_rate DECIMAL(10,2),
    accommodation_price DECIMAL(10,2),
    
    selected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);



-- 1. USERS
INSERT INTO users (user_id, email, password_hash, full_name, phone_number, email_verified) VALUES
('user-001', 'john.doe@example.com', 'hashed_password_123', 'John Doe', '+447123456789', 1),
('user-002', 'jane.smith@example.com', 'hashed_password_456', 'Jane Smith', '+447987654321', 1),
('user-003', 'bob.wilson@example.com', 'hashed_password_789', 'Bob Wilson', '+447556677889', 0);

-- SAVED TRIPS
INSERT INTO saved_trips (trip_id, user_id, trip_name, destination, total_price, saved_date) VALUES
('trip-001', 'user-001', 'London Getaway', 'London', 850.00, '2026-03-15 10:30:00'),
('trip-002', 'user-001', 'Paris Weekend', 'Paris', 620.00, '2026-03-20 14:45:00'),
('trip-003', 'user-002', 'New York Business', 'New York', 1250.00, '2026-03-25 09:15:00'),
('trip-004', 'user-003', 'Dubai Luxury', 'Dubai', 2100.00, '2026-04-01 16:20:00');

-- BOOKINGS
INSERT INTO bookings (user_id, total_price, status, payment_status, booking_date) VALUES
('user-001', 450.00, 'CONFIRMED', 'PAID', '2026-04-01 09:00:00'),
('user-001', 320.00, 'PENDING', 'UNPAID', '2026-04-05 14:30:00'),
('user-002', 680.00, 'CONFIRMED', 'PAID', '2026-04-02 11:15:00'),
('user-002', 950.00, 'CANCELLED', 'REFUNDED', '2026-03-28 10:00:00'),
('user-003', 500.00, 'PENDING', 'UNPAID', '2026-04-10 13:45:00');





-- Booking 1 items
INSERT INTO booking_items (booking_id, user_id, item_type, flight_number, airline, origin, destination, departure_date, return_date, flight_price) VALUES
(1, 'user-001', 'flight', 'BA1234', 'British Airways', 'LHR', 'JFK', '2026-04-20', '2026-04-27', 450.00);

INSERT INTO booking_items (booking_id, user_id, item_type, hotel_name, location, check_in_date, check_out_date, nightly_rate, accommodation_price) VALUES
(1, 'user-001', 'accommodation', 'Hilton Garden Inn', 'New York', '2026-04-20', '2026-04-27', 180.00, 1260.00);


-- Booking 2 items
INSERT INTO booking_items (booking_id, user_id, item_type, flight_number, airline, origin, destination, departure_date, return_date, flight_price) VALUES
(2, 'user-001', 'flight', 'AF5678', 'Air France', 'LHR', 'CDG', '2026-05-10', '2026-05-15', 150.00);

INSERT INTO booking_items (booking_id, user_id, item_type, hotel_name, location, check_in_date, check_out_date, nightly_rate, accommodation_price) VALUES
(2, 'user-001', 'accommodation', 'Hotel de Paris', 'Paris', '2026-05-10', '2026-05-15', 120.00, 600.00);


-- Booking 3 items
INSERT INTO booking_items (booking_id, user_id, item_type, flight_number, airline, origin, destination, departure_date, return_date, flight_price) VALUES
(3, 'user-002', 'flight', 'DL9012', 'Delta Airlines', 'LHR', 'JFK', '2026-04-25', '2026-05-02', 520.00);

INSERT INTO booking_items (booking_id, user_id, item_type, hotel_name, location, check_in_date, check_out_date, nightly_rate, accommodation_price) VALUES
(3, 'user-002', 'accommodation', 'Marriott Marquis', 'New York', '2026-04-25', '2026-05-02', 250.00, 1750.00);


-- Booking 4
INSERT INTO booking_items (booking_id, user_id, item_type, flight_number, airline, origin, destination, departure_date, return_date, flight_price) VALUES
(4, 'user-002', 'flight', 'EK3456', 'Emirates', 'LHR', 'DXB', '2026-03-30', '2026-04-06', 680.00);

INSERT INTO booking_items (booking_id, user_id, item_type, hotel_name, location, check_in_date, check_out_date, nightly_rate, accommodation_price) VALUES
(4, 'user-002', 'accommodation', 'Burj Al Arab', 'Dubai', '2026-03-30', '2026-04-06', 800.00, 5600.00);


-- 5. INSERT SAMPLE CURRENT SELECTIONS
INSERT INTO current_selections (user_id, item_type, flight_number, airline, origin, destination, departure_date, return_date, flight_price, selected_at) VALUES
('user-001', 'flight', 'LH7890', 'Lufthansa', 'LHR', 'BER', '2026-05-20', '2026-05-25', 180.00, '2026-04-14 10:00:00');

INSERT INTO current_selections (user_id, item_type, hotel_name, location, check_in_date, check_out_date, nightly_rate, accommodation_price, selected_at) VALUES
('user-001', 'accommodation', 'Adlon Kempinski', 'Berlin', '2026-05-20', '2026-05-25', 220.00, 1100.00, '2026-04-14 10:05:00');

INSERT INTO current_selections (user_id, item_type, flight_number, airline, origin, destination, departure_date, return_date, flight_price, selected_at) VALUES
('user-003', 'flight', 'VS100', 'Virgin Atlantic', 'LHR', 'LAX', '2026-06-01', '2026-06-15', 650.00, '2026-04-14 11:30:00');




/*
-- GET ALL BOOKING
SELECT 
    b.booking_id,
    b.booking_date,
    b.total_price,
    b.status,
    b.payment_status,
    bi.item_type,
    CASE 
        WHEN bi.item_type = 'flight' THEN bi.flight_number
        ELSE bi.hotel_name
    END as item_name,
    CASE 
        WHEN bi.item_type = 'flight' THEN bi.airline
        ELSE bi.location
    END as provider,
    bi.item_price
FROM bookings b
JOIN booking_items bi ON b.booking_id = bi.booking_id
WHERE b.user_id = 'user-001'
ORDER BY b.booking_date DESC;

-- GET CURRENT SELECTIONS
SELECT * FROM current_selections WHERE user_id = 'user-001' ORDER BY selected_at DESC;

-- GET BOOKING DETAILS
SELECT 
    b.booking_id,
    b.booking_date,
    b.total_price,
    b.status,
    MAX(CASE WHEN bi.item_type = 'flight' THEN bi.flight_number END) as flight_number,
    MAX(CASE WHEN bi.item_type = 'flight' THEN bi.airline END) as airline,
    MAX(CASE WHEN bi.item_type = 'flight' THEN bi.destination END) as flight_destination,
    MAX(CASE WHEN bi.item_type = 'accommodation' THEN bi.hotel_name END) as hotel_name,
    MAX(CASE WHEN bi.item_type = 'accommodation' THEN bi.location END) as hotel_location
FROM bookings b
JOIN booking_items bi ON b.booking_id = bi.booking_id
WHERE b.user_id = 'user-001'
GROUP BY b.booking_id
ORDER BY b.booking_date DESC;

-- GET TRAVEL HISTORY
SELECT 
    'booking' as type,
    b.booking_id as id,
    b.booking_date as date,
    b.total_price as price,
    b.status
FROM bookings b
WHERE b.user_id = 'user-001'
UNION ALL
SELECT 
    'saved_trip' as type,
    s.trip_id as id,
    s.saved_date as date,
    s.total_price as price,
    'SAVED' as status
FROM saved_trips s
WHERE s.user_id = 'user-001'
ORDER BY date DESC;
*/