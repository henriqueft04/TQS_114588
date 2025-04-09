import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import RestaurantApi from '../api/RestaurantApi';

const CheckInPage = () => {
  const navigate = useNavigate();
  const [token, setToken] = useState('');
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(false);

    try {
      await RestaurantApi.checkInReservation(token);
      setSuccess(true);
    } catch (error) {
      setError('Failed to check in reservation. Please verify the ticket code.');
    }
  };

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-3xl font-bold mb-4">Check In Reservation</h1>
      
      {success ? (
        <div className="alert alert-success">
          Reservation checked in successfully!
        </div>
      ) : (
        <form onSubmit={handleSubmit}>
          <div className="form-control mb-4">
            <label className="label">
              <span className="label-text">Ticket Code</span>
            </label>
            <input
              type="text"
              placeholder="Enter ticket code"
              className="input input-bordered"
              value={token}
              onChange={(e) => setToken(e.target.value)}
              required
            />
          </div>
          
          {error && <div className="alert alert-error mb-4">{error}</div>}
          
          <button type="submit" className="btn btn-primary">
            Check In
          </button>
        </form>
      )}
    </div>
  );
};

export default CheckInPage; 