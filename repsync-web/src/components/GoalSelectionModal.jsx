import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { Target, Dumbbell, Flame, Activity, Check, X, ArrowRight, Sparkles } from 'lucide-react';

const GoalSelectionModal = ({ isOpen, onClose, onGoalUpdated }) => {
 const { user, updateGoal } = useAuth();
 const [selectedGoal, setSelectedGoal] = useState(user?.fitnessGoal || 'STRENGTH');
 const [saving, setSaving] = useState(false);

 if (!isOpen) return null;

 const goals = [
 {
 id: 'STRENGTH',
 title: 'Max Strength (Power 5x5)',
 subtitle: 'Build maximal raw power and neural efficiency',
 icon: Dumbbell,
 color: 'text-gym-accent border-gym-accent',
 bg: 'bg-gym-accent/10',
 description: 'Heavy compound lifts (Squat, Bench, Deadlift, OHP) with 5x5 rep schemes and 3+ min rest periods.',
 weeklyPlan: '3 Heavy Full-Body / Power Days + 4 Recovery Days'
 },
 {
 id: 'MUSCLE_GAIN',
 title: 'Build Muscle (Hypertrophy 4x10)',
 subtitle: 'Maximize muscle cross-sectional growth & aesthetics',
 icon: Target,
 color: 'text-gym-purple border-gym-purple',
 bg: 'bg-gym-purple/10',
 description: 'Push / Pull / Legs split focusing on volume, time under tension, and progressive muscle overload.',
 weeklyPlan: '4-5 Hypertrophy Training Days + 2 Rest Days'
 },
 {
 id: 'FAT_LOSS',
 title: 'Lose Fat (Metabolic Conditioning)',
 subtitle: 'Maximize calorie expenditure and preserve lean mass',
 icon: Flame,
 color: 'text-gym-warning border-gym-warning',
 bg: 'bg-gym-warning/10',
 description: 'High-tempo circuit style lifts (3x15) combined with cardio intervals and core conditioning.',
 weeklyPlan: '4 Metabolic & Cardio Days + 3 Active Recovery Days'
 },
 {
 id: 'ENDURANCE',
 title: 'Build Endurance (Stamina 3x20)',
 subtitle: 'Enhance aerobic conditioning and muscular stamina',
 icon: Activity,
 color: 'text-gym-success border-gym-success',
 bg: 'bg-gym-success/10',
 description: 'High-rep muscular endurance training (15-20 reps) paired with sustained cardiovascular sessions.',
 weeklyPlan: '4 Conditioning Days + 3 Mobility/Rest Days'
 }
 ];

 const handleSave = async () => {
 setSaving(true);
 const res = await updateGoal(selectedGoal);
 setSaving(false);
 if (res.success) {
 if (onGoalUpdated) {
 onGoalUpdated(selectedGoal);
 }
 onClose();
 }
 };

 return (
 <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-md animate-fade-in">
 <div className="relative w-full max-w-2xl bg-gym-dark border border-white/10 rounded-3xl p-6 md:p-8 shadow-2xl overflow-hidden max-h-[90vh] overflow-y-auto">
 {/* Glow background */}
 <div className="absolute -top-24 -right-24 w-72 h-72 bg-gym-accent/10 rounded-full blur-3xl pointer-events-none" />
 <div className="absolute -bottom-24 -left-24 w-72 h-72 bg-gym-purple/10 rounded-full blur-3xl pointer-events-none" />

 <div className="flex items-center justify-between mb-6">
 <div className="flex items-center gap-3">
 <div className="p-2.5 rounded-xl bg-gym-accent/20 text-gym-accent">
 <Sparkles className="w-6 h-6" />
 </div>
 <div>
 <h2 className="text-2xl font-black text-white uppercase tracking-wider">
 SELECT ATHLETE GOAL
 </h2>
 <p className="text-xs text-gray-400">
 will generate your weekly training schedule based on this selection
 </p>
 </div>
 </div>
 <button
 onClick={onClose}
 className="p-2 rounded-xl text-gray-400 hover:text-white hover:bg-white/10 transition-colors"
 >
 <X className="w-6 h-6" />
 </button>
 </div>

 <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
 {goals.map((g) => {
 const Icon = g.icon;
 const isSelected = selectedGoal === g.id;
 return (
 <div
 key={g.id}
 onClick={() => setSelectedGoal(g.id)}
 className={`cursor-pointer p-4 rounded-2xl border transition-all text-left flex flex-col justify-between ${
 isSelected
 ? `${g.bg} border-2 ${g.color} shadow-lg scale-[1.02]`
 : 'bg-gym-card/60 border-white/10 hover:border-white/30 text-gray-400'
 }`}
 >
 <div>
 <div className="flex items-center justify-between mb-3">
 <div className={`p-2 rounded-xl ${g.bg} ${g.color}`}>
 <Icon className="w-5 h-5" />
 </div>
 {isSelected && (
 <span className="px-2.5 py-1 rounded-full text-xs font-bold bg-white text-gym-dark flex items-center gap-1">
 <Check className="w-3.5 h-3.5" />
 Selected
 </span>
 )}
 </div>
 <h3 className={`font-extrabold text-base mb-1 ${isSelected ? 'text-white' : 'text-gray-200'}`}>
 {g.title}
 </h3>
 <p className="text-xs text-gray-300 mb-3 leading-relaxed">
 {g.description}
 </p>
 </div>
 <div className="pt-3 border-t border-white/5 text-[11px] font-semibold text-gym-accent">
 Weekly Split: {g.weeklyPlan}
 </div>
 </div>
 );
 })}
 </div>

 <div className="flex flex-col sm:flex-row items-center justify-end gap-3 pt-4 border-t border-white/10">
 <button
 type="button"
 onClick={onClose}
 className="w-full sm:w-auto px-5 py-2.5 rounded-xl border border-white/10 text-gray-300 hover:bg-white/5 text-sm font-semibold transition-colors"
 >
 Cancel
 </button>
 <button
 type="button"
 onClick={handleSave}
 disabled={saving}
 className="w-full sm:w-auto btn-accent px-6 py-3 rounded-xl font-bold flex items-center justify-center gap-2 shadow-xl shadow-gym-accent/20"
 >
 {saving ? (
 <div className="w-5 h-5 border-2 border-gym-dark border-t-transparent rounded-full animate-spin" />
 ) : (
 <>
 <span>Save & Generate Weekly Plan</span>
 <ArrowRight className="w-4 h-4" />
 </>
 )}
 </button>
 </div>
 </div>
 </div>
 );
};

export default GoalSelectionModal;
