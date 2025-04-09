import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import RestaurantApi from '../api/RestaurantApi';

const ReservationConfirmationPage = () => {
  const { id } = useParams();
  const [reservation, setReservation] = useState(null);
  const [restaurant, setRestaurant] = useState(null);

  useEffect(() => {
    const fetchReservation = async () => {
      try {
        const response = await RestaurantApi.getReservation(id);
        setReservation(response.data);
      } catch (error) {
        console.error(`Failed to fetch reservation with ID ${id}:`, error);
      }
    };

    const fetchRestaurant = async () => {
      try {
        const response = await RestaurantApi.getById(reservation.restaurantId);
        setRestaurant(response.data);
      } catch (error) {
        console.error(`Failed to fetch restaurant with ID ${reservation.restaurantId}:`, error);
      }
    };

    fetchReservation();
    
    if (reservation) {
      fetchRestaurant();
    }
  }, [id, reservation]);

  if (!reservation) {
    return <div>Loading...</div>;
  }

  return (
    <div className="container mx-auto">
      <h1 className="text-4xl font-bold mb-8">Reservation Confirmed!</h1>
      
      <div className="mb-8">
        <p>Your reservation is confirmed with the following details:</p>
        
        <ul className="list-disc list-inside">
          <li>Restaurant: {restaurant?.name}</li>
          <li>Date: {new Date(reservation.reservationTime).toLocaleDateString()}</li>
          <li>Time: {new Date(reservation.reservationTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</li>
          <li>Party Size: {reservation.partySize}</li>
          <li>Name: {reservation.name}</li>
          <li>Email: {reservation.email}</li>
        </ul>
      </div>
      
      <p>You can view and manage your reservations from your <Link to="/profile" className="link">profile page</Link>.</p>
    </div>
  );
};

export default ReservationConfirmationPage; 