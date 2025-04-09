import React from 'react'
import { Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth.jsx';

export default function Navbar() {
    const { user, logout } = useAuth();

    // Get initial to display in avatar
    const getInitial = () => {
        if (user?.name) return user.name.charAt(0);
        if (user?.email) return user.email.charAt(0);
        return 'U'; // Default fallback
    };

    return (
        <div className="btm-nav bg-base-200 flex flex-row justify-evenly items-center w-full">
            <Link to="/" className="text-primary ">
                <i className="fas fa-utensils text-xl"></i>
                <span className="btm-nav-label">Restaurants</span>
            </Link>
            
            {user ? (
                <>
                    <Link to="/profile" className="text-primary">
                        <div className="avatar placeholder">
                            <div className="bg-neutral-focus text-neutral-content rounded-full w-8">
                                <span>{getInitial()}</span>
                            </div>
                        </div>
                        <span className="btm-nav-label">Profile</span>
                    </Link>
                    
                    <Link to="/reservations" className="text-primary">
                        <i className="fas fa-calendar-alt text-xl"></i>
                        <span className="btm-nav-label">Reservations</span>
                    </Link>
                    
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