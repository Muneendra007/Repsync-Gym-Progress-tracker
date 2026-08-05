import React, { useState, useEffect, useRef } from 'react';
import {
 X,
 Play,
 Pause,
 RotateCcw,
 ChevronLeft,
 ChevronRight,
 CheckCircle2,
 Trophy,
 Flame,
 Activity,
 Target,
 AlertTriangle,
 Volume2,
 VolumeX,
 Sparkles,
 Check
} from 'lucide-react';
import axiosClient from '../api/axiosClient';

const LiveWorkoutCompanion = ({ isOpen, onClose, planData, userWeight = 75 }) => {
 const [currentIndex, setCurrentIndex] = useState(0);
 const [activeTab, setActiveTab] = useState('LOGGER'); // 'LOGGER' or 'FORM_GUIDE'

 // Rest Timer State
 const [timerSeconds, setTimerSeconds] = useState(90);
 const [remainingSeconds, setRemainingSeconds] = useState(90);
 const [isRunning, setIsRunning] = useState(false);
 const [soundEnabled, setSoundEnabled] = useState(true);

 // Set Logging & PR Tracking
 const [completedSets, setCompletedSets] = useState({});
 const [prAlerts, setPrAlerts] = useState([]);
 const [sessionCompleted, setSessionCompleted] = useState(false);

 const timerRef = useRef(null);

 // Helper to strip HTML tags
 const stripHtml = (html) => {
 if (!html) return '';
 return html.replace(/<[^>]*>?/gm, ' ').replace(/\s+/g, ' ').trim();
 };

 const exercises = planData?.exercises || [];
 const currentExercise = exercises[currentIndex] || null;

 // Initialize remaining seconds when exercise changes
 useEffect(() => {
 if (currentExercise) {
 const rest = Number(currentExercise.restSeconds) || 90;
 setTimerSeconds(rest);
 setRemainingSeconds(rest);
 setIsRunning(false);
 }
 }, [currentIndex, currentExercise]);

 // Audio Beep generator using Web Audio API
 const playBeep = () => {
 if (!soundEnabled) return;
 try {
 const ctx = new (window.AudioContext || window.webkitAudioContext)();
 const osc = ctx.createOscillator();
 const gain = ctx.createGain();
 osc.type = 'sine';
 osc.frequency.setValueAtTime(880, ctx.currentTime); // A5 note
 gain.gain.setValueAtTime(0.3, ctx.currentTime);
 gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.6);
 osc.connect(gain);
 gain.connect(ctx.destination);
 osc.start();
 osc.stop(ctx.currentTime + 0.6);
 } catch (e) {
 console.log('Audio disabled or blocked by browser');
 }
 };

 // Timer interval loop
 useEffect(() => {
 if (isRunning && remainingSeconds > 0) {
 timerRef.current = setInterval(() => {
 setRemainingSeconds((prev) => {
 if (prev <= 1) {
 setIsRunning(false);
 playBeep();
 return 0;
 }
 return prev - 1;
 });
 }, 1000);
 } else {
 clearInterval(timerRef.current);
 }
 return () => clearInterval(timerRef.current);
 }, [isRunning, remainingSeconds, soundEnabled]);

 if (!isOpen || !planData) return null;

 // Handle Set check and background PR query
 const handleToggleSet = async (exIndex, setNum, weight, reps) => {
 const key = `${exIndex}-${setNum}`;
 const alreadyCompleted = completedSets[key];

 setCompletedSets((prev) => ({
 ...prev,
 [key]: !alreadyCompleted
 }));

 // If marking as completed, check for PR and start rest timer!
 if (!alreadyCompleted) {
 setIsRunning(true);
 try {
 const res = await axiosClient.post('/analytics/prs/check', {
 exerciseName: currentExercise.name,
 weightKg: Number(weight),
 repsPerformed: Number(reps)
 });
 if (res.data?.newPrAchieved) {
 const alertMsg = `🏆 NEW PR on ${currentExercise.name}! ${weight}kg x ${reps} reps`;
 setPrAlerts((prev) => [alertMsg, ...prev]);
 }
 } catch (err) {
 console.error('Failed to check PR in background', err);
 }
 }
 };

 const handleNextExercise = () => {
 if (currentIndex < exercises.length - 1) {
 setCurrentIndex((prev) => prev + 1);
 } else {
 setSessionCompleted(true);
 }
 };

 const handlePrevExercise = () => {
 if (currentIndex > 0) {
 setCurrentIndex((prev) => prev - 1);
 }
 };

 const totalSetsCompleted = Object.values(completedSets).filter(Boolean).length;
 const progressPercent = Math.round((totalSetsCompleted / (exercises.length * 5 || 25)) * 100);

 // SVG circular timer properties
 const radius = 54;
 const circumference = 2 * Math.PI * radius;
 const strokeDashoffset = circumference - (remainingSeconds / timerSeconds) * circumference;

 return (
 <div className="fixed inset-0 z-50 flex flex-col bg-gym-dark/95 backdrop-blur-xl text-white overflow-hidden animate-fadeIn">
 {/* Cyberpunk Glow Backgrounds */}
 <div className="absolute top-0 right-1/4 w-96 h-96 bg-gym-accent/10 rounded-full blur-3xl pointer-events-none" />
 <div className="absolute bottom-0 left-1/4 w-96 h-96 bg-gym-purple/10 rounded-full blur-3xl pointer-events-none" />

 {/* Header Bar */}
 <div className="px-6 py-4 border-b border-white/10 flex items-center justify-between bg-gym-card/80 relative z-10">
 <div className="flex items-center gap-4">
 <div className="flex items-center gap-2 px-3 py-1.5 rounded-full bg-gym-danger/20 border border-gym-danger/40 text-gym-danger text-xs font-black uppercase tracking-wider animate-pulse">
 <span className="w-2 h-2 rounded-full bg-gym-danger" />
 Live Session
 </div>
 <div>
 <h2 className="text-lg font-black tracking-wide">{planData.strategyName}</h2>
 <p className="text-xs text-gray-400">
 Exercise {currentIndex + 1} of {exercises.length} • {totalSetsCompleted} Total Sets Logged
 </p>
 </div>
 </div>

 <div className="flex items-center gap-4">
 {/* Audio toggle */}
 <button
 onClick={() => setSoundEnabled(!soundEnabled)}
 className="p-2 rounded-xl bg-white/5 hover:bg-white/10 text-gray-400 hover:text-white transition-colors"
 title={soundEnabled ? 'Mute Beep Alerts' : 'Unmute Beep Alerts'}
 >
 {soundEnabled ? <Volume2 className="w-5 h-5 text-gym-accent" /> : <VolumeX className="w-5 h-5" />}
 </button>

 <button
 onClick={onClose}
 className="px-4 py-2 rounded-xl bg-white/10 hover:bg-gym-danger/20 hover:text-gym-danger text-gray-300 font-bold text-xs transition-all flex items-center gap-1.5"
 >
 <X className="w-4 h-4" />
 Exit Session
 </button>
 </div>
 </div>

 {/* Progress Step Indicator */}
 <div className="h-1.5 w-full bg-white/5 flex">
 {exercises.map((_, i) => (
 <div
 key={i}
 onClick={() => setCurrentIndex(i)}
 className={`flex-1 cursor-pointer transition-all ${
 i === currentIndex
 ? 'bg-gradient-to-r from-gym-accent to-gym-purple shadow-lg shadow-gym-accent/50'
 : i < currentIndex
 ? 'bg-gym-accent/40'
 : 'bg-white/10'
 }`}
 />
 ))}
 </div>

 {/* PR Alert Toast Feed */}
 {prAlerts.length > 0 && (
 <div className="px-6 pt-3 relative z-20 flex flex-col gap-2">
 {prAlerts.slice(0, 2).map((alert, idx) => (
 <div
 key={idx}
 className="px-4 py-2.5 rounded-xl bg-gradient-to-r from-gym-accent/20 via-gym-purple/20 to-gym-accent/20 border border-gym-accent text-white text-xs font-bold flex items-center justify-between shadow-xl animate-bounceOnce"
 >
 <div className="flex items-center gap-2">
 <Trophy className="w-4 h-4 text-gym-accent" />
 <span>{alert}</span>
 </div>
 <button
 onClick={() => setPrAlerts((prev) => prev.filter((_, i) => i !== idx))}
 className="text-gray-400 hover:text-white"
 >
 <X className="w-4 h-4" />
 </button>
 </div>
 ))}
 </div>
 )}

 {/* Main Body */}
 {sessionCompleted ? (
 /* Celebration Completed Screen */
 <div className="flex-1 flex flex-col items-center justify-center p-6 text-center max-w-xl mx-auto relative z-10 animate-fadeIn">
 <div className="p-4 rounded-full bg-gradient-to-tr from-gym-accent to-gym-purple text-gym-dark mb-6 shadow-2xl shadow-gym-accent/30 animate-bounce">
 <Trophy className="w-16 h-16" />
 </div>
 <h2 className="text-3xl font-black uppercase tracking-wider text-white">
 Workout Session Completed! 🏆
 </h2>
 <p className="text-gray-300 mt-2 text-sm leading-relaxed">
 Incredible performance! You completed {totalSetsCompleted} sets across {exercises.length} exercises and calibrated your progressive overload targets.
 </p>

 <div className="grid grid-cols-2 gap-4 w-full mt-8">
 <div className="p-5 rounded-2xl bg-gym-card border border-white/10 text-center">
 <span className="text-xs uppercase font-bold text-gray-400 block">Total Sets</span>
 <span className="text-3xl font-black text-gym-accent mt-1 block">
 {totalSetsCompleted}
 </span>
 </div>
 <div className="p-5 rounded-2xl bg-gym-card border border-white/10 text-center">
 <span className="text-xs uppercase font-bold text-gray-400 block">New PRs Unlocked</span>
 <span className="text-3xl font-black text-gym-purple mt-1 block">
 {prAlerts.length}
 </span>
 </div>
 </div>

 <button
 onClick={onClose}
 className="mt-8 px-8 py-3.5 rounded-xl bg-gradient-to-r from-gym-accent to-gym-purple text-gym-dark font-black text-base shadow-xl shadow-gym-accent/30 hover:opacity-90 transition-all"
 >
 Return to Athlete Dashboard
 </button>
 </div>
 ) : currentExercise ? (
 <div className="flex-1 overflow-y-auto p-6 max-w-7xl mx-auto w-full grid grid-cols-1 lg:grid-cols-3 gap-8 relative z-10">
 {/* Left/Center Column: Active Exercise & Form/Logger Tabs */}
 <div className="lg:col-span-2 space-y-6">
 {/* Active Exercise Header Card */}
 <div className="p-6 rounded-3xl bg-gym-card border border-white/15 flex flex-wrap items-center justify-between gap-4">
 <div>
 <div className="flex items-center gap-2 mb-2">
 <span className="px-3 py-1 rounded-full bg-gym-accent/15 text-gym-accent border border-gym-accent/30 text-xs font-bold uppercase">
 Exercise {currentIndex + 1} of {exercises.length}
 </span>
 <span className="px-3 py-1 rounded-full bg-white/10 text-gray-300 text-xs font-bold uppercase">
 {currentExercise.equipment || 'Barbell'}
 </span>
 </div>
 <h1 className="text-3xl font-black text-white">{currentExercise.name}</h1>
 <p className="text-xs text-gray-400 mt-1">
 Target Load: <strong className="text-white">{currentExercise.sets} Sets × {currentExercise.reps} Reps</strong> • RPE {currentExercise.rpe || '8'}
 </p>
 </div>

 {/* Navigation Arrows */}
 <div className="flex items-center gap-2">
 <button
 onClick={handlePrevExercise}
 disabled={currentIndex === 0}
 className="p-3 rounded-xl bg-white/5 hover:bg-white/10 disabled:opacity-30 disabled:cursor-not-allowed transition-all text-gray-300 hover:text-white"
 >
 <ChevronLeft className="w-6 h-6" />
 </button>
 <button
 onClick={handleNextExercise}
 className="px-5 py-3 rounded-xl bg-gradient-to-r from-gym-accent to-gym-purple text-gym-dark font-black text-sm hover:opacity-90 transition-all flex items-center gap-1 shadow-lg shadow-gym-accent/20"
 >
 <span>{currentIndex === exercises.length - 1 ? 'Finish Workout' : 'Next Exercise'}</span>
 <ChevronRight className="w-5 h-5" />
 </button>
 </div>
 </div>

 {/* Tab Selector */}
 <div className="flex border-b border-white/10 gap-6">
 <button
 onClick={() => setActiveTab('LOGGER')}
 className={`pb-3 text-sm font-bold border-b-2 transition-all flex items-center gap-2 ${
 activeTab === 'LOGGER'
 ? 'border-gym-accent text-gym-accent'
 : 'border-transparent text-gray-400 hover:text-white'
 }`}
 >
 <CheckCircle2 className="w-4 h-4" />
 Live Set Logger & PR Checker
 </button>

 <button
 onClick={() => setActiveTab('FORM_GUIDE')}
 className={`pb-3 text-sm font-bold border-b-2 transition-all flex items-center gap-2 ${
 activeTab === 'FORM_GUIDE'
 ? 'border-gym-purple text-gym-purple'
 : 'border-transparent text-gray-400 hover:text-white'
 }`}
 >
 <Target className="w-4 h-4" />
 Biomechanical Form Execution Guide
 </button>
 </div>

 {/* Tab Content */}
 {activeTab === 'LOGGER' ? (
 <div className="space-y-4">
 <div className="bg-gym-card/70 rounded-3xl p-6 border border-white/10">
 <div className="flex items-center justify-between mb-4">
 <h3 className="text-sm font-bold uppercase tracking-wider text-gray-300 flex items-center gap-2">
 <Flame className="w-4 h-4 text-gym-accent" />
 Interactive Set Tracker (Click checkbox to record & trigger rest timer)
 </h3>
 <span className="text-xs text-gray-400">
 Auto-syncs with MySQL database
 </span>
 </div>

 <div className="space-y-3">
 {Array.from({ length: Number(currentExercise.sets) || 5 }).map((_, setIdx) => {
 const setNum = setIdx + 1;
 const key = `${currentIndex}-${setNum}`;
 const isDone = Boolean(completedSets[key]);
 // Estimate starting weight based on user bodyweight ratio
 const defaultWeight = userWeight > 0 ? Math.round(userWeight * 0.8) : 60;

 return (
 <div
 key={setNum}
 className={`p-4 rounded-2xl border transition-all flex items-center justify-between gap-4 ${
 isDone
 ? 'bg-gym-success/15 border-gym-success/40'
 : 'bg-gym-dark/60 border-white/10 hover:border-white/20'
 }`}
 >
 <div className="flex items-center gap-4">
 <button
 onClick={() => handleToggleSet(currentIndex, setNum, defaultWeight, currentExercise.reps || 5)}
 className={`w-8 h-8 rounded-xl flex items-center justify-center border transition-all ${
 isDone
 ? 'bg-gym-success border-gym-success text-gym-dark shadow-lg shadow-gym-success/30'
 : 'border-white/30 hover:border-gym-accent text-transparent'
 }`}
 >
 <Check className="w-5 h-5 stroke-[3]" />
 </button>

 <div>
 <span className={`text-sm font-black uppercase ${isDone ? 'text-white' : 'text-gray-300'}`}>
 Set {setNum}
 </span>
 <span className="text-xs text-gray-400 block">
 Target: {currentExercise.reps} Reps @ RPE {currentExercise.rpe || 8}
 </span>
 </div>
 </div>

 <div className="flex items-center gap-3">
 <span className="text-xs font-bold px-3 py-1.5 rounded-lg bg-white/5 text-gray-300 border border-white/10">
 {defaultWeight} kg
 </span>
 <span className="text-xs font-bold px-3 py-1.5 rounded-lg bg-white/5 text-gray-300 border border-white/10">
 {currentExercise.reps} Reps
 </span>
 </div>
 </div>
 );
 })}
 </div>
 </div>
 </div>
 ) : (
 /* FORM_GUIDE TAB */
 <div className="space-y-4">
 <div className="bg-gym-card/70 rounded-3xl p-6 border border-white/10 space-y-6">
 {/* Step 1 */}
 <div>
 <h4 className="text-xs font-bold uppercase tracking-wider text-gym-accent mb-2 flex items-center gap-2">
 <Target className="w-4 h-4" /> 1. Machine & Equipment Setup
 </h4>
 <p className="text-sm text-gray-300 leading-relaxed bg-gym-dark/60 p-4 rounded-2xl border border-white/5">
 {stripHtml(currentExercise.machineSetup) || 'Adjust seat height so handles align with chest level. Secure safety stops and verify bar centering.'}
 </p>
 </div>

 {/* Step 2 */}
 <div>
 <h4 className="text-xs font-bold uppercase tracking-wider text-gym-purple mb-2 flex items-center gap-2">
 <Activity className="w-4 h-4" /> 2. Form & Biomechanical Execution
 </h4>
 <p className="text-sm text-gray-300 leading-relaxed bg-gym-dark/60 p-4 rounded-2xl border border-white/5">
 {stripHtml(currentExercise.formGuide) || 'Maintain neutral spine, brace core, control eccentric phase for 3 seconds, pause at bottom, drive up explosively.'}
 </p>
 </div>

 {/* Step 3: Injury Prevention & Breathing Cue */}
 <div className="p-4 rounded-2xl bg-gym-danger/10 border border-gym-danger/30 flex items-start gap-3">
 <AlertTriangle className="w-5 h-5 text-gym-danger shrink-0 mt-0.5" />
 <div>
 <span className="text-xs font-bold text-gym-danger uppercase tracking-wider block">
 Injury Prevention & Breathing Cadence
 </span>
 <p className="text-xs text-gray-300 mt-1">
 <strong>3-0-1-0 Tempo:</strong> 3s eccentric lowering, 0s pause, 1s explosive concentric lift. Inhale deeply before lowering, exhale during the drive up. Keep shoulder blades retracted throughout.
 </p>
 </div>
 </div>
 </div>
 </div>
 )}
 </div>

 {/* Right Column: Interactive Rest Timer & Quick Controls */}
 <div className="space-y-6">
 <div className="p-6 rounded-3xl bg-gym-card border border-white/15 text-center flex flex-col items-center justify-between">
 <div className="flex items-center justify-between w-full mb-4">
 <span className="text-xs uppercase font-bold tracking-wider text-gray-400">
 Inter-Set Rest Timer
 </span>
 <span className="text-xs font-bold text-gym-accent">
 Target: {currentExercise.restSeconds || 90}s
 </span>
 </div>

 {/* Circular SVG Timer Ring */}
 <div className="relative w-44 h-44 flex items-center justify-center my-2">
 <svg className="w-full h-full -rotate-90" viewBox="0 0 120 120">
 <circle
 cx="60"
 cy="60"
 r={radius}
 className="stroke-white/10"
 strokeWidth="8"
 fill="transparent"
 />
 <circle
 cx="60"
 cy="60"
 r={radius}
 className="stroke-gym-accent transition-all duration-300"
 strokeWidth="8"
 strokeDasharray={circumference}
 strokeDashoffset={strokeDashoffset}
 strokeLinecap="round"
 fill="transparent"
 />
 </svg>

 <div className="absolute flex flex-col items-center justify-center">
 <span className="text-4xl font-black text-white tracking-tight">
 {Math.floor(remainingSeconds / 60)}:
 {(remainingSeconds % 60).toString().padStart(2, '0')}
 </span>
 <span className="text-[10px] uppercase font-bold text-gray-400 mt-1">
 {isRunning ? 'Resting...' : 'Ready'}
 </span>
 </div>
 </div>

 {/* Timer Preset Buttons */}
 <div className="grid grid-cols-4 gap-2 w-full mt-4">
 {[30, 60, 90, 120].map((sec) => (
 <button
 key={sec}
 onClick={() => {
 setTimerSeconds(sec);
 setRemainingSeconds(sec);
 setIsRunning(true);
 }}
 className="py-1.5 px-2 rounded-xl bg-white/5 hover:bg-white/10 text-xs font-bold text-gray-300 hover:text-white transition-colors border border-white/5"
 >
 {sec}s
 </button>
 ))}
 </div>

 {/* Timer Control Buttons */}
 <div className="flex items-center gap-3 w-full mt-4">
 <button
 onClick={() => setIsRunning(!isRunning)}
 className="flex-1 py-3 rounded-xl bg-gradient-to-r from-gym-accent to-gym-purple text-gym-dark font-black text-sm hover:opacity-90 transition-all flex items-center justify-center gap-2 shadow-lg shadow-gym-accent/20"
 >
 {isRunning ? <Pause className="w-4 h-4" /> : <Play className="w-4 h-4 fill-current" />}
 <span>{isRunning ? 'Pause Timer' : 'Start Timer'}</span>
 </button>

 <button
 onClick={() => {
 setIsRunning(false);
 setRemainingSeconds(timerSeconds);
 }}
 className="p-3 rounded-xl bg-white/10 hover:bg-white/15 text-gray-300 hover:text-white transition-colors"
 title="Reset Timer"
 >
 <RotateCcw className="w-5 h-5" />
 </button>
 </div>
 </div>

 {/* Quick Coaching Tip Card */}
 <div className="p-5 rounded-3xl bg-gradient-to-br from-gym-purple/20 to-gym-dark border border-gym-purple/30">
 <div className="flex items-center gap-2 text-gym-purple mb-2">
 <Sparkles className="w-4 h-4" />
 <span className="text-xs font-bold uppercase tracking-wider">Coaching Tip</span>
 </div>
 <p className="text-xs text-gray-300 leading-relaxed">
 Aim for progressive overload on every lift. When you complete all sets at the target rep range with clean form, increase weight by 2.5 kg on your next session.
 </p>
 </div>
 </div>
 </div>
 ) : null}
 </div>
 );
};

export default LiveWorkoutCompanion;
