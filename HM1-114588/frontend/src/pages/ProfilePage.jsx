import React from 'react';
import { useAuth } from '../hooks/useAuth';
import { Link } from 'react-router-dom';

export default function ProfilePage() {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <div className="loading loading-spinner loading-lg"></div>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="container mx-auto p-4">
        <div className="alert alert-warning">
          <div>
            <i className="fas fa-exclamation-triangle mr-2"></i>
            <span>Please log in to view your profile.</span>
          </div>
          <div className="flex-none">
            <Link to="/login" className="btn btn-sm btn-primary">Login</Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto p-4">
      <div className="card bg-base-100 shadow-xl">
        <div className="card-body">
          <h2 className="card-title text-2xl">Profile</h2>
          
          <div className="avatar placeholder my-4">
            <div className="bg-neutral-focus text-neutral-content rounded-full w-24">
              <span className="text-3xl">{user.name ? user.name.charAt(0) : user.email.charAt(0)}</span>
            </div>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="form-control">
              <label className="label">
                <span className="label-text font-bold">Name</span>
              </label>
              <div className="input input-bordered flex items-center">
                <i className="fas fa-user mr-2"></i>
                <span>{user.name || 'Not provided'}</span>
              </div>
            </div>
            
            <div className="form-control">
              <label className="label">
                <span className="label-text font-bold">Email</span>
              </label>
              <div className="input input-bordered flex items-center">
                <i className="fas fa-envelope mr-2"></i>
                <span>{user.email}</span>
              </div>
            </div>
            
            <div className="form-control">
              <label className="label">
                <span className="label-text font-bold">Role</span>
              </label>
              <div className="input input-bordered flex items-center">
                <i className="fas fa-id-badge mr-2"></i>
                <span>{user.role || 'User'}</span>
              </div>
            </div>
            
            <div className="form-control">
              <label className="label">
                <span className="label-text font-bold">Member Since</span>
              </label>
              <div className="input input-bordered flex items-center">
                <i className="fas fa-calendar mr-2"></i>
                <span>{user.createdAt ? new Date(user.createdAt).toLocaleDateString() : 'Unknown'}</span>
              </div>
            </div>
          </div>
          
          <div className="divider my-4">Account Actions</div>
          
          <div className="card-actions">
            <Link to="/reservations" className="btn btn-primary">
              <i className="fas fa-calendar-alt mr-2"></i>
              My Reservations
            </Link>
            <button className="btn btn-outline">
              <i className="fas fa-edit mr-2"></i>
              Edit Profile
            </button>
          </div>
        </div>
      </div>
    </div>
  );
} 