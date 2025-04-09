import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import RestaurantApi from '../api/RestaurantApi';

const RestaurantDetailPage = () => {
  const { id } = useParams();
  const [restaurant, setRestaurant] = useState(null);
  const [partySize, setPartySize] = useState(2);
  const [reservationDate, setReservationDate] = useState('');
  const [reservationTime, setReservationTime] = useState('');

  useEffect(() => {
    const fetchRestaurant = async () => {
      try {
        const response = await RestaurantApi.getById(id);
        setRestaurant(response.data);
      } catch (error) {
        console.error(`Failed to fetch restaurant with ID ${id}:`, error);
      }
    };

    fetchRestaurant();
  }, [id]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      // Check capacity
      const capacityResponse = await RestaurantApi.checkCapacity(
        id,
        `${reservationDate}T${reservationTime}:00`,
        partySize
      );

      if (capacityResponse.data.hasCapacity) {
        // TODO: Actually create the reservation
        alert('Reservation created! (not really, still need to implement)');
      } else {
        alert('Sorry, the restaurant is fully booked for the selected time.');
      }
    } catch (error) {
      console.error('Failed to check capacity:', error);
    }
  };

  if (!restaurant) {
    return <div>Loading...</div>;
  }

  return (
    <div className="container mx-auto">
      <h1 className="text-4xl font-bold mb-8">{restaurant.name}</h1>
      
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div>
          {/* TODO: Add restaurant image */}
          <p>Capacity: {restaurant.capacity}</p>
          <p>Available Menus: {restaurant.availableMenus}</p>
        </div>
        
        <div>
          <h2 className="text-2xl font-bold mb-4">Location</h2>
          <p>{restaurant.location.name}</p>
          <p>Latitude: {restaurant.location.latitude}</p>
          <p>Longitude: {restaurant.location.longitude}</p>
        </div>
      </div>
      
      <div className="mt-8">
        <h2 className="text-2xl font-bold mb-4">Make a Reservation</h2>
        
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label htmlFor="partySize" className="block mb-2">Party Size:</label>
            <input
              type="number"
              id="partySize"
              min="1"
              value={partySize}
              onChange={(e) => setPartySize(e.target.value)}
              className="w-full border rounded py-2 px-3"
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
              className="w-full border rounded py-2 px-3"
              required
            />
          </div>
          
          <div className="mb-4">
            <label htmlFor="reservationTime" className="block mb-2">Time:</label>
            <input
              type="time"
              id="reservationTime"
              value={reservationTime}
              onChange={(e) => setReservationTime(e.target.value)}
              className="w-full border rounded py-2 px-3"
              required
            />
          </div>
          
          <button type="submit" className="btn btn-primary">Make Reservation</button>
        </form>
      </div>
    </div>
  );
};

export default RestaurantDetailPage; 