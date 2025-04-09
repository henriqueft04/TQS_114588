import { useState } from 'react'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import RestaurantPage from './pages/RestaurantPage'
import Navbar from './components/Navbar'
import ProfilePage from './pages/ProfilePage'
import ReservationsPage from './pages/ReservationsPage'
import RestaurantDetailPage from './pages/RestaurantDetailPage'

function App() {
  const [count, setCount] = useState(0)

  return (
    <Router>
      <Navbar />
      <Routes>
        <Route path="/" element={<RestaurantPage />} />
        <Route path="/profile" element={<ProfilePage />} />
        <Route path="/reservations" element={<ReservationsPage />} />
        <Route path="/restaurants/:id" element={<RestaurantDetailPage />} />
        {/* TODO: Add routes for other pages */}
      </Routes>
    </Router>
  )
}

export default App
