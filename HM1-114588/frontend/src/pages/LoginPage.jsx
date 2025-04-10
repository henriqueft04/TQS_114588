import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth.jsx';

const LoginPage = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('CUSTOMER');
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    console.log('Attempting login with:', { email, password, role });
    const user = await login(email, password, role);
    if (user) {
      console.log('Login successful for user:', user);
      console.log('User role:', user.role);
      console.log('Is Staff:', user.role.toUpperCase() === 'STAFF');
      
      // Redirect based on role
      if (user.role.toUpperCase() === 'STAFF') {
        console.log('Redirecting staff user to checkin page');
        navigate('/checkin');
      } else {
        console.log('Redirecting customer user to profile page');
        navigate('/profile');
      }
    } else {
      console.error('Login failed');
      setError('Invalid email or password');
    }
  };

  return (
    <div className="container mx-auto max-w-md">
      <div className="card bg-base-100 shadow-xl p-6 mt-10">
        <h1 className="text-3xl font-bold mb-6 text-center">Log In</h1>
        
        {error && (
          <div className="alert alert-error mb-6">
            <i className="fas fa-exclamation-circle mr-2"></i>
            <span>{error}</span>
          </div>
        )}
        
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label htmlFor="email" className="block font-medium mb-2">Email:</label>
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
            <label htmlFor="password" className="block font-medium mb-2">Password:</label>
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
            <label htmlFor="role" className="block font-medium mb-2">Role:</label>
            <select
              id="role"
              value={role}
              onChange={(e) => setRole(e.target.value)}
              className="select select-bordered w-full"
              required
            >
              <option value="CUSTOMER">User</option>
              <option value="STAFF">Staff</option>
            </select>
          </div>
          
          <div className="flex justify-between items-center">
            <button type="submit" className="btn btn-primary">
              <i className="fas fa-sign-in-alt mr-2"></i>
              Log In
            </button>
            
            <Link to="/register" className="link link-primary">
              Don't have an account? Register
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
};

export default LoginPage; 