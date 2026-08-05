import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Dumbbell, Lock, User, Mail, AlertCircle, ArrowRight, Scale, Ruler, Target } from 'lucide-react';

const Register = () => {
 const [formData, setFormData] = useState({
 username: '',
 email: '',
 password: '',
 age: '',
 weightKg: '',
 heightCm: '',
 gender: '',
 fitnessGoal: '',
 experienceLevel: ''
 });
 const [error, setError] = useState('');
 const { register, loading } = useAuth();
 const navigate = useNavigate();

 const handleChange = (e) => {
 const { name, value } = e.target;
 setFormData((prev) => ({
 ...prev,
 [name]: name === 'weightKg' || name === 'heightCm' || name === 'age' ? Number(value) : value
 }));
 };

 const handleSubmit = async (e) => {
 e.preventDefault();
 e.stopPropagation();
 setError('');
 const res = await register(formData);
 if (res.success) {
 navigate('/login', { state: { successMessage: res.message || 'Account registered successfully! Please log in.' } });
 } else {
 setError(res.message);
 }
 };

 return (
 <div className="min-h-screen flex items-center justify-center px-4 py-12 relative overflow-hidden">
 {/* Glow Effects */}
 <div className="absolute top-1/3 left-1/4 w-96 h-96 bg-gym-purple/10 rounded-full blur-3xl pointer-events-none" />
 <div className="absolute bottom-1/3 right-1/4 w-96 h-96 bg-gym-accent/10 rounded-full blur-3xl pointer-events-none" />

 <div className="w-full max-w-lg">
 <div className="text-center mb-8">
 <div className="inline-flex p-3 rounded-2xl bg-gradient-to-tr from-gym-accent to-gym-purple text-gym-dark mb-4 shadow-xl shadow-gym-accent/20">
 <Dumbbell className="w-10 h-10" />
 </div>
 <h1 className="text-3xl font-black tracking-wider text-white">
 JOIN <span className="text-gym-accent">REPSYNC</span>
 </h1>
 <p className="text-gray-400 mt-2 text-sm">
 Create your athlete profile for personalized training & PR tracking
 </p>
 </div>

 <div className="glass-card">
 <form onSubmit={handleSubmit} className="space-y-5">
 {error && (
 <div className="flex items-center gap-3 p-4 rounded-xl bg-gym-danger/10 border border-gym-danger/30 text-gym-danger text-sm">
 <AlertCircle className="w-5 h-5 flex-shrink-0" />
 <span>{error}</span>
 </div>
 )}

 <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
 <div>
 <label className="block text-xs font-bold text-gray-300 uppercase tracking-wider mb-2">
 Username
 </label>
 <div className="relative">
 <User className="absolute left-3.5 top-3.5 w-4 h-4 text-gray-400" />
 <input
 type="text"
 name="username"
 required
 value={formData.username}
 onChange={handleChange}
 placeholder="athlete_name"
 className="w-full pl-10 pr-3 py-2.5 rounded-xl bg-gym-dark/80 border border-white/10 text-white placeholder-gray-500 text-sm focus:border-gym-accent focus:outline-none transition-colors"
 />
 </div>
 </div>

 <div>
 <label className="block text-xs font-bold text-gray-300 uppercase tracking-wider mb-2">
 Email Address
 </label>
 <div className="relative">
 <Mail className="absolute left-3.5 top-3.5 w-4 h-4 text-gray-400" />
 <input
 type="email"
 name="email"
 required
 value={formData.email}
 onChange={handleChange}
 placeholder="you@example.com"
 className="w-full pl-10 pr-3 py-2.5 rounded-xl bg-gym-dark/80 border border-white/10 text-white placeholder-gray-500 text-sm focus:border-gym-accent focus:outline-none transition-colors"
 />
 </div>
 </div>
 </div>

 <div>
 <label className="block text-xs font-bold text-gray-300 uppercase tracking-wider mb-2">
 Password
 </label>
 <div className="relative">
 <Lock className="absolute left-3.5 top-3.5 w-4 h-4 text-gray-400" />
 <input
 type="password"
 name="password"
 required
 value={formData.password}
 onChange={handleChange}
 placeholder="At least 6 characters"
 className="w-full pl-10 pr-3 py-2.5 rounded-xl bg-gym-dark/80 border border-white/10 text-white placeholder-gray-500 text-sm focus:border-gym-accent focus:outline-none transition-colors"
 />
 </div>
 </div>

 <div className="border-t border-white/10 pt-4">
 <span className="text-xs font-bold uppercase tracking-wider text-gym-accent mb-3 block">
 Anatomical & Goal Profile (For Calibration)
 </span>

 <div className="grid grid-cols-2 gap-4 mb-4">
 <div>
 <label className="block text-xs text-gray-400 mb-1">Weight (kg)</label>
 <div className="relative">
 <Scale className="absolute left-3 top-3 w-4 h-4 text-gray-500" />
 <input
 type="number"
 name="weightKg"
 min="30"
 max="250"
 required
 value={formData.weightKg}
 onChange={handleChange}
 className="w-full pl-9 pr-3 py-2 rounded-xl bg-gym-dark/80 border border-white/10 text-white text-sm focus:border-gym-accent focus:outline-none"
 />
 </div>
 </div>

 <div>
 <label className="block text-xs text-gray-400 mb-1">Height (cm)</label>
 <div className="relative">
 <Ruler className="absolute left-3 top-3 w-4 h-4 text-gray-500" />
 <input
 type="number"
 name="heightCm"
 min="100"
 max="240"
 required
 value={formData.heightCm}
 onChange={handleChange}
 className="w-full pl-9 pr-3 py-2 rounded-xl bg-gym-dark/80 border border-white/10 text-white text-sm focus:border-gym-accent focus:outline-none"
 />
 </div>
 </div>
 </div>

 <div className="grid grid-cols-2 gap-4">
 <div>
 <label className="block text-xs text-gray-400 mb-1">Gender</label>
 <select
 name="gender"
 required
 value={formData.gender}
 onChange={handleChange}
 className="w-full px-3 py-2 rounded-xl bg-gym-dark border border-white/10 text-white text-sm focus:border-gym-accent focus:outline-none"
 >
 <option value="" disabled>Select Gender</option>
 <option value="MALE">Male</option>
 <option value="FEMALE">Female</option>
 <option value="OTHER">Other</option>
 </select>
 </div>

 <div>
 <label className="block text-xs text-gray-400 mb-1">Primary Strategy Goal</label>
 <select
 name="fitnessGoal"
 required
 value={formData.fitnessGoal}
 onChange={handleChange}
 className="w-full px-3 py-2 rounded-xl bg-gym-dark border border-white/10 text-white text-sm focus:border-gym-accent focus:outline-none"
 >
 <option value="" disabled>Select Goal</option>
 <option value="STRENGTH">Strength (5x5 Power)</option>
 <option value="MUSCLE_GAIN">Hypertrophy (4x10 Muscle)</option>
 <option value="FAT_LOSS">Fat Loss (3x15 Metabolic)</option>
 <option value="ENDURANCE">Endurance (3x20 Stamina)</option>
 </select>
 </div>
 </div>

 <div className="mt-4">
 <label className="block text-xs text-gray-400 mb-1">Experience Level (For Calibration)</label>
 <select
 name="experienceLevel"
 required
 value={formData.experienceLevel}
 onChange={handleChange}
 className="w-full px-3 py-2 rounded-xl bg-gym-dark border border-white/10 text-white text-sm focus:border-gym-accent focus:outline-none"
 >
 <option value="" disabled>Select Experience</option>
 <option value="BEGINNER">Beginner (&lt; 6 months)</option>
 <option value="INTERMEDIATE">Intermediate (6 months – 2 years)</option>
 <option value="ADVANCED">Advanced (2+ years)</option>
 </select>
 </div>
 </div>

 <button
 type="submit"
 disabled={loading}
 className="w-full btn-accent flex items-center justify-center gap-2 py-3.5 text-base font-bold shadow-xl shadow-gym-accent/20 mt-6"
 >
 {loading ? (
 <div className="w-6 h-6 border-2 border-gym-dark border-t-transparent rounded-full animate-spin" />
 ) : (
 <>
 <span>Create Athlete Profile</span>
 <ArrowRight className="w-5 h-5" />
 </>
 )}
 </button>
 </form>

 <div className="mt-6 text-center text-sm text-gray-400">
 Already have an account?{' '}
 <Link to="/login" className="text-gym-accent hover:underline font-semibold">
 Sign in here
 </Link>
 </div>
 </div>
 </div>
 </div>
 );
};

export default Register;
