import React, { useEffect, useState } from 'react';
import axiosClient from '../api/axiosClient';
import {
 Trophy,
 Flame,
 Award,
 PlusCircle,
 RefreshCw,
 Sparkles,
 Calculator
} from 'lucide-react';
import StrengthCalculatorModal from '../components/StrengthCalculatorModal';

const PRAnalytics = () => {
 const [profile, setProfile] = useState(null);
 const [prs, setPrs] = useState([]);
 const [exercises, setExercises] = useState([]);
 const [loading, setLoading] = useState(true);

 // New PR Check Form State
 const [selectedEx, setSelectedEx] = useState('');
 const [weightKg, setWeightKg] = useState('');
 const [reps, setReps] = useState('');
 const [submitting, setSubmitting] = useState(false);
 const [celebration, setCelebration] = useState(null);
 const [filterType, setFilterType] = useState('ALL');
 const [calcModalOpen, setCalcModalOpen] = useState(false);
 const [calcExercise, setCalcExercise] = useState('Bench Press (CHEST)');
 const [chartLift, setChartLift] = useState('BENCH');

 const liftProgressionData = {
 BENCH: { label: 'Barbell Bench Press', current: 100, start: 82.5, data: [82.5, 85, 87.5, 90, 95, 100], growth: '+21.2%' },
 SQUAT: { label: 'Barbell Back Squat', current: 142.5, start: 115, data: [115, 120, 125, 130, 135, 142.5], growth: '+23.9%' },
 DEADLIFT: { label: 'Conventional Deadlift', current: 175, start: 140, data: [140, 145, 150, 157.5, 165, 175], growth: '+25.0%' },
 OHP: { label: 'Overhead Shoulder Press', current: 67.5, start: 52.5, data: [52.5, 55, 57.5, 60, 62.5, 67.5], growth: '+28.6%' }
 };
 const currentLiftData = liftProgressionData[chartLift];

 const fetchData = async () => {
 setLoading(true);
 try {
 const [profRes, prRes, exRes] = await Promise.all([
 axiosClient.get('/analytics/profile'),
 axiosClient.get('/analytics/prs'),
 axiosClient.get('/exercises')
 ]);
 setProfile(profRes.data);
 setPrs(prRes.data);
 setExercises(exRes.data);
 if (exRes.data.length > 0 && !selectedEx) {
 setSelectedEx(exRes.data[0].id);
 }
 } catch (err) {
 console.error('Failed to load PR analytics data:', err);
 } finally {
 setLoading(false);
 }
 };

 useEffect(() => {
 fetchData();
 }, []);

 const handleCheckPR = async (e) => {
 e.preventDefault();
 if (!selectedEx || (!weightKg && !reps)) return;

 const exerciseObj = exercises.find((ex) => String(ex.id) === String(selectedEx));
 setSubmitting(true);
 setCelebration(null);

 try {
 const payload = {
 exerciseId: Number(selectedEx),
 exerciseName: exerciseObj ? exerciseObj.name : 'Unknown Exercise',
 weightKg: Number(weightKg) || 0,
 reps: Number(reps) || 0
 };

 const res = await axiosClient.post('/analytics/prs/check', payload);
 if (res.data.newPrAchieved) {
 setCelebration({
 title: 'NEW PERSONAL RECORD UNLOCKED! 🏆',
 message: `Congratulations! You set ${res.data.achievedPRs.length} new record(s) on ${payload.exerciseName}!`
 });
 } else {
 setCelebration({
 title: 'Good Set Logged!',
 message: `Your set on ${payload.exerciseName} was logged, but did not exceed your current all-time PR.`
 });
 }
 // Refresh PR history
 const prRes = await axiosClient.get('/analytics/prs');
 setPrs(prRes.data);
 setWeightKg('');
 setReps('');
 } catch (err) {
 console.error('Error logging PR check:', err);
 } finally {
 setSubmitting(false);
 }
 };

 const filteredPrs = prs.filter((pr) => {
 if (filterType === 'ALL') return true;
 return pr.recordType === filterType;
 });

 return (
 <div className="min-h-screen pb-20">
 {/* Top Banner */}
 <div className="relative overflow-hidden bg-gradient-to-b from-gym-card to-gym-dark border-b border-white/10 pt-10 pb-12 px-6">
 <div className="absolute top-0 right-1/3 w-96 h-96 bg-gym-success/10 rounded-full blur-3xl pointer-events-none" />
 <div className="max-w-7xl mx-auto">
 <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
 <div>
 <span className="text-xs font-bold uppercase tracking-widest text-gym-success mb-2 block flex items-center gap-2">
 <Trophy className="w-4 h-4" /> Personal Record Intelligence
 </span>
 <h1 className="text-4xl md:text-5xl font-black text-white tracking-tight">
 PR <span className="text-gym-success">Analytics</span>
 </h1>
 <p className="text-gray-400 mt-2 max-w-2xl text-sm md:text-base">
 Track personal bests across compound and isolation lifts with real-time MySQL persistence.
 </p>
 </div>

 {/* Total PR Badge */}
 <div className="glass-card bg-gym-success/10 border-gym-success/30 px-6 py-4 flex items-center gap-4">
 <Award className="w-10 h-10 text-gym-success" />
 <div>
 <span className="text-xs font-bold uppercase text-gray-300">Total PRs Achieved</span>
 <div className="text-3xl font-black text-white">{prs.length}</div>
 </div>
 </div>
 </div>
 </div>
 </div>

 <div className="max-w-7xl mx-auto px-6 mt-8">
 {/* Celebration Banner */}
 {celebration && (
 <div className="mb-8 p-6 rounded-2xl bg-gradient-to-r from-gym-success/20 via-gym-accent/20 to-gym-purple/20 border border-gym-success/50 shadow-2xl animate-bounce-once flex items-center justify-between">
 <div className="flex items-center gap-4">
 <Sparkles className="w-8 h-8 text-gym-accent shrink-0" />
 <div>
 <h4 className="text-lg font-black text-white">{celebration.title}</h4>
 <p className="text-sm text-gray-200">{celebration.message}</p>
 </div>
 </div>
 <button
 onClick={() => setCelebration(null)}
 className="text-xs text-gray-400 hover:text-white font-bold"
 >
 Dismiss
 </button>
 </div>
 )}

 {/* 1RM Progression Chart & Volume Analytics (Feature 5) */}
 <div className="bg-gradient-to-br from-gym-card via-gym-dark to-gym-card border-2 border-gym-accent/30 rounded-3xl p-6 sm:p-8 mb-8 shadow-2xl">
 <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-6 mb-8 pb-6 border-b border-white/10">
 <div>
 <div className="flex items-center gap-2 mb-2">
 <span className="px-3 py-1 rounded-full text-xs font-black bg-gradient-to-r from-gym-accent to-gym-purple text-gym-dark uppercase tracking-wider flex items-center gap-1.5 shadow-lg shadow-gym-accent/20">
 <Flame className="w-4 h-4 fill-current" />
 <span>1RM PREDICTION & VOLUME ANALYTICS</span>
 </span>
 <span className="text-xs text-gray-400 font-bold">
 • Scientific Brzycki Formula Engine
 </span>
 </div>
 <h2 className="text-2xl font-black text-white tracking-tight">
 6-Week Estimated 1-Rep Max (1RM) Progression
 </h2>
 <p className="text-sm text-gray-400 mt-1">
 Visualizing strength trajectory and total weekly tonnage across major compound lifts.
 </p>
 </div>

 {/* Weekly Volume Load Metric Box */}
 <div className="flex items-center gap-6 bg-gym-dark/90 border border-white/10 p-4 rounded-2xl">
 <div>
 <span className="text-[11px] font-extrabold uppercase text-gray-400 block">
 Weekly Volume Load
 </span>
 <div className="text-2xl font-black text-white">
 14,850 <span className="text-sm text-gray-400 font-bold">kg</span>
 </div>
 </div>
 <div className="px-3 py-1.5 rounded-xl bg-gym-accent/20 text-gym-accent font-black text-xs border border-gym-accent/40">
 +18.4% vs last week
 </div>
 </div>
 </div>

 {/* Compound Lift Selector Tabs */}
 <div className="flex flex-wrap items-center gap-2 mb-6">
 {Object.keys(liftProgressionData).map((key) => {
 const lift = liftProgressionData[key];
 const isSelected = chartLift === key;
 return (
 <button
 key={key}
 onClick={() => setChartLift(key)}
 className={`px-4 py-2.5 rounded-xl text-xs font-black transition-all flex items-center gap-2 border ${
 isSelected
 ? 'bg-gym-accent text-gym-dark border-gym-accent shadow-lg shadow-gym-accent/20 scale-105'
 : 'bg-white/5 text-gray-400 border-white/10 hover:text-white hover:bg-white/10'
 }`}
 >
 <Trophy className="w-4 h-4" />
 <span>{lift.label}</span>
 <span className={`px-1.5 py-0.5 rounded text-[10px] ${isSelected ? 'bg-black/30 text-white' : 'bg-gym-accent/20 text-gym-accent'}`}>
 {lift.current} kg
 </span>
 </button>
 );
 })}
 </div>

 {/* Visual 6-Week Bar/Line Graph */}
 <div className="bg-gym-dark/90 rounded-2xl p-6 border border-white/10">
 <div className="flex items-center justify-between mb-6">
 <div>
 <span className="text-xs font-bold text-gray-400 uppercase block">Selected Compound Lift</span>
 <h3 className="text-lg font-black text-white">{currentLiftData.label}</h3>
 </div>
 <div className="text-right">
 <span className="text-xs font-bold text-gray-400 uppercase block">6-Week Growth</span>
 <span className="text-lg font-black text-gym-accent">{currentLiftData.growth}</span>
 </div>
 </div>

 {/* Bars */}
 <div className="grid grid-cols-6 gap-3 sm:gap-6 items-end h-48 pt-6 pb-2 border-b border-white/10">
 {currentLiftData.data.map((val, idx) => {
 const maxVal = Math.max(...currentLiftData.data);
 const heightPercent = Math.round((val / maxVal) * 100);
 const isLatest = idx === currentLiftData.data.length - 1;
 return (
 <div key={idx} className="flex flex-col items-center h-full justify-end group">
 <span className={`text-xs font-black mb-2 transition-transform group-hover:-translate-y-1 ${isLatest ? 'text-gym-accent scale-110' : 'text-gray-300'}`}>
 {val} kg
 </span>
 <div className="w-full max-w-[48px] bg-white/5 rounded-t-xl h-full flex items-end">
 <div
 className={`w-full rounded-t-xl transition-all duration-700 ${
 isLatest
 ? 'bg-gradient-to-t from-gym-purple to-gym-accent shadow-lg shadow-gym-accent/30'
 : 'bg-gradient-to-t from-gray-700 to-gray-500 hover:from-gym-accent/50 hover:to-gym-accent'
 }`}
 style={{ height: `${heightPercent}%` }}
 />
 </div>
 </div>
 );
 })}
 </div>

 {/* X-Axis Labels */}
 <div className="grid grid-cols-6 gap-3 sm:gap-6 text-center pt-3">
 {['Week 1', 'Week 2', 'Week 3', 'Week 4', 'Week 5', 'Week 6 (Now)'].map((label, idx) => (
 <span key={idx} className={`text-[11px] font-extrabold ${idx === 5 ? 'text-gym-accent' : 'text-gray-400'}`}>
 {label}
 </span>
 ))}
 </div>
 </div>
 </div>

 <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
 {/* PR Check / Log Form */}
 <div className="lg:col-span-1">
 <div className="glass-card">
 <div className="flex items-center gap-2 mb-4">
 <PlusCircle className="w-5 h-5 text-gym-accent" />
 <h3 className="text-lg font-black text-white">Log Set & Test PR</h3>
 </div>
 <p className="text-xs text-gray-400 mb-6">
 Enter your weight and reps for any exercise. The engine automatically checks whether you beat your previous MySQL max record.
 </p>

 <form onSubmit={handleCheckPR} className="space-y-4">
 <div>
 <label className="text-xs font-bold uppercase text-gray-400 block mb-1.5">
 Exercise
 </label>
 <select
 value={selectedEx}
 onChange={(e) => setSelectedEx(e.target.value)}
 className="w-full bg-gym-dark border border-white/10 rounded-xl px-4 py-3 text-sm text-white focus:outline-none focus:border-gym-accent"
 >
 {exercises.map((ex) => (
 <option key={ex.id} value={ex.id}>
 {ex.name} ({ex.muscleGroup})
 </option>
 ))}
 </select>
 </div>

 <div className="grid grid-cols-2 gap-4">
 <div>
 <label className="text-xs font-bold uppercase text-gray-400 block mb-1.5">
 Weight (kg)
 </label>
 <input
 type="number"
 step="0.5"
 placeholder="e.g. 100"
 value={weightKg}
 onChange={(e) => setWeightKg(e.target.value)}
 className="w-full bg-gym-dark border border-white/10 rounded-xl px-4 py-3 text-sm text-white focus:outline-none focus:border-gym-accent"
 />
 </div>

 <div>
 <label className="text-xs font-bold uppercase text-gray-400 block mb-1.5">
 Reps Performed
 </label>
 <input
 type="number"
 placeholder="e.g. 8"
 value={reps}
 onChange={(e) => setReps(e.target.value)}
 className="w-full bg-gym-dark border border-white/10 rounded-xl px-4 py-3 text-sm text-white focus:outline-none focus:border-gym-accent"
 />
 </div>
 </div>

 <button
 type="submit"
 disabled={submitting}
 className="btn-accent w-full flex items-center justify-center gap-2 py-3 mt-2 font-black text-sm"
 >
 {submitting ? (
 <RefreshCw className="w-4 h-4 animate-spin" />
 ) : (
 <Trophy className="w-4 h-4" />
 )}
 <span>Check & Save Record</span>
 </button>
 </form>
 </div>

 {/* 1RM & Sports Science Gauge Card */}
 <div className="glass-card mt-6 bg-gradient-to-br from-gym-card to-gym-purple/10 border-gym-purple/30">
 <div className="flex items-center gap-2 mb-2 text-gym-purple font-bold text-sm">
 <Calculator className="w-4 h-4" />
 <span>Strength Standards Gauge</span>
 </div>
 <h4 className="text-base font-black text-white mb-1">
 1-Rep Max & Wilks Ratio
 </h4>
 <p className="text-xs text-gray-400 mb-4">
 Calculate Epley & Brzycki 1RM targets and see where you rank in strength-to-bodyweight classification.
 </p>
 <button
 onClick={() => {
 setCalcExercise(selectedEx || 'Bench Press (CHEST)');
 setCalcModalOpen(true);
 }}
 className="w-full py-2.5 rounded-xl bg-gym-purple/20 hover:bg-gym-purple/30 border border-gym-purple/40 text-xs font-bold text-gym-purple hover:text-white transition-colors flex items-center justify-center gap-1.5"
 >
 <Calculator className="w-4 h-4" />
 <span>Open 1RM Calculator</span>
 </button>
 </div>
 </div>

 {/* PR History Feed */}
 <div className="lg:col-span-2">
 <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-6">
 <div>
 <h3 className="text-2xl font-black text-white">Historical Records Feed</h3>
 <p className="text-sm text-gray-400">All-time personal best milestones synced with MySQL database.</p>
 </div>

 {/* Filter Tabs */}
 <div className="flex items-center gap-2 bg-gym-card p-1 rounded-xl border border-white/10">
 {[
 { id: 'ALL', label: 'All Records' },
 { id: 'MAX_WEIGHT', label: 'Max Weight PRs' },
 { id: 'MAX_REPS', label: 'Max Rep PRs' },
 ].map((tab) => (
 <button
 key={tab.id}
 onClick={() => setFilterType(tab.id)}
 className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
 filterType === tab.id
 ? 'bg-gym-accent text-gym-dark shadow'
 : 'text-gray-400 hover:text-white'
 }`}
 >
 {tab.label}
 </button>
 ))}
 </div>
 </div>

 {loading ? (
 <div className="glass-card text-center py-16 text-gray-400">
 <RefreshCw className="w-8 h-8 animate-spin mx-auto mb-3 text-gym-accent" />
 <p>Loading personal record analytics from server...</p>
 </div>
 ) : filteredPrs.length > 0 ? (
 <div className="space-y-4">
 {filteredPrs.map((pr) => {
 const isWeightPr = pr.recordType === 'MAX_WEIGHT';
 return (
 <div
 key={pr.id}
 className="glass-card flex items-center justify-between hover:border-gym-success/40 transition-all duration-300"
 >
 <div className="flex items-center gap-4">
 <div
 className={`p-3 rounded-2xl ${
 isWeightPr
 ? 'bg-gym-success/15 text-gym-success border border-gym-success/30'
 : 'bg-gym-purple/15 text-gym-purple border border-gym-purple/30'
 }`}
 >
 {isWeightPr ? <Trophy className="w-6 h-6" /> : <Flame className="w-6 h-6" />}
 </div>

 <div>
 <h4 className="text-lg font-black text-white">{pr.exerciseName}</h4>
 <span className="text-xs text-gray-400 font-medium">
 Achieved on {pr.achievedDate || 'Recent'}
 </span>
 </div>
 </div>

 <div className="text-right">
 <span
 className={`text-xs uppercase font-bold px-2.5 py-0.5 rounded-full ${
 isWeightPr
 ? 'bg-gym-success/20 text-gym-success'
 : 'bg-gym-purple/20 text-gym-purple'
 }`}
 >
 {pr.recordType === 'MAX_WEIGHT' ? 'Max Weight' : 'Max Reps'}
 </span>
 <div className="text-2xl font-black text-white mt-1">
 {pr.recordValue}{' '}
 <span className="text-sm font-semibold text-gray-400">
 {isWeightPr ? 'kg' : 'reps'}
 </span>
 </div>
 </div>
 </div>
 );
 })}
 </div>
 ) : (
 <div className="glass-card text-center py-16">
 <Trophy className="w-12 h-12 text-gray-600 mx-auto mb-3" />
 <h4 className="text-lg font-bold text-gray-300 mb-1">No PRs Found</h4>
 <p className="text-xs text-gray-400">
 Log your first set using the form on the left to set your baseline record!
 </p>
 </div>
 )}
 </div>
 </div>
 </div>

 <StrengthCalculatorModal
 isOpen={calcModalOpen}
 onClose={() => setCalcModalOpen(false)}
 initialExercise={calcExercise}
 userWeightKg={profile?.weightKg || 75}
 onLogPr={(exerciseName, w, r) => {
 setSelectedEx(exerciseName);
 setWeightKg(String(w));
 setReps(String(r));
 }}
 />
 </div>
 );
};

export default PRAnalytics;
