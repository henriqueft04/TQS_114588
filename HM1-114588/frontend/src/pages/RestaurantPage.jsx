import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import RestaurantApi from '../api/RestaurantApi';
import Schedule from '../components/Schedule'

const RestaurantPage = () => {
  const [restaurants, setRestaurants] = useState([]);

  useEffect(() => {
    const fetchRestaurants = async () => {
      try {
        const response = await RestaurantApi.getAll();
        setRestaurants(response.data);
      } catch (error) {
        console.error('Failed to fetch restaurants:', error);
      }
    };

    fetchRestaurants();
  }, []);

  return (
    <div className="container mx-auto">
      <h1 className="text-4xl font-bold mb-8">Restaurants</h1>
      
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        {restaurants.map(restaurant => (
          <div key={restaurant.id} className="card bg-base-100 shadow-xl">
            <figure>
              {/* TODO: Add restaurant image */}
            </figure>
            <div className="card-body">
              <h2 className="card-title">{restaurant.name}</h2>
              <p>
                <i className="fas fa-users mr-2"></i>
                Capacity: {restaurant.capacity}
              </p>
              <p>
                <i className="fas fa-book-open mr-2"></i>
                Available Menus: {restaurant.availableMenus}
              </p>
              <div className="card-actions justify-end">
                <Link to={`/restaurants/${restaurant.id}`} className="btn btn-primary">
                  View Details
                </Link>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default RestaurantPage;