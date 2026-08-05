import React, { useState, useEffect } from 'react';
import { createPortal } from 'react-dom';
import {
 X,
 User,
 Activity,
 Target,
 CheckCircle2,
 Scale,
 Ruler,
 Sparkles,
 Zap,
 HeartPulse,
 Flame,
 Award,
 RefreshCw
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const ProfileOptionsModal = ({ isOpen, onClose }) => {
 const { user, updateProfile } = useAuth();
 const [weightKg, setWeightKg] = useState(user?.weightKg || 75);
 const [heightCm, setHeightCm] = useState(user?.heightCm || 175);
 const [selectedGoal, setSelectedGoal] = useState(user?.fitnessGoal || 'STRENGTH');
 const [saving, setSaving] = useState(false);
 const [savedSuccess, setSavedSuccess] = useState(false);

 useEffect(() => {
 if (user) {
 setWeightKg(user.weightKg || 75);
 setHeightCm(user.heightCm || 175);
 setSelectedGoal(user.fitnessGoal || 'STRENGTH');
 }
 }, [user, isOpen]);

 if (!isOpen) return null;

 // Real-Time BMI Calculation
 const w = parseFloat(weightKg) || 75;
 const h = parseFloat(heightCm) || 175;
 const bmiValue = parseFloat((w / Math.pow(h / 100, 2)).toFixed(1));

 const getBmiAssessment = (bmi) => {
 if (bmi < 18.5) {
 return {
 category: 'Underweight',
 color: 'text-gym-warning border-gym-warning bg-gym-warning/10',
 recommendedGoal: 'MUSCLE_GAIN',
 recommendedName: 'Hypertrophy (Muscle Gain)'
 };
 }
 if (bmi < 25.0) {
 return {
 category: 'Normal Weight',
 color: 'text-gym-success border-gym-success bg-gym-success/10',
 recommendedGoal: 'STRENGTH',
 recommendedName: 'Max Strength (5x5)'
 };
 }
 if (bmi < 30.0) {
 return {
 category: 'Overweight',
 color: 'text-gym-warning border-gym-warning bg-gym-warning/10',
 recommendedGoal: 'FAT_LOSS',
 recommendedName: 'Fat Loss (Metabolic Circuit)'
 };
 }
 return {
 category: 'Obese',
 color: 'text-red-400 border-red-500 bg-red-500/10',
 recommendedGoal: 'FAT_LOSS',
 recommendedName: 'Fat Loss (Metabolic Circuit)'
 };
 };

 const bmiInfo = getBmiAssessment(bmiValue);

 const goals = [
 {
 id: 'STRENGTH',
 title: 'Max Strength',
 subtitle: '5x5 Heavy Compounds',
 color: 'text-gym-accent border-gym-accent/50 bg-gym-accent/10',
 icon: Zap
 },
 {
 id: 'MUSCLE_GAIN',
 title: 'Hypertrophy',
 subtitle: '4x10 Muscle Gain',
 color: 'text-gym-purple border-gym-purple/50 bg-gym-purple/10',
 icon: Award
 },
 {
 id: 'FAT_LOSS',
 title: 'Fat Loss',
 subtitle: '3x15 Metabolic Circuit',
 color: 'text-gym-warning border-gym-warning/50 bg-gym-warning/10',
 icon: Flame
 },
 {
 id: 'ENDURANCE',
 title: 'Endurance',
 subtitle: '3x20 VO2 Max',
 color: 'text-gym-success border-gym-success/50 bg-gym-success/10',
 icon: HeartPulse
 }
 ];

 const handleSaveProfile = async (e) => {
 e.preventDefault();
 setSaving(true);
 setSavedSuccess(false);

 const res = await updateProfile({
 weightKg: parseFloat(weightKg),
 heightCm: parseFloat(heightCm),
 fitnessGoal: selectedGoal
 });

 setSaving(false);
 if (res.success) {
 setSavedSuccess(true);
 setTimeout(() => {
 setSavedSuccess(false);
 onClose();
 }, 1000);
 }
 };

 return createPortal(
 <div
 className="fixed inset-0 z-[99999] overflow-y-auto bg-black/80 backdrop-blur-md p-3 sm:p-6 flex items-center justify-center animate-fadeIn"
 onClick={onClose}
 >
 <div
 className="relative w-full max-w-lg bg-gradient-to-b from-gym-card via-gym-dark to-gym-card rounded-2xl border border-gym-accent/40 shadow-2xl overflow-hidden my-auto max-h-[90vh] flex flex-col"
 onClick={(e) => e.stopPropagation()}
 >
 {/* Compact Header */}
 <div className="p-4 sm:p-5 border-b border-white/10 flex items-center justify-between bg-gym-dark/80">
 <div className="flex items-center gap-2.5">
 <div className="p-2.5 rounded-xl bg-gym-accent/15 text-gym-accent border border-gym-accent/30">
 <User className="w-5 h-5" />
 </div>
 <div>
 <span className="text-[10px] font-extrabold uppercase tracking-widest text-gym-accent block">
 My Profile
 </span>
 <h2 className="text-xl font-black text-white">
 Edit Weight, Height & Goal
 </h2>
 </div>
 </div>
 <button
 onClick={onClose}
 className="p-2 rounded-lg bg-white/5 hover:bg-white/10 text-gray-400 hover:text-white transition-colors"
 >
 <X className="w-5 h-5" />
 </button>
 </div>

 {/* Compact Modal Body */}
 <form onSubmit={handleSaveProfile} className="p-4 sm:p-5 overflow-y-auto space-y-4">
 {/* STEP 1: WEIGHT AND HEIGHT (2-Column Compact Row) */}
 <div className="grid grid-cols-2 gap-3">
 {/* Weight Box */}
 <div className="bg-gym-dark/80 p-3.5 rounded-xl border border-white/10 flex flex-col justify-between">
 <div className="flex items-center justify-between mb-2">
 <span className="text-[11px] font-bold text-gray-400 uppercase tracking-wider flex items-center gap-1">
 <Scale className="w-3.5 h-3.5 text-gym-accent" />
 <span>Weight</span>
 </span>
 <span className="text-[11px] px-2 py-0.5 rounded-md bg-gym-accent/15 text-gym-accent font-extrabold">
 {weightKg} kg
 </span>
 </div>
 <div className="flex items-center gap-2">
 <input
 type="number"
 step="0.5"
 min="30"
 max="250"
 value={weightKg}
 onChange={(e) => setWeightKg(e.target.value)}
 className="w-full bg-white/5 border border-white/15 rounded-lg px-3 py-1.5 text-lg font-black text-white focus:border-gym-accent focus:outline-none"
 />
 <div className="flex flex-col gap-1">
 <button
 type="button"
 onClick={() => setWeightKg((prev) => parseFloat((parseFloat(prev) + 1).toFixed(1)))}
 className="px-2 py-0.5 rounded bg-white/10 hover:bg-white/20 text-white text-[11px] font-bold"
 >
 +1
 </button>
 <button
 type="button"
 onClick={() => setWeightKg((prev) => Math.max(30, parseFloat((parseFloat(prev) - 1).toFixed(1))))}
 className="px-2 py-0.5 rounded bg-white/10 hover:bg-white/20 text-white text-[11px] font-bold"
 >
 -1
 </button>
 </div>
 </div>
 </div>

 {/* Height Box */}
 <div className="bg-gym-dark/80 p-3.5 rounded-xl border border-white/10 flex flex-col justify-between">
 <div className="flex items-center justify-between mb-2">
 <span className="text-[11px] font-bold text-gray-400 uppercase tracking-wider flex items-center gap-1">
 <Ruler className="w-3.5 h-3.5 text-gym-purple" />
 <span>Height</span>
 </span>
 <span className="text-[11px] px-2 py-0.5 rounded-md bg-gym-purple/15 text-gym-purple font-extrabold">
 {heightCm} cm
 </span>
 </div>
 <div className="flex items-center gap-2">
 <input
 type="number"
 step="1"
 min="100"
 max="250"
 value={heightCm}
 onChange={(e) => setHeightCm(e.target.value)}
 className="w-full bg-white/5 border border-white/15 rounded-lg px-3 py-1.5 text-lg font-black text-white focus:border-gym-purple focus:outline-none"
 />
 <div className="flex flex-col gap-1">
 <button
 type="button"
 onClick={() => setHeightCm((prev) => parseInt(prev, 10) + 1)}
 className="px-2 py-0.5 rounded bg-white/10 hover:bg-white/20 text-white text-[11px] font-bold"
 >
 +1
 </button>
 <button
 type="button"
 onClick={() => setHeightCm((prev) => Math.max(100, parseInt(prev, 10) - 1))}
 className="px-2 py-0.5 rounded bg-white/10 hover:bg-white/20 text-white text-[11px] font-bold"
 >
 -1
 </button>
 </div>
 </div>
 </div>
 </div>

 {/* STEP 2: SLEEK COMPACT BMI BANNER */}
 <div className="bg-gym-dark/80 p-3.5 rounded-xl border border-white/15 flex items-center justify-between gap-3">
 <div className="flex items-center gap-3">
 <div className="px-3 py-1.5 rounded-lg bg-white/5 border border-white/10 text-center">
 <span className="text-[9px] uppercase font-bold text-gray-400 block">BMI</span>
 <span className="text-xl font-black text-white">{bmiValue}</span>
 </div>
 <div>
 <div className="flex items-center gap-1.5">
 <span className={`text-[11px] px-2 py-0.5 rounded-md border font-black uppercase ${bmiInfo.color}`}>
 {bmiInfo.category}
 </span>
 </div>
 <p className="text-xs text-gray-300 mt-1">
 Recommended: <strong className="text-gym-accent">{bmiInfo.recommendedName}</strong>
 </p>
 </div>
 </div>
 </div>

 {/* STEP 3: COMPACT 2x2 GOAL GRID */}
 <div>
 <div className="flex items-center justify-between mb-2">
 <label className="text-xs font-bold text-white flex items-center gap-1.5">
 <Target className="w-4 h-4 text-gym-accent" />
 <span>Select Workout Goal</span>
 </label>
 </div>

 <div className="grid grid-cols-2 gap-2.5">
 {goals.map((g) => {
 const Icon = g.icon;
 const isSelected = selectedGoal === g.id;
 const isRecommended = g.id === bmiInfo.recommendedGoal;
 return (
 <button
 key={g.id}
 type="button"
 onClick={() => setSelectedGoal(g.id)}
 className={`p-3 rounded-xl border text-left transition-all relative ${
 isSelected
 ? `${g.color} shadow-lg scale-[1.02] border-2`
 : 'bg-gym-dark/60 border-white/10 hover:border-white/25 text-gray-400'
 }`}
 >
 {isRecommended && (
 <span className="absolute top-1.5 right-1.5 text-[9px] font-black uppercase px-1.5 py-0.5 rounded-md bg-gym-accent text-gym-dark shadow-sm flex items-center gap-0.5">
 <Sparkles className="w-2.5 h-2.5 fill-current" />
 <span>BMI Match</span>
 </span>
 )}

 <div className="flex items-center gap-2">
 <div className={`p-1.5 rounded-lg ${isSelected ? 'bg-white/10 text-white' : 'bg-white/5 text-gray-400'}`}>
 <Icon className="w-4 h-4" />
 </div>
 <div>
 <h4 className={`text-sm font-black ${isSelected ? 'text-white' : 'text-gray-200'}`}>
 {g.title}
 </h4>
 <span className="text-[10px] font-bold text-gray-400 block">
 {g.subtitle}
 </span>
 </div>
 </div>
 </button>
 );
 })}
 </div>
 </div>

 {/* Compact Action Buttons */}
 <div className="pt-3 border-t border-white/10 flex items-center justify-end gap-3">
 <button
 type="button"
 onClick={onClose}
 className="px-4 py-2.5 rounded-xl bg-white/5 hover:bg-white/10 text-gray-300 font-bold text-xs transition-colors"
 >
 Cancel
 </button>
 <button
 type="submit"
 disabled={saving}
 className="px-5 py-2.5 rounded-xl bg-gradient-to-r from-gym-accent to-gym-purple hover:from-gym-purple hover:to-gym-accent text-gym-dark font-black text-xs transition-all shadow-lg shadow-gym-accent/20 flex items-center justify-center gap-1.5"
 >
 {saving ? (
 <>
 <RefreshCw className="w-3.5 h-3.5 animate-spin" />
 <span>Saving...</span>
 </>
 ) : savedSuccess ? (
 <>
 <CheckCircle2 className="w-4 h-4" />
 <span>Saved successfully!</span>
 </>
 ) : (
 <>
 <Sparkles className="w-3.5 h-3.5 fill-current" />
 <span>Save Profile</span>
 </>
 )}
 </button>
 </div>
 </form>
 </div>
 </div>,
 document.body
 );
};

export default ProfileOptionsModal;
