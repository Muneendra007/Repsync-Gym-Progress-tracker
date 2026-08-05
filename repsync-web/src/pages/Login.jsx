import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Dumbbell, Lock, User, AlertCircle, CheckCircle, ArrowRight } from 'lucide-react';

const Login = () => {
 const [username, setUsername] = useState('');
 const [password, setPassword] = useState('');
 const [error, setError] = useState('');
 const { login, loading } = useAuth();
 const navigate = useNavigate();
 const location = useLocation();
 const successMessage = location.state?.successMessage;

 const handleSubmit = async (e) => {
 e.preventDefault();
 e.stopPropagation();
 setError('');
 const res = await login(username, password);
 if (res.success) {
 navigate('/dashboard');
 } else {
 setError(res.message);
 }
 };

 return (
 <div className="min-h-screen flex items-center justify-center px-4 py-12 relative overflow-hidden">
 {/* Cyberpunk Glow Background Effects */}
 <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-gym-accent/10 rounded-full blur-3xl pointer-events-none" />
 <div className="absolute bottom-1/4 right-1/4 w-96 h-96 bg-gym-purple/10 rounded-full blur-3xl pointer-events-none" />

 <div className="w-full max-w-md">
 <div className="text-center mb-8">
 <div className="inline-flex p-3 rounded-2xl bg-gradient-to-tr from-gym-accent to-gym-purple text-gym-dark mb-4 shadow-xl shadow-gym-accent/20">
 <Dumbbell className="w-10 h-10" />
 </div>
 <h1 className="text-3xl font-black tracking-wider text-white">
 REPSYNC
 </h1>
 <p className="text-gray-400 mt-2 text-sm">
 Sign in to access your goal-driven workout strategy & PR analytics
 </p>
 </div>

 <div className="glass-card">
 <form onSubmit={handleSubmit} className="space-y-6">
 {successMessage && (
 <div className="flex items-center gap-3 p-4 rounded-xl bg-gym-success/10 border border-gym-success/30 text-gym-success text-sm">
 <CheckCircle className="w-5 h-5 flex-shrink-0" />
 <span>{successMessage}</span>
 </div>
 )}

 {error && (
 <div className="flex items-center gap-3 p-4 rounded-xl bg-gym-danger/10 border border-gym-danger/30 text-gym-danger text-sm">
 <AlertCircle className="w-5 h-5 flex-shrink-0" />
 <span>{error}</span>
 </div>
 )}

 <div>
 <label className="block text-sm font-semibold text-gray-300 mb-2">
 Username or Email
 </label>
 <div className="relative">
 <User className="absolute left-4 top-3.5 w-5 h-5 text-gray-400" />
 <input
 type="text"
 required
 value={username}
 onChange={(e) => setUsername(e.target.value)}
 placeholder="enter your username"
 className="w-full pl-12 pr-4 py-3 rounded-xl bg-gym-dark/80 border border-white/10 text-white placeholder-gray-500 focus:border-gym-accent focus:outline-none transition-colors"
 />
 </div>
 </div>

 <div>
 <label className="block text-sm font-semibold text-gray-300 mb-2">
 Password
 </label>
 <div className="relative">
 <Lock className="absolute left-4 top-3.5 w-5 h-5 text-gray-400" />
 <input
 type="password"
 required
 value={password}
 onChange={(e) => setPassword(e.target.value)}
 placeholder="••••••••"
 className="w-full pl-12 pr-4 py-3 rounded-xl bg-gym-dark/80 border border-white/10 text-white placeholder-gray-500 focus:border-gym-accent focus:outline-none transition-colors"
 />
 </div>
 </div>

 <button
 type="submit"
 disabled={loading}
 className="w-full btn-accent flex items-center justify-center gap-2 py-3.5 text-base font-bold shadow-xl shadow-gym-accent/20"
 >
 {loading ? (
 <div className="w-6 h-6 border-2 border-gym-dark border-t-transparent rounded-full animate-spin" />
 ) : (
 <>
 <span>Sign In</span>
 <ArrowRight className="w-5 h-5" />
 </>
 )}
 </button>
 </form>

 <div className="mt-6 text-center text-sm text-gray-400">
 Don't have an account?{' '}
 <Link to="/register" className="text-gym-accent hover:underline font-semibold">
 Create an Athlete Account
 </Link>
 </div>
 </div>
 </div>
 </div>
 );
};

export default Login;
