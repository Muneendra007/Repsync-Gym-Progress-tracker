import React, { useState, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Dumbbell, LayoutDashboard, Compass, BookOpen, Trophy, LogOut, User, Settings, Flame } from 'lucide-react';
import ProfileOptionsModal from './ProfileOptionsModal';

const Navbar = () => {
 const { user, isAuthenticated, logout } = useAuth();
 const location = useLocation();
 const [isProfileModalOpen, setIsProfileModalOpen] = useState(false);

 useEffect(() => {
 setIsProfileModalOpen(false);
 }, [location.pathname]);

 if (!isAuthenticated) return null;

 const navItems = [
 { name: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
 { name: 'Workout Strategy', path: '/workouts/plan', icon: Compass },
 { name: 'Exercise Library', path: '/exercises', icon: BookOpen },
 { name: 'PR Analytics', path: '/analytics', icon: Trophy },
 { name: 'Nutrition', path: '/nutrition', icon: Flame },
 ];

 return (
 <nav className="sticky top-0 z-50 bg-gym-dark/90 backdrop-blur-lg border-b border-white/10 px-6 py-4">
 <div className="w-full flex items-center justify-between">
 <Link to="/dashboard" className="flex items-center gap-3 group">
 <div className="p-2 rounded-xl bg-gradient-to-tr from-gym-accent to-gym-purple text-gym-dark transition-transform duration-300 group-hover:scale-110">
 <Dumbbell className="w-6 h-6" />
 </div>
 <span className="text-xl font-black tracking-wider bg-gradient-to-r from-white via-gym-accent to-gym-purple bg-clip-text text-transparent">
 REPSYNC <span className="text-xs uppercase px-2 py-0.5 rounded-full bg-gym-accent/10 text-gym-accent border border-gym-accent/30 ml-1 font-semibold">Gym</span>
 </span>
 </Link>

 <div className="hidden md:flex items-center gap-2">
 {navItems.map((item) => {
 const Icon = item.icon;
 const isActive = location.pathname.startsWith(item.path);
 return (
 <Link
 key={item.name}
 to={item.path}
 className={`flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-semibold transition-all duration-300 ${
 isActive
 ? 'bg-gym-accent/15 text-gym-accent border border-gym-accent/40 shadow-lg shadow-gym-accent/10'
 : 'text-gray-400 hover:text-white hover:bg-white/5'
 }`}
 >
 <Icon className="w-4 h-4" />
 {item.name}
 </Link>
 );
 })}
 </div>

 <div className="flex items-center gap-4">
 <button
 onClick={() => setIsProfileModalOpen(true)}
 className="flex items-center gap-2 px-3 py-1.5 rounded-xl bg-gym-card hover:bg-gym-accent/15 border border-white/10 hover:border-gym-accent/50 transition-all group"
 title="Edit Profile"
 >
 <User className="w-4 h-4 text-gym-accent group-hover:scale-110 transition-transform" />
 <span className="text-sm font-bold text-gray-200 group-hover:text-white">{user?.username || 'Athlete'}</span>
 </button>
 <button
 onClick={logout}
 className="p-2 rounded-xl text-gray-400 hover:text-gym-danger hover:bg-gym-danger/10 transition-colors"
 title="Log out"
 >
 <LogOut className="w-5 h-5" />
 </button>
 </div>
 </div>
 <ProfileOptionsModal isOpen={isProfileModalOpen} onClose={() => setIsProfileModalOpen(false)} />
 </nav>
 );
};

export default Navbar;
