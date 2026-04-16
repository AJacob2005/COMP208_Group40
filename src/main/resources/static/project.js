//const API = "http://localhost:80";


const EXEMPT_PAGES = ['account.html'];
const currentPage = window.location.pathname.split('/').pop() || 'index.html';
if (!EXEMPT_PAGES.includes(currentPage) && !localStorage.getItem('sessionToken')) {
    window.location.href = 'account.html';
}


console.log("project.js loaded with safety checks");
const toggle = document.getElementById("menu-toggle");
const mobileLinks = document.getElementById("nav-links-mobile");

if (toggle && mobileLinks) {
  toggle.addEventListener("click", () => {
    mobileLinks.classList.toggle("active");
  });


  const links = mobileLinks.querySelectorAll("a");
  links.forEach(link => {
    link.addEventListener("click", () => {
      mobileLinks.classList.remove("active");
    });
  });
}


document.querySelectorAll('.dropdown-filter-button').forEach(button => {
    button.addEventListener('click', (e) => {
        e.stopPropagation();
        const dropdown = button.parentElement;
        dropdown.classList.toggle('show');
    });
});

window.addEventListener('click', function(e) {
    document.querySelectorAll('.dropdown-filter').forEach(drop => {
        if (!drop.contains(e.target)) {
            drop.classList.remove('show');
        }
    });
});








// Account form handlers
function showLogin() {
    document.getElementById("login-form").style.display = "block";
    document.getElementById("create-form").style.display = "none";
}

function showCreate() {
    document.getElementById("login-form").style.display = "none";
    document.getElementById("create-form").style.display = "block";
}

const loginForm = document.getElementById("login-form");
if (loginForm) {
    loginForm.addEventListener("submit", async (e) => {
        console.log("Login form submitted");
        e.preventDefault();
        const inputs = e.target.querySelectorAll("input");
        console.log("Login inputs:", inputs);
        const email = inputs[0].value;
        const password = inputs[1].value;
        
        try {
            const res = await fetch("/api/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password })
            });
            const data = await res.json();
            console.log("Login response:", data);
            alert(data.message);
            if (data.success) {
                    localStorage.setItem('sessionToken', data.sessionToken);
                    localStorage.setItem('userId', data.userId);
                    localStorage.setItem('fullName', data.fullName);
                window.location.href = "index.html";
            }
        } catch (err) {
            console.error("Login error:", err);
            alert("Error: " + err.message);
        }
    });
}

const createForm = document.getElementById("create-form");
if (createForm) {
    createForm.addEventListener("submit", async (e) => {
        console.log("Create form submitted");
        e.preventDefault();
        const inputs = e.target.querySelectorAll("input");
        console.log("Create inputs:", inputs);
        const fullName = inputs[0].value;
        const email = inputs[1].value;
        const password = inputs[2].value;
        const confirmPassword = inputs[3].value;
        const phoneNumber = inputs[4].value;
        
        if (password !== confirmPassword) {
            alert("Passwords do not match");
            return;
        }
        
        try {
            const res = await fetch("/api/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password, fullName, phoneNumber })
            });
            const data = await res.json();
            console.log("Registration response:", data);
            alert(data.message);
            if (data.success) {
                showLogin(); // Switch to login form
            }
        } catch (err) {
            console.error("Registration error:", err);
            alert("Error: " + err.message);
        }
    });
} 




const cardOption = document.getElementById("card");
const paypalOption = document.getElementById("paypal");
const bankOption = document.getElementById("bankTransfer");

const cardDetails = document.getElementById("cardDetails");

function updatePaymentDisplay() {
    const paypalDetails = document.getElementById('paypalDetails');
    const bankDetails   = document.getElementById('bankDetails');
    if (cardDetails)   cardDetails.style.display   = (cardOption   && cardOption.checked)   ? 'flex' : 'none';
    if (paypalDetails) paypalDetails.style.display = (paypalOption && paypalOption.checked) ? 'flex' : 'none';
    if (bankDetails)   bankDetails.style.display   = (bankOption   && bankOption.checked)   ? 'flex' : 'none';
}

if (cardOption) cardOption.addEventListener("change", updatePaymentDisplay);
if (paypalOption) paypalOption.addEventListener("change", updatePaymentDisplay);
if (bankOption) bankOption.addEventListener("change", updatePaymentDisplay);

updatePaymentDisplay();

