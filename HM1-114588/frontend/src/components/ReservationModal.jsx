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
  const [error, setError] = useState(null);

  useEffect(() => {
    // Reset form when modal opens
    if (isOpen) {
      setError(null);
      setName(user?.name || '');
      setEmail(user?.email || '');
      setPartySize(2);
      setReservationDate('');
      setReservationTime('');
    }
  }, [isOpen, user]);

  if (!isOpen) return null;

  // Generate reservation time options based on operating hours
  const reservationTimeOptions = [];
  if (restaurant?.operatingHours) {
    const [startTime, endTime] = restaurant.operatingHours.split('-');
    const [startHour, startMinute] = startTime.split(':').map(Number);
    const [endHour, endMinute] = endTime.split(':').map(Number);
    
    for (let hour = startHour; hour <= endHour; hour++) {
      for (let minute of [0, 30]) {
        if (hour === endHour && minute > endMinute) {
          break;
        }
        const time = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
        reservationTimeOptions.push(
          <option key={time} value={time}>
            {time}
          </option>
        );
      }
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    try {
      // Check capacity
      const capacityResponse = await RestaurantApi.checkCapacity(
        restaurant.id,
        `${reservationDate}T${reservationTime}:00`,
        partySize
      );

      if (capacityResponse.data.hasCapacity) {
        // Create the reservation
        await RestaurantApi.createReservation({
          restaurantId: restaurant.id,
          userId: user.id,
          reservationTime: `${reservationDate}T${reservationTime}:00`,
          partySize,
          name,
          email,
        });
        
        // Close modal and redirect to confirmation page
        onClose();
        navigate(`/reservations/${capacityResponse.data.reservationId}`);
      } else {
        setError('Sorry, the restaurant is fully booked for the selected time.');
      }
    } catch (error) {
      console.error('Failed to make reservation:', error);
      setError('Failed to make reservation. Please try again.');
    }
  };

  return (
    <div className="modal modal-open">
      <div className="modal-box max-w-lg">
        <h3 className="font-bold text-lg mb-4">
          Make a Reservation for {restaurant.name}
        </h3>
        
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
              required
            />
          </div>
          
          <div className="mb-4">
            <label htmlFor="reservationTime" className="block mb-2">Time:</label>
            <select
              id="reservationTime"
              value={reservationTime}
              onChange={(e) => setReservationTime(e.target.value)}
              className="w-full select select-bordered"
              required
            >
              <option value="">Select a time</option>
              {reservationTimeOptions}
            </select>
          </div>
          
          <div className="modal-action">
            <button type="button" className="btn" onClick={onClose}>Cancel</button>
            <button type="submit" className="btn btn-primary">Make Reservation</button>
          </div>
        </form>
      </div>
      <div className="modal-backdrop" onClick={onClose}></div>
    </div>
  );
};

export default ReservationModal; 