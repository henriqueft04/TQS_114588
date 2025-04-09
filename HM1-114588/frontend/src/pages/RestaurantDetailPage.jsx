import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import RestaurantApi from '../api/RestaurantApi';
import { useAuth } from '../hooks/useAuth.jsx';
import ReservationModal from '../components/ReservationModal';

const RestaurantDetailPage = () => {
  const { id } = useParams();
  const { user } = useAuth();
  const [restaurant, setRestaurant] = useState(null);
  const [menus, setMenus] = useState([]);
  const [weather, setWeather] = useState({});
  const [isLoading, setIsLoading] = useState(true);
  const [showReservationModal, setShowReservationModal] = useState(false);
  const [showLoginWarning, setShowLoginWarning] = useState(false);

  // Fetch restaurant data
  useEffect(() => {
    const fetchRestaurant = async () => {
      try {
        const response = await RestaurantApi.getById(id);
        setRestaurant(response.data);
      } catch (error) {
        console.error(`Failed to fetch restaurant with ID ${id}:`, error);
      }
    };

    fetchRestaurant();
  }, [id]); // Only depends on id

  // Fetch menus data
  useEffect(() => {
    const fetchMenus = async () => {
      if (!restaurant) return;
      
      try {
        const response = await RestaurantApi.getMenus(id);
        setMenus(response.data);
      } catch (error) {
        console.error(`Failed to fetch menus for restaurant with ID ${id}:`, error);
      }
    };

    fetchMenus();
  }, [id, restaurant]); // Only re-run when id or restaurant changes
  
  // Fetch weather data
  useEffect(() => {
    const fetchWeather = async () => {
      if (!restaurant || !menus.length) return;
      
      try {
        setIsLoading(true);
        const weatherData = {};
        
        // Get weather for each meal time
        if (restaurant.location) {
          for (const menu of menus) {
            if (menu.meals && menu.meals.length > 0) {
              for (const meal of menu.meals) {
                if (meal.startTime) {
                  const date = menu.date || new Date().toISOString().split('T')[0];
                  const timeKey = `${date}T${meal.startTime}`;
                  
                  if (!weatherData[timeKey]) {
                    try {
                      const response = await RestaurantApi.getWeather(
                        restaurant.location.id, 
                        date,
                        meal.startTime
                      );
                      weatherData[timeKey] = response.data;
                    } catch (weatherError) {
                      console.error(`Failed to fetch weather for time ${timeKey}:`, weatherError);
                    }
                  }
                }
              }
            }
          }
        }
        
        setWeather(weatherData);
      } catch (error) {
        console.error(`Failed to fetch weather data:`, error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchWeather();
  }, [menus]); // Only re-run when menus change

  const handleReservationClick = () => {
    if (user) {
      setShowReservationModal(true);
    } else {
      setShowLoginWarning(true);
    }
  };

  if (!restaurant) {
    return <div className="container mx-auto p-4">Loading restaurant details...</div>;
  }

  return (
    <div className="container mx-auto">
      {/* Restaurant Name */}
      <h1 className="text-4xl font-bold mb-4">{restaurant.name}</h1>
      
      {/* Restaurant Description */}
      {restaurant.description && (
        <div className="mb-8">
          <p className="text-lg">{restaurant.description}</p>
        </div>
      )}
      
      {/* Restaurant Info */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8 mb-10">
        <div className="card bg-base-100 shadow-lg p-6">
          <p className="mb-2">
            <i className="fas fa-users mr-2 text-primary"></i> 
            Capacity: {restaurant.capacity}
          </p>
          
          <p className="mb-2">
            <i className="fas fa-clock mr-2 text-primary"></i> 
            {restaurant.operatingHours || "Not available"}
          </p>
          
          <p className="mb-2">
            <i className="fas fa-phone mr-2 text-primary"></i> 
            {restaurant.contactInfo || "Not available"}
          </p>
        
          {restaurant.location ? (
            <>
              <p className="mb-2">
                <i className="fas fa-map-marker-alt mr-2 text-primary"></i> 
                {restaurant.location.name}
              </p>
              
              
              {/* Map placeholder */}
              <div className="mt-4 bg-gray-200 h-40 flex items-center justify-center rounded-lg">
                <i className="fas fa-map-marked-alt text-4xl text-gray-400"></i>
              </div>
            </>
          ) : (
            <p>Location information not available</p>
          )}
        </div>
      </div>
      
      
      {/* Menus Section */}
      <h2 className="text-3xl font-bold mb-8">Menus</h2>
      
      {menus.length > 0 ? (
        menus.map((menu) => (
          <div key={menu.id} className="card bg-base-100 shadow-lg mb-8 p-6">
            <h3 className="text-2xl font-bold mb-4">
              {menu.name} {menu.date && ` - ${new Date(menu.date).toLocaleDateString()}`}
            </h3>
            
            <p className="mb-4">{menu.description}</p>
            
            <h4 className="text-xl font-bold mb-4">Meals</h4>
            {menu.meals && menu.meals.length > 0 ? (
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
                {menu.meals.map((meal) => {
                  const timeKey = meal.startTime && menu.date ? 
                    `${menu.date}T${meal.startTime}` : null;
                  const mealWeather = timeKey ? weather[timeKey] : null;
                  
                  return (
                    <div key={meal.id} className="card bg-base-200 p-4">
                      <div className="flex justify-between items-center mb-2">
                        <h5 className="text-lg font-semibold">
                          {meal.name} {meal.mealType && `(${meal.mealType})`}
                        </h5>
                        <div className="badge badge-primary">
                          {meal.startTime}-{meal.endTime}
                        </div>
                      </div>
                      
                      <p className="mb-2">{meal.description}</p>
                      
                      {meal.price && (
                        <p className="text-right font-bold">{meal.price}</p>
                      )}
                      
                      {/* Weather for this meal's time */}
                      {mealWeather && (
                        <div className="flex items-center mt-2 text-sm">
                          <i className="fas fa-cloud-sun mr-2 text-primary"></i>
                          <span>
                            {mealWeather.temperature}°C, {mealWeather.conditions}
                          </span>
                        </div>
                      )}
                    </div>
                  );
                })}
              </div>
            ) : (
              <p>No meals available for this menu</p>
            )}
          </div>
        ))
      ) : (
        <div className="alert alert-info">
          <i className="fas fa-info-circle mr-2"></i>
          No menus available for this restaurant
        </div>
      )}
      {/* Make Reservation Button */}
      <div className="my-12 flex flex-col items-center">
        <button 
          onClick={handleReservationClick}
          className="btn btn-primary btn-lg"
        >
          <i className="fas fa-calendar-plus mr-2"></i>
          Make a Reservation
        </button>
        
        {showLoginWarning && !user && (
          <div className="mt-4 alert alert-warning">
            <i className="fas fa-exclamation-triangle mr-2"></i>
            You need to log in to make a reservation
          </div>
        )}
      </div>
      
      {/* Reservation Modal */}
      {showReservationModal && (
        <ReservationModal 
          restaurant={restaurant}
          isOpen={showReservationModal}
          onClose={() => setShowReservationModal(false)}
        />
      )}
    </div>
  );
};

export default RestaurantDetailPage; 