// Payment form handling
const paymentForm = document.querySelector('.payment_process');
if (paymentForm) {
    paymentForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const formData = new FormData(paymentForm);
        const paymentMethod = formData.get('paymentMethod');

        if (!paymentMethod) {
            alert('Please select a payment method.');
            return;
        }

        const data = {
            paymentMethod: paymentMethod,
            userId: localStorage.getItem('userId') || 'user123',
            amount: Number(localStorage.getItem('bookingTotal') || 0),
            bookingId: localStorage.getItem('bookingId') || ''
        };

        if (paymentMethod === 'card') {
            data.cardName   = formData.get('cardName');
            data.cardNumber = formData.get('cardNumber');
            data.expiryDate = formData.get('expiryDate');
            data.cvv        = formData.get('cvv');
        } else if (paymentMethod === 'paypal') {
            data.paypalEmail = formData.get('paypalEmail');
        } else if (paymentMethod === 'bank') {
            data.bankName = formData.get('bankName');
        }
        
        try {
            const response = await fetch('/api/payment/process', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });
            
            const result = await response.json();
            
            if (response.ok && result.success) {
                const isBankTransfer = paymentMethod === 'bank';
                const receipt = {
                    id: result.transactionId || ('TXN-' + Date.now()),
                    bookingId: data.bookingId,
                    amount: '\u00a3' + Number(data.amount).toFixed(2),
                    date: new Date().toLocaleDateString('en-GB', { dateStyle: 'long' }),
                    status: isBankTransfer ? 'PENDING' : 'PAID',
                    method: result.paymentMethod || paymentMethod,
                    authCode: result.authCode || (result.transactionId ? result.transactionId.slice(-6).toUpperCase() : 'N/A'),
                    bankReference: isBankTransfer ? result.message : null
                };
                document.getElementById('paymentForm').style.display = 'none';
                const statusEl = document.getElementById('paymentStatus');
                if (statusEl) {
                    statusEl.style.cssText = 'color:green;font-weight:bold;margin-bottom:1rem;';
                    statusEl.textContent = isBankTransfer
                        ? 'Bank transfer initiated! Please use the reference below to complete your payment.'
                        : 'Payment confirmed! Your booking is confirmed.';
                }
                localStorage.removeItem('bookingId');
                localStorage.removeItem('bookingTotal');
                showReceipt(receipt);
            } else {
                alert('Payment failed: ' + (result.message || 'Unknown error'));
            }
        } catch (error) {
            console.error('Payment error:', error);
            alert('Payment failed: Network error');
        }
    });
}

function showReceipt(receipt) {
    const receiptContent = document.getElementById('receiptContent');
    const receiptSpace   = document.getElementById('receiptSpace');
    if (!receiptContent) return;

    receiptContent.innerHTML = `
        <p><strong>Transaction ID:</strong> ${receipt.id}</p>
        <p><strong>Booking ID:</strong>     ${receipt.bookingId || 'N/A'}</p>
        <p><strong>Amount:</strong>         ${receipt.amount}</p>
        <p><strong>Date:</strong>           ${receipt.date}</p>
        <p><strong>Status:</strong>         ${receipt.status}</p>
        <p><strong>Payment Method:</strong> ${receipt.method}</p>
        <p><strong>Auth Code:</strong>      ${receipt.authCode}</p>
        ${receipt.bankReference ? `<p style="color:#c00;font-weight:bold;">Bank Reference: ${receipt.bankReference}</p>` : ''}
    `;

    if (receiptSpace) {
        receiptSpace.style.display = 'block';
        receiptSpace.scrollIntoView({ behavior: 'smooth' });
    }

    const downloadBtn = document.getElementById('downloadReceipt');
    if (downloadBtn) {
        downloadBtn.onclick = () => downloadReceipt(receipt);
    }
}

function downloadReceipt(receipt) {
    const blob = new Blob([JSON.stringify(receipt, null, 2)], { type: "application/json" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `receipt_${receipt.id}.json`;
    a.click();
    URL.revokeObjectURL(url);
}

document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('comparison-grid')) {
        loadBookingPage();
    }


    const summaryElement = document.getElementById('summary');
    if (summaryElement) {
        const total = localStorage.getItem('bookingTotal');
        const bookingId = localStorage.getItem('bookingId');
        summaryElement.textContent = (total && bookingId)
            ? 'Total: \u00a3' + Number(total).toFixed(2)
            : 'Total: £0.00';
    }
});














function conversion(price, currency) {
    if (currency == "USD") return price * 1.25;
    if (currency == "EUR") return price * 1.15;
    return price;
}

