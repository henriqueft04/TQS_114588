import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import RestaurantPage from './pages/RestaurantPage';
import RestaurantDetailPage from './pages/RestaurantDetailPage';
import ReservationConfirmationPage from './pages/ReservationConfirmationPage';
import ProfilePage from './pages/ProfilePage';
import ReservationsPage from './pages/ReservationsPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import CheckInPage from './pages/CheckInPage';
import ManageRestaurantPage from './pages/ManageRestaurantPage';
import Navbar from './components/Navbar';
import PrivateRoute from './components/PrivateRoute';
import StaffRoute from './components/StaffRoute';

function App() {
  return (
    <Router>
      <div className="min-h-screen bg-base-200 flex flex-col">
        <header className="bg-primary text-primary-content shadow-lg">
          <div className="container mx-auto p-4">
            <h1 className="text-3xl font-bold">Moliceiro University Campus</h1>
          </div>
        </header>
        
        <main className="flex-grow p-4">
          <Routes>
            <Route path="/" element={<RestaurantPage />} />
            <Route path="/restaurants/:id" element={<RestaurantDetailPage />} />
            <Route path="/reservations/:id" element={<ReservationConfirmationPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route
              path="/profile"
              element={
                <PrivateRoute>
                  <ProfilePage />
                </PrivateRoute>
              }
            />
            <Route 
              path="/reservations"
              element={
                <PrivateRoute>
                  <ReservationsPage />  
                </PrivateRoute>
              }
            />
            <Route 
              path="/checkin"
              element={
                <StaffRoute>
                  <CheckInPage />  
                </StaffRoute>
              }
            />
            <Route 
              path="/manage/:id"
              element={
                <StaffRoute>
                  <ManageRestaurantPage />  
                </StaffRoute>
              }
            />
          </Routes>
        </main>
        
        <Navbar />
      </div>
    </Router>
  );
}

export default App;
