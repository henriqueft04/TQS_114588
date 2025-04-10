import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth.jsx';

const StaffRoute = ({ children }) => {
  const { user, loading } = useAuth();
  
  // Show loading state while checking auth
  if (loading) {
    return (
      <div className="container mx-auto text-center py-12">
        <span className="loading loading-spinner loading-lg"></span>
      </div>
    );
  }
  
  // If not authenticated or not a staff member, redirect to login
  if (!user || user.role !== 'staff') {
    return <Navigate to="/login" replace />;
  }

  // If user is a staff member, render the child component
  return children;
};

export default StaffRoute; 