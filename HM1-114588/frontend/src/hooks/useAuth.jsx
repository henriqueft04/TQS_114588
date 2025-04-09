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

  const login = async (email, password, role) => {
    try {
      const response = await AuthApi.login(email, password, role);
      setUser(response.data.user);
      return response.data.user;
    } catch (error) {
      console.error('Failed to log in:', error);
      return null;
    }
  };

  const logout = async () => {
    try {
      await AuthApi.logout();
      setUser(null);
    } catch (error) {
      console.error('Failed to log out:', error);
    }
  };
  
  const register = async (userData) => {
    try {
      const response = await AuthApi.register(userData);
      setUser(response.data.user);
      return response.data.user;
    } catch (error) {
      console.error('Failed to register:', error);
      return null;
    }
  };

  // Subscribe to user on mount
  // Because this sets state in the callback it will cause any
  // component that utilizes this hook to re-render with the
  // latest auth object.
  useEffect(() => {
    const getUser = async () => {
      setLoading(true);
      try {
        const response = await AuthApi.getUser();
        setUser(response.data);
      } catch (error) {
        setUser(null);
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
    loading
  };
} 