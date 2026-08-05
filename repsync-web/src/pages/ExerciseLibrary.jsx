import React, { useEffect, useState } from 'react';
import axiosClient from '../api/axiosClient';
import {
 BookOpen,
 Search,
 Target,
 ChevronDown,
 ChevronUp,
 CheckCircle2,
 RefreshCw,
 Calculator
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import StrengthCalculatorModal from '../components/StrengthCalculatorModal';
import ExerciseAnimationGuide from '../components/ExerciseAnimationGuide';

const ExerciseLibrary = () => {
 const { user } = useAuth();
 const [exercises, setExercises] = useState([]);
 const [filtered, setFiltered] = useState([]);
 const [loading, setLoading] = useState(true);
 const [selectedGroup, setSelectedGroup] = useState('ALL');
 const [searchQuery, setSearchQuery] = useState('');
 const [expandedId, setExpandedId] = useState(null);
 const [calcModalOpen, setCalcModalOpen] = useState(false);
 const [calcExercise, setCalcExercise] = useState('Bench Press');

 const muscleGroups = [
 { id: 'ALL', label: 'All Muscles' },
 { id: 'Chest', label: 'Chest' },
 { id: 'Back', label: 'Back' },
 { id: 'Legs', label: 'Legs' },
 { id: 'Shoulders', label: 'Shoulders' },
 { id: 'Arms', label: 'Arms' },
 { id: 'Core', label: 'Core' },
 ];

 useEffect(() => {
 const fetchExercises = async () => {
 setLoading(true);
 try {
 const res = await axiosClient.get('/exercises');
 setExercises(res.data);
 setFiltered(res.data);
 } catch (err) {
 console.error('Failed to load exercises:', err);
 } finally {
 setLoading(false);
 }
 };
 fetchExercises();
 }, []);

 useEffect(() => {
 let result = exercises;
 if (selectedGroup !== 'ALL') {
 result = result.filter((ex) => ex.muscleGroup?.toLowerCase() === selectedGroup.toLowerCase());
 }
 if (searchQuery.trim() !== '') {
 result = result.filter(
 (ex) =>
 ex.name?.toLowerCase().includes(searchQuery.toLowerCase()) ||
 ex.equipment?.toLowerCase().includes(searchQuery.toLowerCase()) ||
 ex.targetRegion?.toLowerCase().includes(searchQuery.toLowerCase())
 );
 }
 setFiltered(result);
 }, [selectedGroup, searchQuery, exercises]);

 const toggleExpand = (id) => {
 setExpandedId(expandedId === id ? null : id);
 };

 return (
 <div className="min-h-screen pb-20">
 {/* Top Banner */}
 <div className="relative overflow-hidden bg-gradient-to-b from-gym-card to-gym-dark border-b border-white/10 pt-10 pb-12 px-6">
 <div className="absolute -top-10 left-1/3 w-96 h-96 bg-gym-accent/10 rounded-full blur-3xl pointer-events-none" />
 <div className="max-w-7xl mx-auto">
 <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
 <div>
 <span className="text-xs font-bold uppercase tracking-widest text-gym-accent mb-2 block flex items-center gap-2">
 <BookOpen className="w-4 h-4" /> Anatomical Guide & Biomechanics
 </span>
 <h1 className="text-4xl md:text-5xl font-black text-white tracking-tight">
 Exercise <span className="text-gym-accent">Library</span>
 </h1>
 <p className="text-gray-400 mt-2 max-w-2xl text-sm md:text-base">
 Explore target regions, machine setup guides, and form execution steps for compound & isolation movements.
 </p>
 </div>

 {/* Search Input */}
 <div className="relative w-full md:w-80">
 <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
 <input
 type="text"
 placeholder="Search by exercise or equipment..."
 value={searchQuery}
 onChange={(e) => setSearchQuery(e.target.value)}
 className="w-full bg-gym-dark/80 border border-white/10 rounded-2xl pl-11 pr-4 py-3 text-sm text-white placeholder-gray-500 focus:outline-none focus:border-gym-accent transition-colors"
 />
 </div>
 </div>

 {/* Muscle Group Filter Pills */}
 <div className="flex flex-wrap items-center gap-2 mt-6">
 {muscleGroups.map((grp) => {
 const isSelected = selectedGroup === grp.id;
 return (
 <button
 key={grp.id}
 onClick={() => setSelectedGroup(grp.id)}
 className={`px-4 py-2 rounded-xl text-xs font-bold uppercase tracking-wider transition-all duration-300 ${
 isSelected
 ? 'bg-gym-accent text-gym-dark font-black shadow-lg shadow-gym-accent/20 scale-105'
 : 'bg-gym-card/60 text-gray-400 border border-white/10 hover:text-white hover:border-white/30'
 }`}
 >
 {grp.label}
 </button>
 );
 })}
 </div>
 </div>
 </div>

 {/* Main Content Grid */}
 <div className="max-w-7xl mx-auto px-6 mt-8">
 <div className="flex items-center justify-between mb-6">
 <span className="text-sm font-bold text-gray-400">
 Showing <span className="text-white font-black">{filtered.length}</span> Exercises
 </span>
 {selectedGroup !== 'ALL' && (
 <span className="text-xs text-gym-accent font-semibold">
 Filtered by: {selectedGroup}
 </span>
 )}
 </div>

 {loading ? (
 <div className="flex flex-col items-center justify-center py-20 text-gray-400 gap-3">
 <RefreshCw className="w-8 h-8 animate-spin text-gym-accent" />
 <p className="text-sm font-medium">Loading anatomical exercise guide database...</p>
 </div>
 ) : filtered.length > 0 ? (
 <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
 {filtered.map((ex) => {
 const isExpanded = expandedId === ex.id;
 return (
 <div
 key={ex.id}
 className="glass-card flex flex-col justify-between transition-all duration-300 hover:border-gym-accent/50 group"
 >
 <div>
 {/* Header Tags */}
 <div className="flex items-center justify-between gap-2 mb-3">
 <span className="text-xs font-bold uppercase px-2.5 py-1 rounded-lg bg-gym-accent/15 text-gym-accent border border-gym-accent/30">
 {ex.muscleGroup}
 </span>
 <span className="text-xs font-semibold px-2.5 py-1 rounded-lg bg-white/5 text-gray-300 border border-white/10">
 {ex.exerciseType || 'COMPOUND'}
 </span>
 </div>

 <h3 className="text-xl font-black text-white group-hover:text-gym-accent transition-colors mb-2">
 {ex.name}
 </h3>

 <p className="text-xs text-gray-400 mb-4">
 Equipment: <span className="text-gray-200 font-semibold">{ex.equipment || 'Standard Weight'}</span>
 </p>

 {/* Target Region Badge */}
 {ex.targetRegion && (
 <div className="p-3 rounded-xl bg-gym-purple/10 border border-gym-purple/20 mb-4">
 <span className="text-[10px] uppercase font-bold text-gym-purple tracking-wider block mb-0.5">
 Anatomical Target Region
 </span>
 <p className="text-xs font-semibold text-gray-200">{ex.targetRegion}</p>
 </div>
 )}

 {/* Expandable Guide Section */}
 {isExpanded && (
 <div className="space-y-5 pt-4 border-t border-white/10 mt-4 animate-fadeIn">
 <ExerciseAnimationGuide
 exerciseName={ex.name}
 muscleGroup={ex.muscleGroup}
 goal={user?.fitnessGoal}
 gender={user?.gender || 'MALE'}
 />

 <div>
 <span className="text-[10px] font-bold uppercase text-gym-accent block mb-1 flex items-center gap-1.5">
 <Target className="w-3.5 h-3.5" /> Machine & Equipment Setup
 </span>
 <p className="text-xs text-gray-300 leading-relaxed bg-gym-dark/60 p-3 rounded-xl border border-white/5">
 {ex.machineSetup || 'Align handles with upper chest. Secure adjustable seat pin.'}
 </p>
 </div>

 <div>
 <span className="text-[10px] font-bold uppercase text-gym-purple block mb-1 flex items-center gap-1.5">
 <CheckCircle2 className="w-3.5 h-3.5" /> Biomechanical Form Execution
 </span>
 <p className="text-xs text-gray-300 leading-relaxed bg-gym-dark/60 p-3 rounded-xl border border-white/5">
 {ex.formGuide || 'Brace core, maintain scapular retraction, drive through full range of motion.'}
 </p>
 </div>
 </div>
 )}
 </div>

 {/* Action Bar */}
 <div className="grid grid-cols-2 gap-2 mt-5">
 <button
 onClick={() => toggleExpand(ex.id)}
 className="py-2.5 rounded-xl bg-white/5 hover:bg-white/10 border border-white/10 text-xs font-bold text-gray-300 hover:text-white flex items-center justify-center gap-1.5 transition-colors"
 >
 <span>{isExpanded ? 'Hide Guide' : 'Form Guide'}</span>
 {isExpanded ? <ChevronUp className="w-3.5 h-3.5" /> : <ChevronDown className="w-3.5 h-3.5" />}
 </button>

 <button
 onClick={() => {
 setCalcExercise(ex.name);
 setCalcModalOpen(true);
 }}
 className="py-2.5 rounded-xl bg-gym-accent/15 hover:bg-gym-accent/25 border border-gym-accent/30 text-xs font-bold text-gym-accent hover:text-white flex items-center justify-center gap-1.5 transition-colors"
 >
 <Calculator className="w-3.5 h-3.5" />
 <span>1RM & Form</span>
 </button>
 </div>
 </div>
 );
 })}
 </div>
 ) : (
 <div className="glass-card text-center py-16">
 <p className="text-gray-400 font-medium">No exercises found matching your search criteria.</p>
 <button
 onClick={() => {
 setSelectedGroup('ALL');
 setSearchQuery('');
 }}
 className="mt-4 px-4 py-2 rounded-xl bg-gym-accent/15 text-gym-accent border border-gym-accent/30 text-xs font-bold"
 >
 Reset Filters
 </button>
 </div>
 )}
 </div>

 <StrengthCalculatorModal
 isOpen={calcModalOpen}
 onClose={() => setCalcModalOpen(false)}
 initialExercise={calcExercise}
 userWeightKg={user?.weightKg || 75}
 />
 </div>
 );
};

export default ExerciseLibrary;