async function searchHotels() {
    console.log("searchHotels() fired");


    const location = document.getElementById("location").value;
    const checkIn = document.getElementById("checkIn").value;
    const checkOut = document.getElementById("checkOut").value;
    const guests = document.getElementById("adults").value;
    const minRating = document.getElementById("minRating").value;
    const currency = document.getElementById("currency").value;

    const requestBody = {
        location: location,
        checkIn: checkIn,
        checkOut: checkOut,
        guests: Number(guests),
        minRating: Number(minRating),
        currency: currency
    };

    console.log("sending request", requestBody);

    try {
        const response = await fetch("/api/accommodation/search", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestBody)
        });

        if (!response.ok) {
            throw new Error("Server error: " + response.status);
        }

        const data = await response.json();
        console.log("data received");
        displayResults(data);

    }
    catch (error) {
        console.error("Error fetching hotels:", error);
    }
}

function displayResults(hotels) {
    const resultsDiv = document.getElementById("results");
    resultsDiv.innerHTML = "";

    const validHotels = (hotels || []).filter(h => h.totalPrice > 0 && h.nightlyRate > 0);

    if (validHotels.length === 0) {
        resultsDiv.innerHTML = "<p>No hotels found</p>";
        return;
    }

    let html = `<div class="hotel-cards-container">`;

    validHotels.forEach(hotel => {
        html += `
            <div class="hotel-card">
                <div class="hotel-info">
                    <div class="hotel-name">${hotel.name}</div>
                    <div class="hotel-location">📍 ${hotel.location}</div>
                    <div class="hotel-meta">
                        <span class="hotel-rating">⭐ ${hotel.rating}</span>
                        <span class="hotel-guests">👤 ${document.getElementById("adults").value} guests</span>
                    </div>
                </div>
                <div class="hotel-action">
                    <div class="hotel-price">£${conversion(hotel.totalPrice, document.getElementById("currency").value).toFixed(2)}</div>
                    <div class="price-sub">total stay</div>
                    <button class="select-btn" onclick="selectHotel(
                        '${hotel.hotelKey}',
                        '${hotel.name}',
                        ${hotel.nightlyRate},
                        ${conversion(hotel.totalPrice, document.getElementById("currency").value).toFixed(2)}
                    )">
                        Select
                    </button>
                </div>
            </div>
        `;
    });

    html += `</div>`;
    resultsDiv.innerHTML = html;
}





function formatDateTime(str) {
    const [datePart, timePart] = str.split('T');
    const time = timePart.slice(0,5);
    const [y,m,d] = datePart.split('-');
    return {
        date: `${d}/${m}/${y}`,
        time: time
    };
}
function renderFlights(flights) {
    const resultsDiv = document.getElementById('results');
    if (!resultsDiv) return;

    resultsDiv.innerHTML = '';
    currentFlightResults = flights || [];

    if (!flights || flights.length === 0) {
        resultsDiv.innerHTML = '<p>No flights found</p>';
        return;
    }

    let html = `
        <table class="flight-results-table">
            <thead>
                <tr>
                    <th>Price</th>
                    <th>Leg</th>
                    <th>Airline</th>
                    <th>Departure</th>
                    <th>Arrival</th>
                    <th>Stops</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
    `;
    flights.forEach((flight, index) => {
        const outDep = formatDateTime(flight.outboundDeparture);
        const outArr = formatDateTime(flight.outboundArrival);
        const retDep = formatDateTime(flight.returnDeparture);
        const retArr = formatDateTime(flight.returnArrival);
        html += `
            <tr class="flight-main-row">
                <td class="price-cell" rowspan="2">
                    £${flight.price ?? ''}
                </td>
                <td class="leg-cell">
                    <span class="badge outbound">Outbound</span>
                </td>
                <td class="airline-cell">
                    ${flight.outboundAirline || ''}
                </td>
                <td class="time-cell">
                    ${outDep.date || ''} ${outDep.time || ''}
                </td>
                <td class="time-cell">
                    ${outArr.date || ''} ${outArr.time || ''}
                </td>
                <td class="stops-cell">
                    ${flight.outboundStops ?? ''}
                </td>
                <td class="action-cell" rowspan="2">
                    <button class="select-btn" onclick="selectFlight(${index})">
                        Select
                    </button>
                </td>
            </tr>
            <tr class="flight-sub-row">
                <td class="leg-cell">
                    <span class="badge return">Return</span>
                </td>
                <td class="airline-cell">
                    ${flight.returnAirline || ''}
                </td>
                <td class="time-cell">
                    ${retDep.date || ''} ${retDep.time || ''}
                </td>
                <td class="time-cell">
                    ${retArr.date || ''} ${retArr.time || ''}
                </td>
                <td class="stops-cell">
                    ${flight.returnStops ?? ''}
                </td>
            </tr>
        `;
    });
    html += `
            </tbody>
        </table>
    `;
    resultsDiv.innerHTML = html;
}

