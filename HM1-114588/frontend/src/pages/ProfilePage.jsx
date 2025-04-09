import React, { useEffect, useState } from 'react';
import { useAuth } from '../hooks/useAuth.jsx';
import RestaurantApi from '../api/RestaurantApi';

const ProfilePage = () => {
  const { user } = useAuth();
  const [reservations, setReservations] = useState([]);

  useEffect(() => {
    const fetchReservations = async () => {
      try {
        const response = await RestaurantApi.getReservationsByUser(user.id);
        setReservations(response.data);
      } catch (error) {
        console.error('Failed to fetch reservations:', error);
      }
    };

    fetchReservations();
  }, [user]);
  
  const handleCancelReservation = async (id) => {
    try {
      await RestaurantApi.cancelReservation(id);
      setReservations(reservations.filter((r) => r.id !== id));
    } catch (error) {
      console.error(`Failed to cancel reservation with ID ${id}:`, error);
    }
  };

  const pastReservations = reservations.filter((r) => new Date(r.reservationTime) < new Date());
  const upcomingReservations = reservations.filter((r) => new Date(r.reservationTime) >= new Date());

  return (
    <div className="container mx-auto">
      <h1 className="text-4xl font-bold mb-8">Profile</h1>
      
      <div className="mb-8">
        <h2 className="text-2xl font-bold mb-4">Upcoming Reservations</h2>
        
        {upcomingReservations.length > 0 ? (
          <ul>
            {upcomingReservations.map((reservation) => (
              <li key={reservation.id} className="mb-4">
                <p>
                  {reservation.restaurant.name} on{' '}
                  {new Date(reservation.reservationTime).toLocaleDateString()} at{' '}
                  {new Date(reservation.reservationTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                </p>
                <p>Party Size: {reservation.partySize}</p>
                <button 
                  onClick={() => handleCancelReservation(reservation.id)}
                  className="btn btn-error btn-sm"
                >
                  Cancel
                </button>
              </li>
            ))}
          </ul>
        ) : (
          <p>No upcoming reservations.</p>
        )}
      </div>
      
      <div>
        <h2 className="text-2xl font-bold mb-4">Past Reservations</h2>
        
        {pastReservations.length > 0 ? (
          <ul>
            {pastReservations.map((reservation) => (
              <li key={reservation.id} className="mb-4">
                <p>
                  {reservation.restaurant.name} on{' '}
                  {new Date(reservation.reservationTime).toLocaleDateString()} at{' '}
                  {new Date(reservation.reservationTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                </p>
                <p>Party Size: {reservation.partySize}</p>
              </li>
            ))}
          </ul>
        ) : (
          <p>No past reservations.</p>
        )}
      </div>
    </div>
  );
};

export default ProfilePage; 