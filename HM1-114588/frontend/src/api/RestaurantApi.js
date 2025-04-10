import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api';

const RestaurantApi = {
  // Restaurant endpoints
  getAll: () => {
    return axios.get(`${BASE_URL}/restaurants`);
  },

  getById: (id) => {
    return axios.get(`${BASE_URL}/restaurants/${id}`);
  },
  
  createRestaurant: (restaurantData) => {
    return axios.post(`${BASE_URL}/restaurants`, restaurantData);
  },
  
  updateRestaurant: (id, restaurantData) => {
    return axios.put(`${BASE_URL}/restaurants/${id}`, restaurantData);
  },

  // Capacity endpoints
  getAvailableCapacity: (id, startTime, endTime) => {
    return axios.get(`${BASE_URL}/restaurants/${id}/available-capacity`, {
      params: { startTime, endTime }
    });
  },

  checkCapacity: (id, reservationTime, partySize) => {
    console.log(`Checking capacity for restaurant ${id} at ${reservationTime} for ${partySize} people`);
    return axios.post(`${BASE_URL}/restaurants/${id}/check-capacity`, {
      reservationTime,
      partySize
    })
    .then(response => {
      console.log('Capacity check response:', response.data);
      return response;
    })
    .catch(error => {
      console.error('Error checking capacity:', error.response?.data || error.message);
      console.error('Request data:', { reservationTime, partySize });
      throw error;
    });
  },

  // Menu endpoints
  getMenus: (restaurantId) => {
    return axios.get(`${BASE_URL}/menus/restaurant/${restaurantId}`);
  },
  
  createMenu: (menuData) => {
    return axios.post(`${BASE_URL}/menus`, menuData);
  },
  
  updateMenu: (id, menuData) => {
    return axios.put(`${BASE_URL}/menus/${id}`, menuData);
  },

  // Schedule endpoints
  getSchedules: (restaurantId) => {
    return axios.get(`${BASE_URL}/schedules/restaurant/${restaurantId}`);
  },
  
  createSchedule: (scheduleData) => {
    return axios.post(`${BASE_URL}/schedules`, scheduleData);
  },
  
  // Meal endpoints
  getMeals: (restaurantId) => {
    return axios.get(`${BASE_URL}/meals/restaurant/${restaurantId}`);
  },
  
  createMeal: (mealData) => {
    return axios.post(`${BASE_URL}/meals`, mealData);
  },

  // Reservation endpoints
  createReservation: (reservationData) => {
    console.log('Creating reservation with data:', JSON.stringify(reservationData, null, 2));
    
    // Create copy with simplified object values for better logging
    const logData = {...reservationData};
    if (logData.restaurant) logData.restaurantId = logData.restaurant.id;
    if (logData.user) logData.userId = logData.user.id;
    console.log('Simplified data for debugging:', logData);
    
    return axios.post(`${BASE_URL}/reservations`, reservationData)
      .then(response => {
        console.log('Reservation created successfully:', response.data);
        return response;
      })
      .catch(error => {
        console.error('Error creating reservation:', error.response || error);
        console.error('Full error response:', error.response?.data);
        
        // Try to extract more detailed error message
        const errorMessage = error.response?.data?.message || 
                            error.response?.data?.error ||
                            error.response?.data?.errors ||
                            error.message;
        console.error('Error details:', errorMessage);
        
        // Log request that failed
        console.error('Request config:', error.config);
        console.error('Status:', error.response?.status);
        console.error('Headers:', error.response?.headers);
        throw error;
      });
  },

  getReservation: (id) => {
    return axios.get(`${BASE_URL}/reservations/${id}`);
  },
  
  getReservationByToken: (token) => {
    return axios.get(`${BASE_URL}/reservations/token/${token}`);
  },

  getReservationsByUser: async (userId) => {
    console.log(`Fetching reservations for user ID: ${userId}`);
    try {
      const response = await axios.get(`${BASE_URL}/reservations/user/${userId}`);
      console.log(`Found ${response.data.length} reservations for user ${userId}`);
      return response;
    } catch (error) {
      console.warn(`Error fetching reservations for user ${userId}:`, error.response?.status || error);
      // If it's a 404, return an empty array
      if (error.response && error.response.status === 404) {
        console.log('No reservations found, returning empty array');
        return { data: [] };
      }
      // Otherwise, rethrow the error
      throw error;
    }
  },
  
  getReservationsByRestaurant: (restaurantId) => {
    return axios.get(`${BASE_URL}/reservations/restaurant/${restaurantId}`);
  },

  cancelReservation: (id) => {
    console.log(`Canceling reservation with ID: ${id}`);
    return axios.put(`${BASE_URL}/reservations/${id}/cancel`)
      .then(response => {
        console.log('Reservation canceled successfully');
        return response;
      })
      .catch(error => {
        console.error('Error canceling reservation:', error.response || error);
        throw error;
      });
  },
  
  confirmReservation: (id) => {
    return axios.put(`${BASE_URL}/reservations/${id}/confirm`);
  },
  
  checkInReservation: (token) => {
    return axios.put(`${BASE_URL}/reservations/check-in/${token}`);
  },

  // Weather endpoints
  getWeather: (locationId, date) => {
    return axios.get(`${BASE_URL}/weather/forecast`, {
      params: { locationId, date }
    });
  },
  
  getWeatherCacheStats: () => {
    return axios.get(`${BASE_URL}/weather/cache-stats`);
  }
};

export default RestaurantApi; 