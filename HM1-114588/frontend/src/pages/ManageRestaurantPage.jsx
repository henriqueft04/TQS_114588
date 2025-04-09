import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import RestaurantApi from '../api/RestaurantApi';

const ManageRestaurantPage = () => {
  const { id } = useParams();
  const [restaurant, setRestaurant] = useState(null);
  const [schedules, setSchedules] = useState([]);
  const [meals, setMeals] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchRestaurantData = async () => {
      try {
        const restaurantResponse = await RestaurantApi.getById(id);
        setRestaurant(restaurantResponse.data);

        const schedulesResponse = await RestaurantApi.getSchedules(id);
        setSchedules(schedulesResponse.data);

        const mealsResponse = await RestaurantApi.getMeals(id);
        setMeals(mealsResponse.data);
      } catch (error) {
        setError('Failed to load restaurant data');
      } finally {
        setIsLoading(false);
      }
    };

    fetchRestaurantData();
  }, [id]);

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div className="alert alert-error">{error}</div>;
  }

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-3xl font-bold mb-4">Manage Restaurant: {restaurant.name}</h1>

      <div className="mb-8">
        <h2 className="text-2xl font-bold mb-4">Restaurant Details</h2>
        {/* TODO: Add form to edit restaurant details */}
      </div>

      <div className="mb-8">
        <h2 className="text-2xl font-bold mb-4">Schedules</h2>
        {/* TODO: Add form to add/edit schedules */}
        {schedules.map((schedule) => (
          <div key={schedule.id} className="mb-4">
            {/* TODO: Display schedule details */}
          </div>
        ))}
      </div>

      <div className="mb-8">
        <h2 className="text-2xl font-bold mb-4">Meals</h2>
        {/* TODO: Add form to add/edit meals */}
        {meals.map((meal) => (
          <div key={meal.id} className="mb-4">
            {/* TODO: Display meal details */}
          </div>
        ))}
      </div>
    </div>
  );
};

export default ManageRestaurantPage; 