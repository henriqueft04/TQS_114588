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
      setIsStaff(false);
      return null;
    }
    
    // Ensure we normalize the role for consistent comparison
    const role = userData.role ? userData.role.toUpperCase() : '';
    userData.normalizedRole = role;
    
    // Set staff flag
    setIsStaff(role === 'STAFF');
    
    return userData;
  };

  const login = async (email, password, role) => {
    try {
      setError(null);
      const response = await AuthApi.login(email, password, role);
      const userData = response.data.user || response.data; // Handle both formats
      const processedUser = updateUserRole(userData);
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
      // Don't even try to fetch user data if no token exists
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
      } catch (error) {
        console.error('Error fetching user:', error);
        setUser(null);
        setIsStaff(false);
        // If there was an authentication error, clear the token
        if (error.response?.status === 401) {
          localStorage.removeItem('authToken');
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