import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth.jsx';

const RegisterPage = () => {
  const navigate = useNavigate();
  const { register } = useAuth();
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [role, setRole] = useState('user');
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    
    if (password !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }
    
    const user = await register({
      name,
      email,
      phone,
      password,
      role
    });
    
    if (user) {
      // Redirect to profile page on successful registration
      navigate('/profile');
    } else {
      setError('Failed to create account');
    }
  };

  return (
    <div className="container mx-auto max-w-md">
      <div className="card bg-base-100 shadow-xl p-6 mt-10">
        <h1 className="text-3xl font-bold mb-6 text-center">Register</h1>
        
        {error && (
          <div className="alert alert-error mb-6">
            <i className="fas fa-exclamation-circle mr-2"></i>
            <span>{error}</span>
          </div>
        )}
        
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label htmlFor="name" className="block font-medium mb-2">
              Full Name: <span className="text-error">*</span>
            </label>
            <input
              type="text"
              id="name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="input input-bordered w-full"
              required
            />
          </div>
          
          <div className="mb-4">
            <label htmlFor="email" className="block font-medium mb-2">
              Email: <span className="text-error">*</span>
            </label>
            <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="input input-bordered w-full"
              required
            />
          </div>
          
          <div className="mb-4">
            <label htmlFor="phone" className="block font-medium mb-2">
              Phone Number: <span className="text-gray-400">(Optional)</span>
            </label>
            <input
              type="tel"
              id="phone"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              className="input input-bordered w-full"
            />
          </div>
          
          <div className="mb-4">
            <label htmlFor="role" className="block font-medium mb-2">
              Role: <span className="text-error">*</span>
            </label>
            <select
              id="role"
              value={role}
              onChange={(e) => setRole(e.target.value)}
              className="select select-bordered w-full"
              required
            >
              <option value="user">User</option>
              <option value="staff">Staff</option>
            </select>
          </div>
          
          <div className="mb-4">
            <label htmlFor="password" className="block font-medium mb-2">
              Password: <span className="text-error">*</span>
            </label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="input input-bordered w-full"
              required
            />
          </div>
          
          <div className="mb-6">
            <label htmlFor="confirmPassword" className="block font-medium mb-2">
              Confirm Password: <span className="text-error">*</span>
            </label>
            <input
              type="password"
              id="confirmPassword"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              className="input input-bordered w-full"
              required
            />
          </div>
          
          <div className="flex justify-between items-center">
            <button type="submit" className="btn btn-primary">
              <i className="fas fa-user-plus mr-2"></i>
              Register
            </button>
            
            <Link to="/login" className="link link-primary">
              Already have an account? Log In
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
};

export default RegisterPage; 