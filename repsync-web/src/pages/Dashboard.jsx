import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import axiosClient from '../api/axiosClient';
import { useAuth } from '../context/AuthContext';
import { Dumbbell, Compass, Trophy, BookOpen, Activity, Zap, HeartPulse, ShieldCheck, ArrowUpRight, Settings, Flame } from 'lucide-react';
import GoalSelectionModal from '../components/GoalSelectionModal';
import ProfileOptionsModal from '../components/ProfileOptionsModal';
import GamificationBar from '../components/GamificationBar';

const Dashboard = () => {
 const { user } = useAuth();
 const [profile, setProfile] = useState(null);
 const [loading, setLoading] = useState(true);
 const [isGoalModalOpen, setIsGoalModalOpen] = useState(false);
 const [isProfileModalOpen, setIsProfileModalOpen] = useState(false);

 useEffect(() => {
 const fetchProfile = async () => {
 try {
 const res = await axiosClient.get('/analytics/profile');
 setProfile(res.data);
 } catch (err) {
 console.error('Failed to load profile analytics:', err);
 } finally {
 setLoading(false);
 }
 };
 fetchProfile();
 }, []);

 useEffect(() => {
 if (user && !user.fitnessGoal) {
 setIsGoalModalOpen(true);
 }
 }, [user]);

 // Live physiological metrics from AuthContext (user state updates immediately across pages)
 const currentWeight = user?.weightKg || profile?.weightKg || 75;
 const currentHeight = user?.heightCm || profile?.heightCm || 175;
 const currentGoal = user?.fitnessGoal || profile?.currentGoal || 'STRENGTH';
 const currentBmi = parseFloat((currentWeight / Math.pow(currentHeight / 100, 2)).toFixed(1));
 const currentBmiCategory =
 currentBmi < 18.5
 ? 'Underweight'
 : currentBmi < 25.0
 ? 'Normal Weight'
 : currentBmi < 30.0
 ? 'Overweight'
 : 'Obese';

 const getBmiBadgeColor = (category) => {
 if (!category) return 'bg-gray-500/20 text-gray-400 border-gray-500/40';
 if (category === 'Normal Weight') return 'bg-gym-success/20 text-gym-success border-gym-success/40';
 if (category === 'Underweight') return 'bg-gym-warning/20 text-gym-warning border-gym-warning/40';
 return 'bg-gym-danger/20 text-gym-danger border-gym-danger/40';
 };

 return (
 <div className="min-h-screen pb-16">
 {/* Top Banner */}
 <div className="relative overflow-hidden bg-gradient-to-b from-gym-card to-gym-dark border-b border-white/10 pt-12 pb-16 px-6">
 <div className="absolute top-0 right-1/4 w-96 h-96 bg-gym-accent/10 rounded-full blur-3xl pointer-events-none" />
 <div className="max-w-7xl mx-auto">
 <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
 <div>
 <span className="text-xs font-bold uppercase tracking-widest text-gym-accent mb-2 block">
 Athlete Command Center
 </span>
 <h1 className="text-4xl md:text-5xl font-black text-white tracking-tight">
 Welcome back, <span className="text-gym-accent">{user?.username || 'Athlete'}</span>
 </h1>
 <p className="text-gray-400 mt-2 max-w-2xl text-sm md:text-base">
 Your digital gym assistant has calibrated your anatomical profile and synchronized your strategy goals.
 </p>
 </div>

 <div className="flex flex-wrap items-center gap-3 self-start md:self-center">
 <button
 onClick={() => setIsProfileModalOpen(true)}
 className="px-4 py-3 rounded-xl bg-gym-accent/20 hover:bg-gym-accent/30 text-gym-accent border border-gym-accent/40 font-bold text-sm transition-colors flex items-center gap-2"
 >
 <Settings className="w-4 h-4" />
 <span>Edit Profile</span>
 </button>
 <button
 onClick={() => setIsGoalModalOpen(true)}
 className="px-4 py-3 rounded-xl bg-gym-purple/20 hover:bg-gym-purple/30 text-gym-purple border border-gym-purple/40 font-bold text-sm transition-colors"
 >
 Change Goal
 </button>
 <Link
 to="/workouts/plan"
 className="btn-accent inline-flex items-center gap-2 shadow-xl shadow-gym-accent/20"
 >
 <Compass className="w-5 h-5" />
 <span>Launch Strategy Plan</span>
 </Link>
 </div>
 </div>
 </div>
 </div>

 <div className="max-w-7xl mx-auto px-6 -mt-8">
 {/* Gamification Level, Streak & Badges Bar (Feature 4) */}
 <div className="mb-8">
 <GamificationBar />
 </div>

 {/* Physiological Profile Metrics Row */}
 <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
 <div className="glass-card flex items-center gap-4">
 <div className="p-3 rounded-2xl bg-gym-accent/10 text-gym-accent border border-gym-accent/20">
 <Activity className="w-6 h-6" />
 </div>
 <div>
 <span className="text-xs text-gray-400 uppercase font-semibold">BMI Index</span>
 <div className="flex items-center gap-2 mt-1">
 <span className="text-2xl font-black text-white">{currentBmi}</span>
 <span
 className={`text-xs px-2 py-0.5 rounded-full border font-bold ${getBmiBadgeColor(
 currentBmiCategory
 )}`}
 >
 {currentBmiCategory}
 </span>
 </div>
 </div>
 </div>

 <div className="glass-card flex items-center justify-between">
 <div className="flex items-center gap-4">
 <div className="p-3 rounded-2xl bg-gym-purple/10 text-gym-purple border border-gym-purple/20">
 <Zap className="w-6 h-6" />
 </div>
 <div>
 <span className="text-xs text-gray-400 uppercase font-semibold">Active Goal Strategy</span>
 <div className="text-xl font-black text-white mt-1">
 {currentGoal}
 </div>
 </div>
 </div>
 <button
 onClick={() => setIsGoalModalOpen(true)}
 className="px-3 py-1.5 rounded-xl bg-gym-purple/20 hover:bg-gym-purple/30 text-gym-purple border border-gym-purple/40 text-xs font-bold transition-colors"
 >
 Change Goal
 </button>
 </div>

 <div className="glass-card flex items-center gap-4">
 <div className="p-3 rounded-2xl bg-gym-success/10 text-gym-success border border-gym-success/20">
 <HeartPulse className="w-6 h-6" />
 </div>
 <div>
 <span className="text-xs text-gray-400 uppercase font-semibold">Body Weight</span>
 <div className="text-2xl font-black text-white mt-1">
 {currentWeight} <span className="text-sm font-medium text-gray-400">kg</span>
 </div>
 </div>
 </div>

 <div className="glass-card flex items-center gap-4">
 <div className="p-3 rounded-2xl bg-blue-500/10 text-blue-400 border border-blue-500/20">
 <ShieldCheck className="w-6 h-6" />
 </div>
 <div>
 <span className="text-xs text-gray-400 uppercase font-semibold">Height</span>
 <div className="text-2xl font-black text-white mt-1">
 {currentHeight} <span className="text-sm font-medium text-gray-400">cm</span>
 </div>
 </div>
 </div>
 </div>

 {/* Calibration Banner */}
 <div className="glass-card mb-8 bg-gradient-to-r from-gym-card via-gym-card to-gym-accent/5 border-gym-accent/30 flex items-center justify-between">
 <div className="flex items-center gap-4">
 <div className="w-3 h-3 rounded-full bg-gym-accent animate-pulse" />
 <div>
 <span className="text-xs text-gym-accent font-bold uppercase tracking-wider">
 Live Calibration Summary
 </span>
 <p className="text-sm text-gray-200 font-medium mt-0.5">
 Personalized for {currentWeight}kg / {currentHeight}cm (BMI {currentBmi}) • {currentBmiCategory} • Active Strategy: {currentGoal}
 </p>
 </div>
 </div>
 <Link to="/analytics" className="text-xs text-gym-accent hover:underline font-semibold flex items-center gap-1">
 <span>View PR Analytics</span>
 <ArrowUpRight className="w-3.5 h-3.5" />
 </Link>
 </div>

 {/* Action Hub Cards */}
 <h2 className="text-2xl font-black text-white mb-4">Training Navigation</h2>
 <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
 <Link to="/workouts/plan" className="glass-card-hover group">
 <div className="p-3 rounded-2xl bg-gym-accent/10 text-gym-accent w-fit mb-4 group-hover:scale-110 transition-transform">
 <Compass className="w-8 h-8" />
 </div>
 <h3 className="text-xl font-bold text-white mb-2">Strategy Workout Planner</h3>
 <p className="text-sm text-gray-400 mb-4">
 Dynamically generated workout splits tailored to your Strategy Goal ({profile?.currentGoal || 'STRENGTH'}).
 Sets, reps, and weights scaled to your body profile.
 </p>
 <span className="text-sm font-bold text-gym-accent flex items-center gap-1 group-hover:translate-x-1 transition-transform">
 <span>Generate Split</span>
 <ArrowUpRight className="w-4 h-4" />
 </span>
 </Link>

 <Link to="/exercises" className="glass-card-hover group">
 <div className="p-3 rounded-2xl bg-gym-purple/10 text-gym-purple w-fit mb-4 group-hover:scale-110 transition-transform">
 <BookOpen className="w-8 h-8" />
 </div>
 <h3 className="text-xl font-bold text-white mb-2">Exercise Guide Library</h3>
 <p className="text-sm text-gray-400 mb-4">
 Explore anatomical target regions, machine setup guides, and biomechanical form execution steps for
 compound and isolation exercises.
 </p>
 <span className="text-sm font-bold text-gym-purple flex items-center gap-1 group-hover:translate-x-1 transition-transform">
 <span>Browse Exercises</span>
 <ArrowUpRight className="w-4 h-4" />
 </span>
 </Link>

 <Link to="/analytics" className="glass-card-hover group">
 <div className="p-3 rounded-2xl bg-gym-success/10 text-gym-success w-fit mb-4 group-hover:scale-110 transition-transform">
 <Trophy className="w-8 h-8" />
 </div>
 <h3 className="text-xl font-bold text-white mb-2">PR Analytics & History</h3>
 <p className="text-sm text-gray-400 mb-4">
 Log completed sets and automatically detect personal bests (MAX_WEIGHT and MAX_REPS) with real-time MySQL
 persistence.
 </p>
 <span className="text-sm font-bold text-gym-success flex items-center gap-1 group-hover:translate-x-1 transition-transform">
 <span>Track Records</span>
 <ArrowUpRight className="w-4 h-4" />
 </span>
 </Link>

 <Link to="/nutrition" className="glass-card-hover group">
 <div className="p-3 rounded-2xl bg-amber-500/10 text-amber-400 w-fit mb-4 group-hover:scale-110 transition-transform">
 <Flame className="w-8 h-8" />
 </div>
 <h3 className="text-xl font-bold text-white mb-2">Calorie & Macro Coach</h3>
 <p className="text-sm text-gray-400 mb-4">
 Calibrated calorie targets, protein fueling, hydration metrics, and nutrient timing tailored to your BMI Index & Goal.
 </p>
 <span className="text-sm font-bold text-amber-400 flex items-center gap-1 group-hover:translate-x-1 transition-transform">
 <span>Optimize Diet</span>
 <ArrowUpRight className="w-4 h-4" />
 </span>
 </Link>
 </div>
 </div>

 <GoalSelectionModal
 isOpen={isGoalModalOpen}
 onClose={() => setIsGoalModalOpen(false)}
 onGoalUpdated={(newGoal) => {
 setProfile((prev) => (prev ? { ...prev, currentGoal: newGoal } : prev));
 }}
 />
 <ProfileOptionsModal
 isOpen={isProfileModalOpen}
 onClose={() => setIsProfileModalOpen(false)}
 />
 </div>
 );
};

export default Dashboard;
