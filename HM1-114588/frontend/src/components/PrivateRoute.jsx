import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth.jsx';

const PrivateRoute = ({ children }) => {
  const { user } = useAuth();
  
  if (!user) {
    // If not authenticated, redirect to login
    return <Navigate to="/login" replace />;
  }

  // If authenticated, render the child component
  return children;
};

export default PrivateRoute; 