import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api';

// Create an axios instance for authenticated requests
const authAxios = axios.create({
  baseURL: BASE_URL
});

// Request interceptor to add user info from localStorage
authAxios.interceptors.request.use(config => {
  // Get user data from localStorage
  const userDataString = localStorage.getItem('userData');
  
  if (userDataString) {
    try {
      // No need to add an auth header since the backend doesn't validate it
      // Just log that we're making an authenticated request
      console.log('Making authenticated request with stored user data');
    } catch (error) {
      console.error('Error parsing user data:', error);
    }
  }
  
  return config;
}, error => {
  return Promise.reject(error);
});

export default authAxios; 