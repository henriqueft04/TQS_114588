import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api';

const AuthApi = {
  login: (email, password, role) => {
    return axios.post(`${BASE_URL}/auth/login`, { email, password, role });
  },

  logout: () => {
    return axios.post(`${BASE_URL}/auth/logout`);
  },
  
  register: (userData) => {
    return axios.post(`${BASE_URL}/auth/register`, userData);
  },
  
  getUser: () => {
    return axios.get(`${BASE_URL}/auth/user`);
  },
};

export default AuthApi; 