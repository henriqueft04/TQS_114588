import React from 'react'
import { Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth.jsx';

export default function Navbar() {
    const { user, logout, isStaff } = useAuth();

    // Get initial to display in avatar
    const getInitial = () => {
        if (user?.name) return user.name.charAt(0);
        if (user?.email) return user.email.charAt(0);
        return 'U'; // Default fallback
    };
    
    // Render staff-specific links
    const renderStaffLinks = () => {
        return (
            <>
                <Link to="/checkin" className="text-primary">
                    <i className="fas fa-clipboard-check text-xl"></i>
                    <span className="btm-nav-label">Check In</span>
                </Link>
                <Link to="/restaurants/manage" className="text-primary">
                    <i className="fas fa-edit text-xl"></i>
                    <span className="btm-nav-label">Manage</span>
                </Link>
            </>
        );
    };

    // Render user-specific links
    const renderUserLinks = () => {
        return (
            <>
                <Link to="/profile" className="text-primary">
                    <i className="fas fa-user text-xl"></i>
                    <span className="btm-nav-label">Profile</span>
                </Link>
                <Link to="/reservations" className="text-primary">
                    <i className="fas fa-calendar-alt text-xl"></i>
                    <span className="btm-nav-label">Reservations</span>
                </Link>
            </>
        );
    };

    return (
        <div className="dock bg-base-200 flex flex-row justify-evenly items-center w-full">
            <Link to="/" className="text-primary ">
                <i className="fas fa-utensils text-xl"></i>
                <span className="btm-nav-label">Restaurants</span>
            </Link>
            
            {user ? (
                <>
                    {isStaff ? renderStaffLinks() : renderUserLinks()}
                    
                    <button onClick={logout} className="text-primary">
                        <i className="fas fa-sign-out-alt text-xl"></i>
                        <span className="btm-nav-label">Logout</span>
                    </button>
                    
                </>
            ) : (
                <>
                    <Link to="/login" className="text-primary">
                        <i className="fas fa-sign-in-alt text-xl"></i>
                        <span className="btm-nav-label">Login</span>
                    </Link>
                    <Link to="/register" className="text-primary">
                        <i className="fas fa-user-plus text-xl"></i>
                        <span className="btm-nav-label">Register</span>
                    </Link>
                </>
            )}
        </div>
    )
}