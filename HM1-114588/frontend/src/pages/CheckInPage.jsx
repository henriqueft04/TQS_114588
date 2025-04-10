import React, { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import { useNavigate } from 'react-router-dom';
import RestaurantApi from '../api/RestaurantApi';

export default function CheckInPage() {
  const { user, isStaff } = useAuth();
  const navigate = useNavigate();
  const [token, setToken] = useState('');
  const [reservation, setReservation] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  // Redirect non-staff users
  if (!isStaff) {
    return (
      <div className="container mx-auto p-4">
        <div className="alert alert-error">
          <i className="fas fa-exclamation-triangle mr-2"></i>
          <span>You don't have permission to access this page. Staff access only.</span>
        </div>
      </div>
    );
  }

  const handleTokenSearch = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    setReservation(null);
    setLoading(true);
    
    try {
      if (!token.trim()) {
        setError('Please enter a valid reservation token');
        return;
      }
      
      const response = await RestaurantApi.getReservationByToken(token.trim());
      setReservation(response.data);
    } catch (err) {
      console.error('Error fetching reservation:', err);
      setError('No reservation found with this token');
    } finally {
      setLoading(false);
    }
  };

  const handleCheckIn = async () => {
    setLoading(true);
    setError(null);
    setSuccess(null);
    
    try {
      await RestaurantApi.checkInReservation(token);
      setSuccess('Reservation has been successfully checked in!');
      
      // Refresh reservation data
      const response = await RestaurantApi.getReservationByToken(token);
      setReservation(response.data);
    } catch (err) {
      console.error('Error checking in reservation:', err);
      setError('Failed to check in. This reservation may already be checked in or canceled.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-3xl font-bold mb-6">Reservation Check-In</h1>
      
      <div className="card bg-base-100 shadow-xl mb-6">
        <div className="card-body">
          <h2 className="card-title text-xl mb-4">
            <i className="fas fa-clipboard-check mr-2"></i>
            Verify Reservation Token
          </h2>
          
          <form onSubmit={handleTokenSearch} className="flex flex-col md:flex-row gap-4">
            <div className="form-control flex-1">
              <input
                type="text"
                placeholder="Enter reservation token"
                value={token}
                onChange={(e) => setToken(e.target.value)}
                className="input input-bordered w-full"
              />
            </div>
            <button 
              type="submit" 
              className="btn btn-primary"
              disabled={loading}
            >
              {loading ? <span className="loading loading-spinner"></span> : <i className="fas fa-search mr-2"></i>}
              Verify
            </button>
          </form>
          
          {error && (
            <div className="alert alert-error mt-4">
              <i className="fas fa-exclamation-circle mr-2"></i>
              <span>{error}</span>
            </div>
          )}
          
          {success && (
            <div className="alert alert-success mt-4">
              <i className="fas fa-check-circle mr-2"></i>
              <span>{success}</span>
            </div>
          )}
        </div>
      </div>
      
      {reservation && (
        <div className="card bg-base-100 shadow-xl">
          <div className="card-body">
            <h2 className="card-title text-xl mb-4">
              <i className="fas fa-info-circle mr-2"></i>
              Reservation Details
            </h2>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="form-control">
                <label className="label">
                  <span className="label-text font-bold">Customer</span>
                </label>
                <div className="input input-bordered">
                  {reservation.customerName}
                </div>
              </div>
              
              <div className="form-control">
                <label className="label">
                  <span className="label-text font-bold">Email</span>
                </label>
                <div className="input input-bordered">
                  {reservation.customerEmail}
                </div>
              </div>
              
              <div className="form-control">
                <label className="label">
                  <span className="label-text font-bold">Restaurant</span>
                </label>
                <div className="input input-bordered">
                  {reservation.restaurant.name}
                </div>
              </div>
              
              <div className="form-control">
                <label className="label">
                  <span className="label-text font-bold">Date & Time</span>
                </label>
                <div className="input input-bordered">
                  {new Date(reservation.reservationTime).toLocaleString()}
                </div>
              </div>
              
              <div className="form-control">
                <label className="label">
                  <span className="label-text font-bold">Party Size</span>
                </label>
                <div className="input input-bordered">
                  {reservation.partySize} people
                </div>
              </div>
              
              <div className="form-control">
                <label className="label">
                  <span className="label-text font-bold">Status</span>
                </label>
                <div className="input input-bordered">
                  <span className={`badge 
                    ${reservation.status === 'CONFIRMED' ? 'badge-success' : ''}
                    ${reservation.status === 'PENDING' ? 'badge-warning' : ''}
                    ${reservation.status === 'CANCELLED' ? 'badge-error' : ''}
                    ${reservation.status === 'CHECKED_IN' ? 'badge-info' : ''}
                  `}>
                    {reservation.status}
                  </span>
                </div>
              </div>
            </div>
            
            <div className="card-actions justify-end mt-6">
              {reservation.status === 'CONFIRMED' && (
                <button 
                  className="btn btn-success" 
                  onClick={handleCheckIn}
                  disabled={loading}
                >
                  {loading ? <span className="loading loading-spinner"></span> : <i className="fas fa-check-circle mr-2"></i>}
                  Check In
                </button>
              )}
              {reservation.status === 'CHECKED_IN' && (
                <div className="alert alert-success">
                  <i className="fas fa-check-circle mr-2"></i>
                  <span>This reservation has already been checked in.</span>
                </div>
              )}
              {reservation.status === 'CANCELLED' && (
                <div className="alert alert-error">
                  <i className="fas fa-times-circle mr-2"></i>
                  <span>This reservation has been cancelled.</span>
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
} 