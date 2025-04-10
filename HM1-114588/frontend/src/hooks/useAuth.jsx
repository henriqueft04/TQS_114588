import { useState, useEffect, useContext, createContext } from 'react';
import AuthApi from '../api/AuthApi.js';

const authContext = createContext();

// Provider component that wraps your app and makes auth object available
export function ProvideAuth({ children }) {
  const auth = useProvideAuth();
  return <authContext.Provider value={auth}>{children}</authContext.Provider>;
}

// Hook for child components to get the auth object and re-render when it changes
export const useAuth = () => {
  return useContext(authContext);
};

// Provider hook that creates auth object and handles state
function useProvideAuth() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isStaff, setIsStaff] = useState(false);

  // Update user role state when user changes
  const updateUserRole = (userData) => {
    if (!userData) {
      console.log('updateUserRole called with no userData, setting isStaff to false');
      setIsStaff(false);
      return null;
    }
    
    // Ensure we normalize the role for consistent comparison
    const role = userData.role ? userData.role.toUpperCase() : '';
    userData.normalizedRole = role;
    console.log('updateUserRole processing user with role:', userData.role);
    console.log('Normalized role to:', role);
    
    // Set staff flag
    const staffFlag = role === 'STAFF';
    console.log('Setting isStaff flag to:', staffFlag);
    setIsStaff(staffFlag);
    
    return userData;
  };

  const login = async (email, password, role) => {
    try {
      setError(null);
      const response = await AuthApi.login(email, password, role);
      const userData = response.data.user || response.data; // Handle both formats
      const processedUser = updateUserRole(userData);
      
      // Store user data in localStorage for persistence
      localStorage.setItem('userData', JSON.stringify(processedUser));
      
      setUser(processedUser);
      return processedUser;
    } catch (error) {
      const errorMessage = error.response?.data || 'Failed to log in';
      console.error('Failed to log in:', errorMessage);
      setError(errorMessage);
      return null;
    }
  };

  const logout = async () => {
    try {
      await AuthApi.logout();
      setUser(null);
      setIsStaff(false);
      
      // Clear user data from localStorage
      localStorage.removeItem('userData');
      
      // Clear any local state that should be reset on logout
    } catch (error) {
      console.error('Failed to log out:', error);
    }
  };
  
  const register = async (userData) => {
    try {
      setError(null);
      const response = await AuthApi.register(userData);
      const responseData = response.data.user || response.data; // Handle both formats
      const processedUser = updateUserRole(responseData);
      setUser(processedUser);
      return processedUser;
    } catch (error) {
      const errorMessage = error.response?.data || 'Failed to register';
      console.error('Failed to register:', errorMessage);
      setError(errorMessage);
      return null;
    }
  };

  // Check if the user is already authenticated when the component mounts
  useEffect(() => {
    const getUser = async () => {
      // Try to get user data from localStorage first
      const storedUserData = localStorage.getItem('userData');
      
      if (storedUserData) {
        try {
          const userData = JSON.parse(storedUserData);
          const processedUser = updateUserRole(userData);
          setUser(processedUser);
          setLoading(false);
          return;
        } catch (error) {
          console.error('Error parsing stored user data:', error);
          // Clear invalid data
          localStorage.removeItem('userData');
        }
      }
      
      // If no local data or parsing failed, check with the server if we can
      if (!AuthApi.isAuthenticated()) {
        setUser(null);
        setIsStaff(false);
        setLoading(false);
        return;
      }
      
      setLoading(true);
      try {
        const response = await AuthApi.getUser();
        const userData = updateUserRole(response.data);
        setUser(userData);
        // Update localStorage with fresh data
        localStorage.setItem('userData', JSON.stringify(userData));
      } catch (error) {
        console.error('Error fetching user:', error);
        setUser(null);
        setIsStaff(false);
        // If there was an authentication error, clear the token
        if (error.response?.status === 401) {
          localStorage.removeItem('authToken');
          localStorage.removeItem('userData');
        }
      } finally {
        setLoading(false);
      }
    };
    
    getUser();
  }, []);

  // Return the user object and auth methods
  return {
    user,
    login,
    logout,
    register,
    loading,
    error,
    isAuthenticated: !!user,
    isStaff
  };
} 