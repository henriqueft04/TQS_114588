import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import RestaurantApi from '../api/RestaurantApi';

const RestaurantPage = () => {
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchRestaurants = async () => {
      try {
        setLoading(true);
        const response = await RestaurantApi.getAll();
        setRestaurants(response.data);
      } catch (error) {
        console.error('Failed to fetch restaurants:', error);
        setError('Failed to load restaurants. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchRestaurants();
  }, []);

  if (loading) {
    return (
      <div className="container mx-auto text-center py-12">
        <span className="loading loading-spinner loading-lg"></span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container mx-auto py-8">
        <div className="alert alert-error">
          <i className="fas fa-exclamation-circle mr-2"></i>
          <span>{error}</span>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto">
      <h1 className="text-4xl font-bold mb-8">Restaurants</h1>
      
      {restaurants.length === 0 ? (
        <div className="alert alert-info">
          <i className="fas fa-info-circle mr-2"></i>
          <span>No restaurants found. Check back later!</span>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {restaurants.map(restaurant => (
            <div key={restaurant.id} className="card bg-base-100 shadow-xl">
              <div className="card-body">
                <h2 className="card-title">{restaurant.name}</h2>
                <p className="line-clamp-2">{restaurant.description}</p>
                <p>
                  <i className="fas fa-users mr-2"></i>
                  Capacity: {restaurant.capacity || 'N/A'}
                </p>
                <p>
                  <i className="fas fa-clock mr-2"></i>
                  Hours: {restaurant.operatingHours || 'Call for hours'}
                </p>
                <div className="card-actions justify-end mt-4">
                  <Link to={`/restaurants/${restaurant.id}`} className="btn btn-primary">
                    View Details
                  </Link>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default RestaurantPage;