import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api';

const RestaurantApi = {
  getAll: () => {
    return axios.get(`${BASE_URL}/restaurants`);
  },

  getById: (id) => {
    return axios.get(`${BASE_URL}/restaurants/${id}`);
  },

  getAvailableCapacity: (id, startTime, endTime) => {
    return axios.get(`${BASE_URL}/restaurants/${id}/available-capacity`, {
      params: { startTime, endTime }
    });
  },

  checkCapacity: (id, reservationTime, partySize) => {
    return axios.post(`${BASE_URL}/restaurants/${id}/check-capacity`, {
      reservationTime,
      partySize
    });
  },

  getMenus: (restaurantId) => {
    return axios.get(`${BASE_URL}/menus/restaurant/${restaurantId}`);
  },

  createReservation: (reservationData) => {
    return axios.post(`${BASE_URL}/reservations`, reservationData);
  },

  getReservation: (id) => {
    return axios.get(`${BASE_URL}/reservations/${id}`);
  },

  getReservationsByUser: (userId) => {
    return axios.get(`${BASE_URL}/reservations/user/${userId}`);
  },

  cancelReservation: (id) => {
    return axios.put(`${BASE_URL}/reservations/${id}/cancel`);
  },

  getWeather: (locationId, date) => {
    return axios.get(`${BASE_URL}/weather/forecast`, {
      params: { locationId, date }
    });
  },
};

export default RestaurantApi; 