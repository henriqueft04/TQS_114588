import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api';

// Create an axios instance that will automatically include the token
const authAxios = axios.create({
  baseURL: BASE_URL
});

// Request interceptor to add token to all requests
authAxios.interceptors.request.use(config => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers['Authorization'] = token;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

const AuthApi = {
  login: async (email, password, role) => {
    const response = await axios.post(`${BASE_URL}/auth/login`, { email, password, role });
    return response;
  },

  logout: async () => {
    const response = await authAxios.post(`${BASE_URL}/auth/logout`);
    localStorage.removeItem('userData');
    return response;
  },
  
  register: async (userData) => {
    const response = await axios.post(`${BASE_URL}/auth/register`, userData);
    return response;
  },
  
  getUser: () => {
    return authAxios.get(`${BASE_URL}/auth/user`);
  },
  
  // Check if user is authenticated
  isAuthenticated: () => {
    return localStorage.getItem('userData') !== null;
  }
};

export default AuthApi; 