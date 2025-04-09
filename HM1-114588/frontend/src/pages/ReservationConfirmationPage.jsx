import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import RestaurantApi from '../api/RestaurantApi';

const ReservationConfirmationPage = () => {
  const { id } = useParams();
  const [reservation, setReservation] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchReservation = async () => {
      try {
        const response = await RestaurantApi.getReservation(id);
        setReservation(response.data);
      } catch (error) {
        setError('Failed to load reservation');
      } finally {
        setIsLoading(false);
      }
    };

    fetchReservation();
  }, [id]);

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div className="alert alert-error">{error}</div>;
  }

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-3xl font-bold mb-4">Reservation Confirmation</h1>
      
      <p className="mb-4">
        Thank you for your reservation at {reservation.restaurant.name}!
      </p>
      
      <div className="mb-4">
        <strong>Reservation Details:</strong>
        <ul>
          <li>Date: {new Date(reservation.reservationTime).toLocaleDateString()}</li>
          <li>Time: {new Date(reservation.reservationTime).toLocaleTimeString()}</li>
          <li>Party Size: {reservation.partySize}</li>
        </ul>
      </div>
      
      <div className="mb-4">
        <strong>Ticket Code:</strong> {reservation.token}
      </div>
      
      <p>
        Please present this ticket code upon arrival at the restaurant to check in for your reservation.
      </p>
    </div>
  );
};

export default ReservationConfirmationPage; 