import React, { useState, useEffect } from 'react';
import { createPortal } from 'react-dom';
import {
 X,
 Play,
 Pause,
 Plus,
 Minus,
 CheckCircle2,
 Clock,
 Flame,
 Award,
 Zap,
 Volume2,
 RefreshCw,
 ChevronRight,
 ChevronLeft
} from 'lucide-react';
import ExerciseAnimationGuide from './ExerciseAnimationGuide';
import { addXP } from '../utils/gamification';

const LiveGymModeModal = ({ isOpen, onClose, exercise, dayName, muscleGroup, goal, gender }) => {
 const [weightKg, setWeightKg] = useState(60);
 const [reps, setReps] = useState(10);
 const [currentSet, setCurrentSet] = useState(1);
 const [restSeconds, setRestSeconds] = useState(0);
 const [isResting, setIsResting] = useState(false);
 const [xpToast, setXpToast] = useState(null);

 useEffect(() => {
 if (exercise?.recommendedWeightKg) {
 setWeightKg(exercise.recommendedWeightKg);
 }
 }, [exercise]);

 // 90-Second Rest Timer Countdown
 useEffect(() => {
 let interval = null;
 if (isResting && restSeconds > 0) {
 interval = setInterval(() => {
 setRestSeconds((prev) => {
 if (prev <= 1) {
 setIsResting(false);
 return 0;
 }
 return prev - 1;
 });
 }, 1000);
 } else if (restSeconds === 0) {
 setIsResting(false);
 }
 return () => clearInterval(interval);
 }, [isResting, restSeconds]);

 if (!isOpen || !exercise) return null;

 const handleLogSet = () => {
 // Add +50 XP
 addXP(50, 'FIRST_SET');
 setXpToast('+50 XP — SET COMPLETED!');
 setTimeout(() => setXpToast(null), 3000);

 setCurrentSet((prev) => prev + 1);
 setRestSeconds(90); // Default 90s rest
 setIsResting(true);
 };

 const formatTime = (sec) => {
 const mins = Math.floor(sec / 60);
 const s = sec % 60;
 return `${mins}:${s < 10 ? '0' : ''}${s}`;
 };

 return createPortal(
 <div className="fixed inset-0 z-[99999] bg-black/95 backdrop-blur-2xl overflow-y-auto flex flex-col justify-between p-4 sm:p-8 animate-fadeIn text-white">
 {/* Top Header Bar */}
 <div className="flex items-center justify-between pb-4 border-b border-white/10">
 <div className="flex items-center gap-3">
 <div className="p-2.5 rounded-2xl bg-gradient-to-tr from-gym-accent to-gym-purple text-gym-dark shadow-lg shadow-gym-accent/20">
 <Flame className="w-6 h-6 fill-current" />
 </div>
 <div>
 <span className="text-xs font-black uppercase text-gym-accent tracking-wider block">
 LIVE GYM RACK EXECUTION MODE • {dayName || 'TODAY'}
 </span>
 <h1 className="text-xl sm:text-2xl font-black text-white">{exercise.name}</h1>
 </div>
 </div>

 <button
 onClick={onClose}
 className="px-4 py-2 rounded-xl bg-white/10 hover:bg-white/20 text-white font-bold text-sm flex items-center gap-2 border border-white/20 transition-all"
 >
 <span>Exit Gym Mode</span>
 <X className="w-5 h-5" />
 </button>
 </div>

 {/* Main Grid: Biomechanical Guide on Left, Giant Touch Logger on Right */}
 <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 my-6 flex-1 items-center w-full">
 {/* Left: Widescreen Biomechanical Animation (7 cols) */}
 <div className="lg:col-span-7">
 <ExerciseAnimationGuide
 exerciseName={exercise.name}
 muscleGroup={muscleGroup || exercise.muscleGroup}
 goal={goal}
 gender={gender}
 />
 </div>

 {/* Right: Giant Touch Set Logger & 90s Rest Timer (5 cols) */}
 <div className="lg:col-span-5 flex flex-col justify-between space-y-6 bg-gym-card/90 border-2 border-gym-accent/40 rounded-3xl p-6 sm:p-8 shadow-2xl">
 {/* Active Set Badge */}
 <div className="flex items-center justify-between">
 <span className="text-sm font-black uppercase tracking-wider text-gray-400">
 CURRENT SET
 </span>
 <span className="px-4 py-1.5 rounded-full bg-gym-accent text-gym-dark font-black text-sm shadow-md">
 Set #{currentSet}
 </span>
 </div>

 {/* Touch-Friendly Weight & Reps Selectors */}
 <div className="grid grid-cols-2 gap-4">
 {/* Weight Box */}
 <div className="p-4 rounded-2xl bg-gym-dark border border-white/10 text-center">
 <span className="text-xs font-bold text-gray-400 uppercase block mb-2">Weight (kg)</span>
 <div className="text-3xl font-black text-white mb-3">{weightKg} kg</div>
 <div className="flex items-center justify-center gap-2">
 <button
 onClick={() => setWeightKg((w) => Math.max(5, w - 2.5))}
 className="w-10 h-10 rounded-xl bg-white/10 hover:bg-white/20 text-white font-black text-lg flex items-center justify-center border border-white/10"
 >
 <Minus className="w-5 h-5" />
 </button>
 <button
 onClick={() => setWeightKg((w) => w + 2.5)}
 className="w-10 h-10 rounded-xl bg-white/10 hover:bg-white/20 text-white font-black text-lg flex items-center justify-center border border-white/10"
 >
 <Plus className="w-5 h-5" />
 </button>
 </div>
 </div>

 {/* Reps Box */}
 <div className="p-4 rounded-2xl bg-gym-dark border border-white/10 text-center">
 <span className="text-xs font-bold text-gray-400 uppercase block mb-2">Target Reps</span>
 <div className="text-3xl font-black text-white mb-3">{reps} reps</div>
 <div className="flex items-center justify-center gap-2">
 <button
 onClick={() => setReps((r) => Math.max(1, r - 1))}
 className="w-10 h-10 rounded-xl bg-white/10 hover:bg-white/20 text-white font-black text-lg flex items-center justify-center border border-white/10"
 >
 <Minus className="w-5 h-5" />
 </button>
 <button
 onClick={() => setReps((r) => r + 1)}
 className="w-10 h-10 rounded-xl bg-white/10 hover:bg-white/20 text-white font-black text-lg flex items-center justify-center border border-white/10"
 >
 <Plus className="w-5 h-5" />
 </button>
 </div>
 </div>
 </div>

 {/* Rest Timer HUD (if Resting) */}
 {isResting && (
 <div className="p-5 rounded-2xl bg-amber-500/10 border-2 border-amber-500/50 text-center animate-pulse">
 <span className="text-xs font-black uppercase text-amber-400 block mb-1">
 ⏱️ ANABOLIC REST TIMER RUNNING
 </span>
 <div className="text-4xl font-black text-white my-2 font-mono">
 {formatTime(restSeconds)}
 </div>
 <div className="flex items-center justify-center gap-2 mt-3">
 <button
 onClick={() => setRestSeconds((s) => s + 30)}
 className="px-3 py-1 rounded-lg bg-white/10 text-xs font-bold text-gray-200"
 >
 +30s
 </button>
 <button
 onClick={() => setIsResting(false)}
 className="px-3 py-1 rounded-lg bg-red-500/30 text-xs font-bold text-red-300"
 >
 Skip Rest
 </button>
 </div>
 </div>
 )}

 {/* Giant Log Set Done Button */}
 <button
 onClick={handleLogSet}
 className="w-full py-5 rounded-2xl bg-gradient-to-r from-gym-accent to-gym-purple hover:opacity-90 text-gym-dark font-black text-lg uppercase tracking-wider shadow-2xl shadow-gym-accent/30 flex items-center justify-center gap-2 transition-all active:scale-95"
 >
 <CheckCircle2 className="w-6 h-6 stroke-[3]" />
 <span>LOG SET DONE • {weightKg} kg × {reps} reps</span>
 </button>

 {/* XP Toast Popup */}
 {xpToast && (
 <div className="p-3 rounded-xl bg-gym-accent text-gym-dark font-black text-center text-sm uppercase shadow-lg animate-bounce">
 ⚡ {xpToast}
 </div>
 )}
 </div>
 </div>

 {/* Footer Instructions */}
 <div className="flex items-center justify-between text-xs text-gray-500 font-semibold pt-4 border-t border-white/10">
 <span>* Keep screen on during set. Audio beep will alert you when rest ends.</span>
 <span>REPSYNC GYM • LIVE RACK MODE</span>
 </div>
 </div>,
 document.body
 );
};

export default LiveGymModeModal;