function selectFlight(index) {
    const flight = currentFlightResults[index];
    if (!flight) return;
    const selectedFlight = {
        price: flight.price,
        outboundAirline: flight.outboundAirline,
        outboundDeparture: flight.outboundDeparture,
        outboundArrival: flight.outboundArrival,
        outboundStops: flight.outboundStops,
        returnAirline: flight.returnAirline,
        returnDeparture: flight.returnDeparture,
        returnArrival: flight.returnArrival,
        returnStops: flight.returnStops,
        origin: flight.origin || '',
        destination: flight.destination || ''
    };
    localStorage.setItem('pendingFlight', JSON.stringify(selectedFlight));
    alert('Flight saved! Now select a hotel to complete your trip, or visit Compare Bookings.');
    checkBothSelected();
}

function selectHotel(key, name, nightlyRate, totalPrice) {

    const selected = {
        hotelKey: key,
        name: name,
        nightlyRate: nightlyRate,
        totalPrice: totalPrice
    };

    localStorage.setItem('pendingHotel', JSON.stringify(selected));
    alert('Hotel saved! Now select a flight to complete your trip, or visit Compare Bookings.');
    checkBothSelected();
}






let currentFlightResults = [];

async function searchFlights(event) {
    event.preventDefault();
    const resultsDiv = document.getElementById("results");
    if (resultsDiv) {
        resultsDiv.innerText = 'Loading flights...';
    }

    const flightData = {
        origin: document.getElementById("origin").value,
        destination: document.getElementById("destination").value,
        departureDate: document.getElementById("departureDate").value,
        returnDate: document.getElementById("returnDate").value,
        adults: document.getElementById("adults").value,
        children: 0,
        infants: 0,
        cabin: document.getElementById("cabin").value,
        currency: document.getElementById("currency").value
    };

    try {
        const response = await fetch("/api/flights", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(flightData)
        });

        if (!response.ok) throw new Error(`Server error: ${response.status}`);
        const flights = await response.json();
        renderFlights(flights);
    }
    catch (err) {
        console.error(err);
        const resultsDiv = document.getElementById("results");
        if (resultsDiv) {
            resultsDiv.innerText = "Failed to fetch flights.";
        }
    }
}




// Booking
function checkBothSelected() {
    const flight = localStorage.getItem('pendingFlight');
    const hotel  = localStorage.getItem('pendingHotel');
    if (flight && hotel) {
        if (confirm('You\'ve selected both a flight and a hotel! Go to Compare Bookings to add this trip?')) {
            window.location.href = 'booking_page.html';
        }
    }
}
function removeSelection(type){
    if (type == 'flight') localStorage.removeItem('pendingFlight');
    if (type === 'hotel')  localStorage.removeItem('pendingHotel');
    loadBookingPage();
}

function addTripToComparison() {
    const flight = JSON.parse(localStorage.getItem('pendingFlight') || 'null');
    const hotel  = JSON.parse(localStorage.getItem('pendingHotel')  || 'null');

    if (!flight && !hotel) {
        alert('Select a flight and/or hotel first before adding a trip.');
        return;
    }

    const statusEl = document.getElementById('booking-status');
    if (statusEl) statusEl.textContent = 'Saving trip…';

    const tripsKey = 'tripComparisons_' + (localStorage.getItem('userId') || 'guest');
    fetch('/api/bookings', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            userId: localStorage.getItem('userId') || 'guest',
            flight: flight,
            hotel: hotel
        })
    })
    .then(r => r.json())
    .then(data => {
        if (!data.success) {
            if (statusEl) statusEl.textContent = 'Error: ' + (data.message || 'Unknown');
            return;
        }
        const trips = JSON.parse(localStorage.getItem(tripsKey) || '[]');
        trips.push({ id: data.bookingId, flight, hotel, totalPrice: data.totalPrice });
        localStorage.setItem(tripsKey, JSON.stringify(trips));
        localStorage.removeItem('pendingFlight');
        localStorage.removeItem('pendingHotel');
        if (statusEl) statusEl.textContent = '';
        loadBookingPage();
    })
    .catch(err => {
        if (statusEl) statusEl.textContent = 'Network error. Please try again.';
        console.error(err);
    });
}

function removeTrip(id) {
    const tripsKey = 'tripComparisons_' + (localStorage.getItem('userId') || 'guest');
    let trips = JSON.parse(localStorage.getItem(tripsKey) || '[]');
    trips = trips.filter(t => t.id !== id);
    localStorage.setItem(tripsKey, JSON.stringify(trips));
    loadBookingPage();
}

