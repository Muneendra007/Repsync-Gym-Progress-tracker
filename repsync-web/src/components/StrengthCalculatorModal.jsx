import React, { useState, useEffect } from 'react';
import {
 X,
 Calculator,
 Trophy,
 Activity,
 Flame,
 TrendingUp,
 Award,
 Info,
 CheckCircle2
} from 'lucide-react';

const StrengthCalculatorModal = ({ isOpen, onClose, initialExercise = 'Bench Press', userWeightKg = 75, onLogPr }) => {
 const [exerciseName, setExerciseName] = useState(initialExercise);
 const [weightKg, setWeightKg] = useState(100);
 const [reps, setReps] = useState(5);
 const [bodyweightKg, setBodyweightKg] = useState(userWeightKg || 75);

 useEffect(() => {
 if (initialExercise) setExerciseName(initialExercise);
 if (userWeightKg) setBodyweightKg(userWeightKg);
 }, [initialExercise, userWeightKg]);

 if (!isOpen) return null;

 // 1RM Formulas
 const w = Number(weightKg) || 0;
 const r = Math.max(1, Math.min(30, Number(reps) || 1));
 const bw = Number(bodyweightKg) || 1;

 const epley1Rm = r === 1 ? w : Math.round((w * (1 + r / 30)) * 10) / 10;
 const brzycki1Rm = r === 1 ? w : Math.round((w * (36 / (37 - r))) * 10) / 10;
 const avg1Rm = Math.round(((epley1Rm + brzycki1Rm) / 2) * 10) / 10;

 // Strength to Bodyweight Ratio
 const ratio = Math.round((avg1Rm / bw) * 100) / 100;

 // Classification Standard
 const getStandard = (rt) => {
 if (rt >= 2.0) return { label: 'Elite Athlete', color: 'text-gym-purple', bg: 'bg-gym-purple/20 border-gym-purple/40' };
 if (rt >= 1.5) return { label: 'Advanced Lifter', color: 'text-gym-accent', bg: 'bg-gym-accent/20 border-gym-accent/40' };
 if (rt >= 1.0) return { label: 'Intermediate', color: 'text-gym-success', bg: 'bg-gym-success/20 border-gym-success/40' };
 return { label: 'Novice / Developing', color: 'text-gray-300', bg: 'bg-white/10 border-white/20' };
 };

 const standard = getStandard(ratio);

 // Training Intensity Breakdown
 const intensityTable = [
 { percent: 95, repsTarget: '1–2 Reps', focus: 'Maximal Power & Neural Drive', color: 'text-gym-purple' },
 { percent: 90, repsTarget: '2–3 Reps', focus: 'Heavy Strength Overload', color: 'text-gym-purple' },
 { percent: 85, repsTarget: '4–5 Reps', focus: '5x5 Core Strength Standard', color: 'text-gym-accent' },
 { percent: 80, repsTarget: '6–8 Reps', focus: 'Hypertrophy & Strength Hybrid', color: 'text-gym-accent' },
 { percent: 75, repsTarget: '8–10 Reps', focus: 'Pure Muscle Hypertrophy', color: 'text-gym-success' },
 { percent: 70, repsTarget: '10–12 Reps', focus: 'Metabolic Conditioning & Stamina', color: 'text-gray-300' }
 ];

 return (
 <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-md animate-fadeIn">
 <div className="relative w-full max-w-2xl bg-gym-card border border-white/15 rounded-3xl shadow-2xl overflow-hidden">
 {/* Cyberpunk Glow Backgrounds */}
 <div className="absolute top-0 right-0 w-64 h-64 bg-gym-accent/10 rounded-full blur-3xl pointer-events-none" />
 <div className="absolute bottom-0 left-0 w-64 h-64 bg-gym-purple/10 rounded-full blur-3xl pointer-events-none" />

 {/* Modal Header */}
 <div className="p-6 border-b border-white/10 flex items-center justify-between relative z-10">
 <div className="flex items-center gap-3">
 <div className="p-2.5 rounded-2xl bg-gradient-to-tr from-gym-accent to-gym-purple text-gym-dark">
 <Calculator className="w-6 h-6" />
 </div>
 <div>
 <h3 className="text-xl font-black text-white tracking-wide">
 1-Rep Max & Strength Standard Gauge
 </h3>
 <p className="text-xs text-gray-400">
 Sports-science Epley & Brzycki equations calibrated for {exerciseName}
 </p>
 </div>
 </div>
 <button
 onClick={onClose}
 className="p-2 rounded-full bg-white/5 hover:bg-white/10 text-gray-400 hover:text-white transition-colors"
 >
 <X className="w-6 h-6" />
 </button>
 </div>

 {/* Modal Body */}
 <div className="p-6 space-y-6 max-h-[80vh] overflow-y-auto relative z-10">
 {/* Input Row */}
 <div className="grid grid-cols-1 sm:grid-cols-4 gap-4 bg-gym-dark/60 p-4 rounded-2xl border border-white/10">
 <div className="sm:col-span-1">
 <label className="block text-[11px] font-bold uppercase tracking-wider text-gray-400 mb-1">
 Exercise Name
 </label>
 <input
 type="text"
 value={exerciseName}
 onChange={(e) => setExerciseName(e.target.value)}
 className="w-full px-3 py-2 rounded-xl bg-gym-card border border-white/15 text-white text-sm focus:border-gym-accent focus:outline-none"
 />
 </div>
 <div>
 <label className="block text-[11px] font-bold uppercase tracking-wider text-gray-400 mb-1">
 Weight Lifted (kg)
 </label>
 <input
 type="number"
 min="1"
 value={weightKg}
 onChange={(e) => setWeightKg(e.target.value)}
 className="w-full px-3 py-2 rounded-xl bg-gym-card border border-white/15 text-white font-bold text-sm focus:border-gym-accent focus:outline-none"
 />
 </div>
 <div>
 <label className="block text-[11px] font-bold uppercase tracking-wider text-gray-400 mb-1">
 Reps Performed
 </label>
 <input
 type="number"
 min="1"
 max="30"
 value={reps}
 onChange={(e) => setReps(e.target.value)}
 className="w-full px-3 py-2 rounded-xl bg-gym-card border border-white/15 text-white font-bold text-sm focus:border-gym-accent focus:outline-none"
 />
 </div>
 <div>
 <label className="block text-[11px] font-bold uppercase tracking-wider text-gray-400 mb-1">
 Bodyweight (kg)
 </label>
 <input
 type="number"
 min="30"
 value={bodyweightKg}
 onChange={(e) => setBodyweightKg(e.target.value)}
 className="w-full px-3 py-2 rounded-xl bg-gym-card border border-white/15 text-white font-bold text-sm focus:border-gym-accent focus:outline-none"
 />
 </div>
 </div>

 {/* Result Highlight Cards */}
 <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
 <div className="p-5 rounded-2xl bg-gradient-to-br from-gym-accent/15 to-transparent border border-gym-accent/30 text-center">
 <span className="text-xs uppercase font-bold text-gray-400 block">
 Estimated 1RM (Avg)
 </span>
 <span className="text-4xl font-black text-white mt-1 block">
 {avg1Rm} <span className="text-lg font-normal text-gym-accent">kg</span>
 </span>
 <span className="text-[10px] text-gray-400 mt-1 block">
 Epley: {epley1Rm}kg • Brzycki: {brzycki1Rm}kg
 </span>
 </div>

 <div className="p-5 rounded-2xl bg-gradient-to-br from-gym-purple/15 to-transparent border border-gym-purple/30 text-center">
 <span className="text-xs uppercase font-bold text-gray-400 block">
 Strength-to-Weight
 </span>
 <span className="text-4xl font-black text-white mt-1 block">
 {ratio}x <span className="text-lg font-normal text-gym-purple">BW</span>
 </span>
 <span className="text-[10px] text-gray-400 mt-1 block">
 Relative Power Ratio
 </span>
 </div>

 <div className={`p-5 rounded-2xl border text-center flex flex-col items-center justify-center ${standard.bg}`}>
 <Trophy className={`w-7 h-7 mb-1 ${standard.color}`} />
 <span className="text-xs uppercase font-bold text-gray-400 block">
 Standard Rating
 </span>
 <span className={`text-xl font-black ${standard.color} mt-0.5 block`}>
 {standard.label}
 </span>
 </div>
 </div>

 {/* Training Intensity Breakdown Table */}
 <div className="bg-gym-dark/60 rounded-2xl p-5 border border-white/10">
 <h4 className="text-sm font-bold text-white mb-3 flex items-center gap-2">
 <Flame className="w-4 h-4 text-gym-accent" />
 Calibrated Training Load Table (Based on {avg1Rm} kg 1RM)
 </h4>
 <div className="overflow-x-auto">
 <table className="w-full text-left border-collapse">
 <thead>
 <tr className="border-b border-white/10 text-[11px] uppercase font-bold text-gray-400">
 <th className="py-2 px-3">Intensity</th>
 <th className="py-2 px-3">Target Weight</th>
 <th className="py-2 px-3">Rep Range</th>
 <th className="py-2 px-3">Training Adaptation Focus</th>
 </tr>
 </thead>
 <tbody className="divide-y divide-white/5 text-sm">
 {intensityTable.map((item) => {
 const targetWeight = Math.round((avg1Rm * (item.percent / 100)) * 10) / 10;
 return (
 <tr key={item.percent} className="hover:bg-white/5 transition-colors">
 <td className="py-2.5 px-3 font-black text-white">
 {item.percent}%
 </td>
 <td className={`py-2.5 px-3 font-black ${item.color}`}>
 {targetWeight} kg
 </td>
 <td className="py-2.5 px-3 text-gray-300 font-medium">
 {item.repsTarget}
 </td>
 <td className="py-2.5 px-3 text-xs text-gray-400">
 {item.focus}
 </td>
 </tr>
 );
 })}
 </tbody>
 </table>
 </div>
 </div>
 </div>

 {/* Modal Footer */}
 <div className="p-6 border-t border-white/10 bg-gym-dark/80 flex flex-wrap items-center justify-between gap-4 relative z-10">
 <div className="flex items-center gap-2 text-xs text-gray-400">
 <Info className="w-4 h-4 text-gym-accent" />
 <span>Use these loads in your guided workout session for optimal overload.</span>
 </div>

 <div className="flex items-center gap-3">
 <button
 onClick={onClose}
 className="px-5 py-2.5 rounded-xl bg-white/10 hover:bg-white/15 text-white font-bold text-sm transition-colors"
 >
 Close
 </button>
 {onLogPr && (
 <button
 onClick={() => {
 onLogPr(exerciseName, Number(weightKg), Number(reps));
 onClose();
 }}
 className="px-5 py-2.5 rounded-xl bg-gradient-to-r from-gym-accent to-gym-purple hover:from-gym-purple hover:to-gym-accent text-gym-dark font-black text-sm transition-all shadow-lg shadow-gym-accent/20"
 >
 Log as PR Attempt
 </button>
 )}
 </div>
 </div>
 </div>
 </div>
 );
};

export default StrengthCalculatorModal;
