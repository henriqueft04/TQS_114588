import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import RestaurantApi from '../api/RestaurantApi';
import ReservationModal from '../components/ReservationModal';

export default function ReservationsPage() {
  const { user, loading, isAuthenticated } = useAuth();
  const [reservations, setReservations] = useState([]);
  const [loadingReservations, setLoadingReservations] = useState(true);
  const [error, setError] = useState(null);
  const [cancelingId, setCancelingId] = useState(null);
  
  // Added for reservation creation
  const [restaurants, setRestaurants] = useState([]);
  const [loadingRestaurants, setLoadingRestaurants] = useState(false);
  const [selectedRestaurant, setSelectedRestaurant] = useState(null);
  const [showReservationModal, setShowReservationModal] = useState(false);

  // Fetch restaurants for the dropdown
  useEffect(() => {
    async function fetchRestaurants() {
      setLoadingRestaurants(true);
      try {
        const response = await RestaurantApi.getAll();
        setRestaurants(response.data);
      } catch (err) {
        console.error('Error fetching restaurants:', err);
      } finally {
        setLoadingRestaurants(false);
      }
    }
    
    fetchRestaurants();
  }, []);

  useEffect(() => {
    async function fetchReservations() {
      if (!user?.id) return;
      
      setLoadingReservations(true);
      try {
        const response = await RestaurantApi.getReservationsByUser(user.id);
        setReservations(response.data);
      } catch (err) {
        console.error('Error fetching reservations:', err);
        setError('Failed to load your reservations. Please try again later.');
      } finally {
        setLoadingReservations(false);
      }
    }
    
    if (user) {
      fetchReservations();
    } else {
      setLoadingReservations(false);
    }
  }, [user]);

  const handleCancelReservation = async (id) => {
    setCancelingId(id);
    try {
      await RestaurantApi.cancelReservation(id);
      // Update the reservation status in the local state
      setReservations(reservations.map(reservation => 
        reservation.id === id 
          ? { ...reservation, status: 'CANCELLED' } 
          : reservation
      ));
    } catch (err) {
      console.error('Error canceling reservation:', err);
      setError('Failed to cancel reservation. Please try again.');
    } finally {
      setCancelingId(null);
    }
  };
  
  // Added for reservation creation
  const handleOpenReservationModal = (restaurant) => {
    setSelectedRestaurant(restaurant);
    setShowReservationModal(true);
  };

  const handleCloseReservationModal = () => {
    setShowReservationModal(false);
    setSelectedRestaurant(null);
  };
  
  // Filter reservations by status
  const pendingReservations = reservations.filter(r => r.status === 'PENDING');
  const confirmedReservations = reservations.filter(r => r.status === 'CONFIRMED');
  const pastReservations = reservations.filter(r => 
    r.status === 'CHECKED_IN' || r.status === 'COMPLETED' || 
    (r.status === 'CONFIRMED' && new Date(r.reservationTime) < new Date())
  );
  const cancelledReservations = reservations.filter(r => r.status === 'CANCELLED');
  
  // While authentication is loading
  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <div className="loading loading-spinner loading-lg"></div>
      </div>
    );
  }
  
  // If not authenticated
  if (!isAuthenticated) {
    return (
      <div className="container mx-auto p-4">
        <div className="alert alert-warning">
          <div>
            <i className="fas fa-exclamation-triangle mr-2"></i>
            <span>Please log in to view your reservations.</span>
          </div>
          <div className="flex-none">
            <Link to="/login" className="btn btn-sm btn-primary">Login</Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto p-4">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">My Reservations</h1>
        <div className="dropdown dropdown-end">
          <label tabIndex={0} className="btn btn-primary">
            <i className="fas fa-plus mr-2"></i>
            Make Reservation
          </label>
          <ul tabIndex={0} className="dropdown-content z-[1] menu p-2 shadow bg-base-100 rounded-box w-52 max-h-80 overflow-y-auto">
            {loadingRestaurants ? (
              <li className="p-2 text-center">
                <span className="loading loading-spinner loading-sm"></span>
                Loading...
              </li>
            ) : restaurants.length > 0 ? (
              restaurants.map(restaurant => (
                <li key={restaurant.id}>
                  <a onClick={() => handleOpenReservationModal(restaurant)}>
                    {restaurant.name}
                  </a>
                </li>
              ))
            ) : (
              <li className="p-2 text-center">No restaurants available</li>
            )}
          </ul>
        </div>
      </div>
      
      {error && (
        <div className="alert alert-error mb-6">
          <i className="fas fa-exclamation-circle mr-2"></i>
          <span>{error}</span>
        </div>
      )}
      
      {loadingReservations ? (
        <div className="flex justify-center my-10">
          <div className="loading loading-spinner loading-lg"></div>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="card bg-base-100 shadow-xl">
              <div className="card-body">
                <h2 className="card-title text-xl">
                  <i className="fas fa-clock text-warning mr-2"></i>
                  Pending Reservations
                </h2>
                {pendingReservations.length === 0 ? (
                  <p className="py-4 text-base-content/70">No pending reservations</p>
                ) : (
                  <div className="space-y-4 mt-4">
                    {pendingReservations.map(reservation => (
                      <div key={reservation.id} className="border border-base-300 rounded-lg p-4">
                        <div className="flex justify-between">
                          <div>
                            <h3 className="font-bold">{reservation.restaurant.name}</h3>
                            <p className="text-sm">
                              {new Date(reservation.reservationTime).toLocaleDateString()} at{' '}
                              {new Date(reservation.reservationTime).toLocaleTimeString([], {
                                hour: '2-digit',
                                minute: '2-digit'
                              })}
                            </p>
                            <p className="text-sm">Party of {reservation.partySize}</p>
                            <p className="text-sm">
                              Status: <span className="badge badge-warning">PENDING</span>
                            </p>
                          </div>
                          <div>
                            <button
                              onClick={() => handleCancelReservation(reservation.id)}
                              className="btn btn-sm btn-outline btn-error"
                              disabled={cancelingId === reservation.id}
                            >
                              {cancelingId === reservation.id ? (
                                <span className="loading loading-spinner loading-xs"></span>
                              ) : (
                                <i className="fas fa-times mr-1"></i>
                              )}
                              Cancel
                            </button>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
            
            <div className="card bg-base-100 shadow-xl">
              <div className="card-body">
                <h2 className="card-title text-xl">
                  <i className="fas fa-check-circle text-success mr-2"></i>
                  Confirmed Reservations
                </h2>
                {confirmedReservations.length === 0 ? (
                  <p className="py-4 text-base-content/70">No confirmed reservations</p>
                ) : (
                  <div className="space-y-4 mt-4">
                    {confirmedReservations.map(reservation => (
                      <div key={reservation.id} className="border border-base-300 rounded-lg p-4">
                        <div className="flex justify-between">
                          <div>
                            <h3 className="font-bold">{reservation.restaurant.name}</h3>
                            <p className="text-sm">
                              {new Date(reservation.reservationTime).toLocaleDateString()} at{' '}
                              {new Date(reservation.reservationTime).toLocaleTimeString([], {
                                hour: '2-digit',
                                minute: '2-digit'
                              })}
                            </p>
                            <p className="text-sm">Party of {reservation.partySize}</p>
                            <p className="text-sm">
                              Status: <span className="badge badge-success">CONFIRMED</span>
                            </p>
                            {reservation.token && (
                              <p className="text-sm mt-2">
                                Token: <span className="font-mono bg-base-200 p-1 rounded">{reservation.token}</span>
                              </p>
                            )}
                          </div>
                          <div>
                            <button
                              onClick={() => handleCancelReservation(reservation.id)}
                              className="btn btn-sm btn-outline btn-error"
                              disabled={cancelingId === reservation.id}
                            >
                              {cancelingId === reservation.id ? (
                                <span className="loading loading-spinner loading-xs"></span>
                              ) : (
                                <i className="fas fa-times mr-1"></i>
                              )}
                              Cancel
                            </button>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          </div>
          
          <div className="divider my-8">History</div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="card bg-base-100 shadow-xl">
              <div className="card-body">
                <h2 className="card-title text-xl">
                  <i className="fas fa-history text-info mr-2"></i>
                  Past Reservations
                </h2>
                {pastReservations.length === 0 ? (
                  <p className="py-4 text-base-content/70">No past reservations</p>
                ) : (
                  <div className="space-y-4 mt-4">
                    {pastReservations.map(reservation => (
                      <div key={reservation.id} className="border border-base-300 rounded-lg p-4 opacity-75">
                        <h3 className="font-bold">{reservation.restaurant.name}</h3>
                        <p className="text-sm">
                          {new Date(reservation.reservationTime).toLocaleDateString()} at{' '}
                          {new Date(reservation.reservationTime).toLocaleTimeString([], {
                            hour: '2-digit',
                            minute: '2-digit'
                          })}
                        </p>
                        <p className="text-sm">Party of {reservation.partySize}</p>
                        <p className="text-sm">
                          Status: <span className="badge badge-neutral">{reservation.status}</span>
                        </p>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
            
            <div className="card bg-base-100 shadow-xl">
              <div className="card-body">
                <h2 className="card-title text-xl">
                  <i className="fas fa-ban text-error mr-2"></i>
                  Cancelled Reservations
                </h2>
                {cancelledReservations.length === 0 ? (
                  <p className="py-4 text-base-content/70">No cancelled reservations</p>
                ) : (
                  <div className="space-y-4 mt-4">
                    {cancelledReservations.map(reservation => (
                      <div key={reservation.id} className="border border-base-300 rounded-lg p-4 opacity-75">
                        <h3 className="font-bold">{reservation.restaurant.name}</h3>
                        <p className="text-sm">
                          {new Date(reservation.reservationTime).toLocaleDateString()} at{' '}
                          {new Date(reservation.reservationTime).toLocaleTimeString([], {
                            hour: '2-digit',
                            minute: '2-digit'
                          })}
                        </p>
                        <p className="text-sm">Party of {reservation.partySize}</p>
                        <p className="text-sm">
                          Status: <span className="badge badge-error">CANCELLED</span>
                        </p>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          </div>
          
          <div className="flex justify-center mt-10">
            <Link to="/" className="btn btn-primary">
              <i className="fas fa-utensils mr-2"></i>
              Find Restaurants
            </Link>
          </div>
        </>
      )}
      
      {/* Reservation Modal */}
      {showReservationModal && selectedRestaurant && (
        <ReservationModal
          restaurant={selectedRestaurant}
          isOpen={showReservationModal}
          onClose={handleCloseReservationModal}
        />
      )}
    </div>
  );
} 