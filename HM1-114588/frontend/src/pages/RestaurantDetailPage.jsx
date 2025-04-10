import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import RestaurantApi from '../api/RestaurantApi';
import ReservationModal from '../components/ReservationModal';
import { useAuth } from '../hooks/useAuth';

const RestaurantDetailPage = () => {
  const { id } = useParams();
  const { user } = useAuth();
  const [restaurant, setRestaurant] = useState(null);
  const [menus, setMenus] = useState([]);
  const [weather, setWeather] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        
        // Fetch restaurant details
        const restaurantResponse = await RestaurantApi.getById(id);
        setRestaurant(restaurantResponse.data);
        
        // Fetch menus
        const menusResponse = await RestaurantApi.getMenus(id);
        setMenus(menusResponse.data);
        
        // Fetch weather for the restaurant's location
        if (restaurantResponse.data.location) {
          const today = new Date().toISOString().split('T')[0];
          const weatherResponse = await RestaurantApi.getWeather(
            restaurantResponse.data.location.id,
            today
          );
          setWeather(weatherResponse.data);
        }
      } catch (error) {
        console.error('Failed to fetch data:', error);
        setError('Failed to load restaurant details. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id]);

  const handleOpenModal = () => {
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

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

  if (!restaurant) {
    return (
      <div className="container mx-auto py-8">
        <div className="alert alert-warning">
          <i className="fas fa-exclamation-triangle mr-2"></i>
          <span>Restaurant not found</span>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto">
      <div className="card bg-base-100 shadow-xl mb-8">
        <div className="card-body">
          <h1 className="card-title text-3xl">{restaurant.name}</h1>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-4">
            <div>
              <h2 className="text-xl font-bold mb-2">About</h2>
              <p className="mb-4">{restaurant.description}</p>
              
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <h3 className="font-bold">Capacity</h3>
                  <p>{restaurant.capacity} people</p>
                </div>
                
                <div>
                  <h3 className="font-bold">Hours</h3>
                  <p>{restaurant.operatingHours || 'Contact for hours'}</p>
                </div>
                
                <div>
                  <h3 className="font-bold">Location</h3>
                  <p>{restaurant.location?.name || 'No location specified'}</p>
                </div>
                
                <div>
                  <h3 className="font-bold">Contact</h3>
                  <p>{restaurant.contactInfo || 'No contact information'}</p>
                </div>
              </div>
            </div>
            
            <div>
              <h2 className="text-xl font-bold mb-2">Today's Weather</h2>
              {weather ? (
                <div className="card bg-base-200 p-4">
                  <div className="flex items-center">
                    <div className="mr-4">
                      <i className="fas fa-temperature-high text-3xl"></i>
                    </div>
                    <div>
                      <p className="text-xl">{Math.round(weather.temperature)}°C</p>
                      <p>Humidity: {weather.humidity}%</p>
                      {weather.precipitation > 0 && (
                        <p>Rain: {weather.precipitation} mm</p>
                      )}
                      <p>Wind: {Math.round(weather.windSpeedKm)} km/h</p>
                    </div>
                  </div>
                </div>
              ) : (
                <p>Weather information unavailable</p>
              )}
              
              <div className="mt-6">
                <button 
                  className="btn btn-primary w-full" 
                  onClick={user ? handleOpenModal : () => navigate('/login')}
                >
                  {user ? "Make a Reservation" : "Login to Make Reservation"}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div className="mb-8">
        <h2 className="text-2xl font-bold mb-4">Available Menus</h2>
        
        {menus.length === 0 ? (
          <div className="alert alert-info">
            <i className="fas fa-info-circle mr-2"></i>
            <span>No menus available at this time.</span>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {menus.map(menu => (
              <div key={menu.id} className="card bg-base-100 shadow-md">
                <div className="card-body">
                  <h3 className="card-title">{menu.name}</h3>
                  <p>{menu.description}</p>
                  
                  {menu.date && (
                    <p className="text-sm text-gray-500">
                      Available on: {new Date(menu.date).toLocaleDateString()}
                    </p>
                  )}
                  
                  {menu.dishes && menu.dishes.length > 0 && (
                    <div className="mt-4">
                      <h4 className="font-bold mb-2">Dishes:</h4>
                      <ul className="list-disc list-inside">
                        {menu.dishes.map(dish => (
                          <li key={dish.id}>
                            {dish.name} - ${dish.price}
                          </li>
                        ))}
                      </ul>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
      
      {isModalOpen && (
        <ReservationModal 
          restaurant={restaurant} 
          isOpen={isModalOpen} 
          onClose={handleCloseModal} 
        />
      )}
    </div>
  );
};

export default RestaurantDetailPage; 