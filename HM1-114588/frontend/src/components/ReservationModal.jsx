import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth.jsx';
import RestaurantApi from '../api/RestaurantApi';

const ReservationModal = ({ restaurant, isOpen, onClose }) => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [partySize, setPartySize] = useState(2);
  const [reservationDate, setReservationDate] = useState('');
  const [reservationTime, setReservationTime] = useState('');
  const [name, setName] = useState(user?.name || '');
  const [email, setEmail] = useState(user?.email || '');
  const [phone, setPhone] = useState('123-456-7890'); // Default phone number
  const [mealType, setMealType] = useState('REGULAR'); // Default meal type
  const [specialRequests, setSpecialRequests] = useState('');
  const [error, setError] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [availableTimeSlots, setAvailableTimeSlots] = useState([]);
  const [mealSlots, setMealSlots] = useState([]);

  // Log user information when component mounts or user changes
  useEffect(() => {
    console.log('Current user object:', user);
    if (user) {
      console.log('User ID:', user.id);
      console.log('User authenticated:', !!user);
    }
  }, [user]);

  useEffect(() => {
    // Reset form when modal opens
    if (isOpen) {
      setError(null);
      setName(user?.name || '');
      setEmail(user?.email || '');
      setPhone('123-456-7890');
      setPartySize(2);
      setReservationDate('');
      setReservationTime('');
      setMealType('REGULAR');
      setSpecialRequests('');
      setIsSubmitting(false);
      
      // Parse operating hours when modal opens
      if (restaurant?.operatingHours) {
        parseOperatingHours(restaurant.operatingHours);
      }
    }
  }, [isOpen, user, restaurant]);

  // Parse operating hours in the format "Segunda-Sexta: 12:00-15:00, 19:00-21:00"
  const parseOperatingHours = (operatingHoursString) => {
    try {
      console.log('Parsing operating hours:', operatingHoursString);
      
      // Extract time slots (everything after the colon)
      const colonIndex = operatingHoursString.indexOf(':');
      if (colonIndex === -1) {
        throw new Error('Invalid operating hours format. Missing colon.');
      }
      
      const timeSlotsPart = operatingHoursString.substring(colonIndex + 1).trim();
      console.log('Time slots part:', timeSlotsPart);
      
      // Split by comma to get different time ranges
      const timeRanges = timeSlotsPart.split(',').map(range => range.trim());
      console.log('Time ranges:', timeRanges);
      
      // Store the meal slots for display
      const mealSlotsFormatted = timeRanges.map((range, index) => {
        const mealNames = ['Lunch', 'Dinner', 'Breakfast', 'Brunch', 'Snack'];
        const mealName = index < mealNames.length ? mealNames[index] : `Meal ${index + 1}`;
        return `${mealName}: ${range}`;
      });
      setMealSlots(mealSlotsFormatted);
      
      // For each range, generate time slots at 30-minute intervals
      const slots = [];
      
      timeRanges.forEach(range => {
        const [start, end] = range.split('-').map(time => time.trim());
        
        if (!start || !end) {
          console.error('Invalid time range format:', range);
          return; // Skip this range
        }
        
        const [startHour, startMinute] = start.split(':').map(Number);
        const [endHour, endMinute] = end.split(':').map(Number);
        
        if (isNaN(startHour) || isNaN(startMinute) || isNaN(endHour) || isNaN(endMinute)) {
          console.error('Invalid time format in range:', range);
          return; // Skip this range
        }
        
        // Convert to minutes for easier calculation
        const startTimeInMinutes = startHour * 60 + startMinute;
        const endTimeInMinutes = endHour * 60 + endMinute;
        
        // Generate slots every 30 minutes, but stop 30 minutes before closing
        for (let timeInMinutes = startTimeInMinutes; timeInMinutes <= endTimeInMinutes - 30; timeInMinutes += 30) {
          const hour = Math.floor(timeInMinutes / 60);
          const minute = timeInMinutes % 60;
          const timeString = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
          slots.push(timeString);
        }
      });
      
      console.log('Generated time slots:', slots);
      setAvailableTimeSlots(slots);
    } catch (error) {
      console.error('Error parsing operating hours:', error);
      // Fallback to basic time slots if parsing fails
      const basicSlots = [];
      for (let hour = 11; hour <= 21; hour++) {
        for (let minute of [0, 30]) {
          const timeString = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
          basicSlots.push(timeString);
        }
      }
      setAvailableTimeSlots(basicSlots);
      setMealSlots(['Standard meal times (parsing error occurred)']);
    }
  };

  // Set today as the minimum date for reservation
  const today = new Date().toISOString().split('T')[0];

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setIsSubmitting(true);

    if (!reservationDate || !reservationTime) {
      setError('Please select both date and time for your reservation.');
      setIsSubmitting(false);
      return;
    }

    try {
      // Format the date and time properly
      // Ensure the time string is properly formatted with hours and minutes
      const formattedTime = reservationTime.includes(':') 
        ? reservationTime 
        : `${reservationTime.padStart(2, '0')}:00`;
        
      // Create the datetime string in different formats based on what the backend might expect
      const localDateTimeFormat = `${reservationDate}T${formattedTime}`;
      const formattedWithSeconds = `${reservationDate}T${formattedTime}:00`;
      
      // Try with the format that includes seconds but no timezone
      const formattedDateTime = formattedWithSeconds;
      
      console.log('Reservation datetime:', formattedDateTime);
      console.log('Date part:', reservationDate);
      console.log('Time part:', formattedTime);

      // Check capacity
      const capacityResponse = await RestaurantApi.checkCapacity(
        restaurant.id,
        formattedDateTime,
        parseInt(partySize, 10)
      );

      if (capacityResponse.data.hasCapacity) {
        // Set up the reservation object for the backend
        const parsedPartySize = parseInt(partySize, 10);
        const reservationData = {
          restaurant: { id: restaurant.id },
          customerName: name,
          customerEmail: email,
          customerPhone: phone,
          partySize: parsedPartySize,
          reservationTime: formattedDateTime,
          mealType: mealType,
          specialRequests: specialRequests || "",
          isGroupReservation: parsedPartySize >= 8,
          menusRequired: parsedPartySize
        };

        // If user is logged in, associate the reservation with the user
        if (user && user.id) {
          console.log('Adding user ID to reservation:', user.id);
          // Only use one format for user association - try the object format first
          reservationData.user = { id: user.id };
          // Remove userId to avoid potential conflict
          // reservationData.userId = user.id;
        } else {
          console.warn('No user ID available to associate with the reservation');
        }
        
        console.log('Sending reservation data:', JSON.stringify(reservationData, null, 2));
        
        // Create the reservation
        const reservationResponse = await RestaurantApi.createReservation(reservationData);
        console.log('Reservation created successfully:', reservationResponse.data);
        
        // Close modal and redirect to confirmation page
        onClose();
        
        if (reservationResponse.data && reservationResponse.data.id) {
          navigate(`/reservations/${reservationResponse.data.id}`);
        } else if (capacityResponse.data.reservationId) {
          navigate(`/reservations/${capacityResponse.data.reservationId}`);
        } else {
          navigate(`/reservations`);
        }
      } else {
        setError('Sorry, the selected time slot is fully booked. Please choose a different time.');
      }
    } catch (error) {
      console.error('Failed to make reservation:', error);
      console.error('Error details:', error.response?.data);
      if (error.response?.data) {
        setError(`Failed to make reservation: ${error.response.data.message || error.response.data.error || JSON.stringify(error.response.data)}`);
      } else {
        setError('Failed to make reservation. Please try again.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  async function fetchMealTypes() {
    const restaurantId = restaurant.id;
    const reservationTime = reservationTime; // Format the time as expected by the backend
    
    try {
      const response = await RestaurantApi.getMealTypes(restaurantId, reservationTime);
      setMealType(response.data[0]);
    } catch (error) {
      console.error('Failed to fetch meal types:', error);
    }
  }

  async function fetchMenus() {
    const restaurantId = restaurant.id;
    const mealType = mealType;

    try {    
      const response = await RestaurantApi.getMenus(restaurantId, mealType);
      // Assuming the response data is stored in the state
      // You might want to update the state with the fetched menus
      console.log('Fetched menus:', response.data);
    } catch (error) {
      console.error('Failed to fetch menus:', error);  
    }
  }

  return (
    <div className="modal modal-open">
      <div className="modal-box max-w-lg">
        <h3 className="font-bold text-lg mb-4">
          Make a Reservation for {restaurant.name}
        </h3>
        
        {restaurant?.operatingHours && (
          <div className="alert alert-info mb-4">
            <i className="fas fa-clock mr-2"></i>
            <span>Operating hours: {restaurant.operatingHours}</span>
          </div>
        )}
        
        {user && (
          <div className="alert alert-info mb-4">
            <i className="fas fa-user mr-2"></i>
            <span>Reservation will be associated with your account ({user.email})</span>
          </div>
        )}
        
        {mealSlots.length > 0 && (
          <div className="alert alert-success mb-4">
            <i className="fas fa-utensils mr-2"></i>
            <div className="flex flex-col">
              <span className="font-bold">Available meal slots:</span>
              {mealSlots.map((slot, index) => (
                <span key={index} className="text-sm">{slot}</span>
              ))}
            </div>
          </div>
        )}
        
        {error && (
          <div className="alert alert-error mb-4">
            <i className="fas fa-exclamation-circle"></i>
            <span>{error}</span>
          </div>
        )}
        
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label htmlFor="name" className="block mb-2">Name:</label>
            <input
              type="text"
              id="name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="w-full input input-bordered"
              required
            />
          </div>
          
          <div className="mb-4">
            <label htmlFor="email" className="block mb-2">Email:</label>
            <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full input input-bordered"
              required
            />
          </div>
          
          <div className="mb-4">
            <label htmlFor="phone" className="block mb-2">Phone:</label>
            <input
              type="tel"
              id="phone"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              className="w-full input input-bordered"
              required
            />
          </div>
          
          <div className="mb-4">
            <label htmlFor="partySize" className="block mb-2">Party Size:</label>
            <input
              type="number"
              id="partySize"
              min="1"
              value={partySize}
              onChange={(e) => setPartySize(e.target.value)}
              className="w-full input input-bordered"
              required
            />
          </div>
          
          <div className="mb-4">
            <label htmlFor="reservationDate" className="block mb-2">Date:</label>
            <input
              type="date"
              id="reservationDate"
              value={reservationDate}
              onChange={(e) => setReservationDate(e.target.value)}
              className="w-full input input-bordered"
              min={today}
              required
            />
          </div>
          
          <div className="mb-4">
            <label htmlFor="reservationTime" className="block mb-2">
              Time: <span className="text-sm text-gray-500">(Only times during meal service are available)</span>
            </label>
            <select
              id="reservationTime"
              value={reservationTime}
              onChange={(e) => setReservationTime(e.target.value)}
              className="w-full select select-bordered"
              required
            >
              <option value="">Select a time</option>
              {availableTimeSlots.length > 0 ? (
                availableTimeSlots.map(time => (
                  <option key={time} value={time}>
                    {time}
                  </option>
                ))
              ) : (
                <option value="" disabled>No available time slots</option>
              )}
            </select>
          </div>
          
          <div className="mb-4">
            <label htmlFor="mealType" className="block mb-2">Meal Type:</label>
            <select
              value={mealType}
              onChange={(e) => setMealType(e.target.value)}
              className="w-full select select-bordered"
            >
              <option value="REGULAR">Regular</option>
              <option value="VEGETARIAN">Vegetarian</option>
              <option value="VEGAN">Vegan</option>
              <option value="GLUTEN_FREE">Gluten-free</option>
              <option value="KETO">Keto</option>
              <option value="PALEO">Paleo</option>
              <option value="LOW_CARB">Low Carb</option>
              <option value="HIGH_PROTEIN">High Protein</option>
            </select>
          </div>
          
          <div className="mb-4">
            <label htmlFor="specialRequests" className="block mb-2">Special Requests:</label>
            <textarea
              id="specialRequests"
              value={specialRequests}
              onChange={(e) => setSpecialRequests(e.target.value)}
              className="w-full textarea textarea-bordered"
              rows="3"
            ></textarea>
          </div>
          
          <div className="modal-action">
            <button type="button" className="btn" onClick={onClose} disabled={isSubmitting}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={isSubmitting || availableTimeSlots.length === 0}>
              {isSubmitting ? <span className="loading loading-spinner"></span> : null}
              Make Reservation
            </button>
          </div>
        </form>
      </div>
      <div className="modal-backdrop" onClick={onClose}></div>
    </div>
  );
};

export default ReservationModal; 