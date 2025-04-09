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
  }
};

export default RestaurantApi; 