function selectTrip(id) {
    const tripsKey = 'tripComparisons_' + (localStorage.getItem('userId') || 'guest');
    const trips = JSON.parse(localStorage.getItem(tripsKey) || '[]');
    const trip = trips.find(t => t.id == id);
    if (!trip) {
        return;
    }
    localStorage.setItem('bookingId', trip.id);
    localStorage.setItem('bookingTotal', trip.totalPrice);
    window.location.href = 'payment.html';
}

function loadBookingPage() {
    const pendingFlightEl = document.getElementById('pending-flight-display');
    const pendingHotelEl  = document.getElementById('pending-hotel-display');
    const pendingFlight   = JSON.parse(localStorage.getItem('pendingFlight') || 'null');
    const pendingHotel    = JSON.parse(localStorage.getItem('pendingHotel')  || 'null');

    if (pendingFlightEl) {
        if (pendingFlight) {
            const outDep = formatDateTime(pendingFlight.outboundDeparture);
            const retArr = formatDateTime(pendingFlight.returnArrival);
            pendingFlightEl.innerHTML = `
                <p><strong>Flight Outbound:</strong> ${pendingFlight.outboundAirline} | ${pendingFlight.origin} → ${pendingFlight.destination} | ${outDep.date} ${outDep.time} (${pendingFlight.outboundStops} stop${pendingFlight.outboundStops === 1 ? '' : 's'})</p>
                <p><strong>Flight Return:</strong> ${pendingFlight.returnAirline} | ${pendingFlight.destination} → ${pendingFlight.origin} | ${retArr.date} ${retArr.time} (${pendingFlight.returnStops} stop${pendingFlight.returnStops === 1 ? '' : 's'})</p>
                <p><strong>Flight price:</strong> £${pendingFlight.price}</p>
            `;
        } else {
            pendingFlightEl.innerHTML = '<p>No flight selected yet.';
        }
    }

    if (pendingHotelEl) {
        if (pendingHotel) {
            pendingHotelEl.innerHTML = `
                <p><strong>Hotel ${pendingHotel.name}</strong> — £${Number(pendingHotel.totalPrice).toFixed(2)}</p>
            `;
        } else {
            pendingHotelEl.innerHTML = '<p>No hotel selected yet.';
        }
    }

    const grid = document.getElementById('comparison-grid');
    if (!grid) return;
    const tripsKey = 'tripComparisons_' + (localStorage.getItem('userId') || 'guest');
    const trips = JSON.parse(localStorage.getItem(tripsKey) || '[]');

    if (trips.length === 0) {
        grid.innerHTML = '<p>No trips added yet. Select a flight and hotel above, then click "Add Trip to Comparison".</p>';
        return;
    }

    grid.innerHTML = `
        <div class="trip-cards-container">
            ${trips.map(t => {
                const f = t.flight;
                const h = t.hotel;

                const flightHtml = f ? (() => {
                    const outDep = formatDateTime(f.outboundDeparture);
                    const outArr = formatDateTime(f.outboundArrival);
                    const retDep = formatDateTime(f.returnDeparture);
                    const retArr = formatDateTime(f.returnArrival);
                    return `
                        <div class="trip-section">
                            <div class="section-title">✈️ Flights</div>
                            <p><strong>Outbound:</strong> ${f.origin} → ${f.destination}</p>
                            <p>${outDep.date} ${outDep.time} → ${outArr.date} ${outArr.time}</p>
                            <p>${f.outboundStops} stop${f.outboundStops === 1 ? '' : 's'}</p>

                            <p><strong>Return:</strong> ${f.destination} → ${f.origin}</p>
                            <p>${retDep.date} ${retDep.time} → ${retArr.date} ${retArr.time}</p>
                            <p>${f.returnStops} stop${f.returnStops === 1 ? '' : 's'}</p>

                            <p class="price-line">£${f.price}</p>
                        </div>`;
                })() : `<p>No flight</p>`;

                const hotelHtml = h ? `
                    <div class="trip-section">
                        <div class="section-title">🏨 Hotel</div>
                        <p>${h.name}</p>
                        <p class="price-line">${Number(h.totalPrice).toFixed(2)}</p>
                    </div>` : `<p>No hotel</p>`;

                return `
                    <div class="trip-card">
                        <div class="trip-header">Trip Option: ${h.location}</div>

                        ${flightHtml}
                        ${hotelHtml}

                        <div class="trip-footer">
                            <div class="trip-total">£${t.totalPrice.toFixed(2)}</div>
                            <button onclick="selectTrip(${t.id})">Book</button>
                            <button onclick="removeTrip(${t.id})">Remove</button>
                        </div>
                    </div>
                `;
            }).join('')}
        </div>
    `;
}


