import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth.jsx';

const StaffRoute = ({ children }) => {
  const { user, loading, isStaff } = useAuth();
  
  console.log('StaffRoute rendering with user:', user);
  console.log('StaffRoute loading state:', loading);
  console.log('StaffRoute isStaff flag:', isStaff);
  
  // Show loading state while checking auth
  if (loading) {
    console.log('StaffRoute - auth still loading, showing spinner');
    return (
      <div className="container mx-auto text-center py-12">
        <span className="loading loading-spinner loading-lg"></span>
      </div>
    );
  }
  
  // If not authenticated or not a staff member, redirect to login
  if (!user || !isStaff) {
    console.log('Access denied to staff route - redirecting to login');
    console.log('User:', user);
    console.log('IsStaff:', isStaff);
    if (user) {
      console.log('User role:', user.role);
      console.log('User normalized role:', user.normalizedRole);
    }
    return <Navigate to="/login" replace />;
  }

  console.log('Access granted to staff route - rendering children');
  // If user is a staff member, render the child component
  return children;
};

export default StaffRoute; 