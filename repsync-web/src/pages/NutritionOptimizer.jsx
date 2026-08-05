import React from 'react';
import { useAuth } from '../context/AuthContext';
import {
 Flame,
 Zap,
 Target,
 CheckCircle2,
 Award,
 HeartPulse,
 Activity,
 Sparkles,
 Droplets,
 Utensils,
 Clock,
 ChevronRight
} from 'lucide-react';

const NutritionOptimizer = () => {
 const { user } = useAuth();

 // Retrieve physiological metrics
 const weightKg = parseFloat(user?.weightKg) || 75;
 const heightCm = parseFloat(user?.heightCm) || 175;
 const goal = (user?.fitnessGoal || 'STRENGTH').toUpperCase();
 const gender = (user?.gender || 'MALE').toUpperCase();

 // Mifflin-St Jeor Equation for BMR
 const bmr = Math.round(10 * weightKg + 6.25 * heightCm - 5 * 25 + (gender === 'FEMALE' ? -161 : 5));
 // Moderate gym training TDEE factor
 const tdee = Math.round(bmr * 1.55);

 // Calculate Goal Caloric Target
 let calorieAdjustment = 300;
 let goalLabel = 'Lean Muscle Surplus (+300 kcal)';
 let goalColor = 'from-gym-accent to-gym-purple';

 if (goal === 'FAT_LOSS' || goal === 'LOSE_WEIGHT') {
 calorieAdjustment = -500;
 goalLabel = 'Caloric Deficit (-500 kcal) for Fat Shredding';
 goalColor = 'from-red-500 to-amber-500';
 } else if (goal === 'STRENGTH' || goal === 'MAX_STRENGTH') {
 calorieAdjustment = 400;
 goalLabel = 'Power Surplus (+400 kcal) for Max 1RM Output';
 goalColor = 'from-blue-500 to-cyan-500';
 } else if (goal === 'HYPERTROPHY') {
 calorieAdjustment = 300;
 goalLabel = 'Anabolic Surplus (+300 kcal) for Muscle Size';
 goalColor = 'from-gym-accent to-gym-purple';
 }

 const targetCalories = Math.max(1200, tdee + calorieAdjustment);

 // Exact Macro Breakdown
 const proteinGrams = Math.round(weightKg * 2.2); // 2.2g per kg
 const fatGrams = Math.round(weightKg * 0.9); // 0.9g per kg
 const proteinCalories = proteinGrams * 4;
 const fatCalories = fatGrams * 9;
 const carbCalories = Math.max(0, targetCalories - proteinCalories - fatCalories);
 const carbGrams = Math.round(carbCalories / 4);

 // Daily Water Intake (Liters)
 const waterLiters = (weightKg * 0.043).toFixed(1);

 // Meal timing recommendations
 const mealTiming = [
 {
 time: '90 Min Pre-Workout',
 title: 'High-Carb Energy Fuel',
 items: ['Oatmeal with sliced berries & honey', '1 scoop Whey Isolate in water', '1 medium banana'],
 macros: '45g Carbs • 25g Protein • 5g Fat'
 },
 {
 time: '30 Min Post-Workout',
 title: 'Anabolic Recovery Window',
 items: ['Grilled Chicken Breast or Lean Steak', 'White/Jasmine Rice or Sweet Potato', 'Steamed Spinach or Broccoli'],
 macros: '55g Carbs • 40g Protein • 10g Fat'
 },
 {
 time: 'Before Sleep (Casein)',
 title: 'Overnight Muscle Repair',
 items: ['Greek Yogurt or Cottage Cheese', 'Handful of Almonds or Walnuts'],
 macros: '10g Carbs • 25g Protein • 15g Fat'
 }
 ];

 const supplements = [
 { name: 'Creatine Monohydrate', dosage: '5g daily', why: 'Boosts ATP energy & increases intra-muscular hydration.' },
 { name: 'Whey Protein Isolate', dosage: '25-50g post-workout', why: 'Rapid absorption leucine trigger for muscle synthesis.' },
 { name: 'Electrolytes & Sodium', dosage: '500mg pre-workout', why: 'Prevents cramping & sustains vascular pump.' },
 { name: 'Omega-3 Fish Oil', dosage: '2000mg daily', why: 'Reduces joint inflammation & supports recovery.' }
 ];

 return (
 <div className="w-full px-4 sm:px-6 py-8 animate-fadeIn">
 {/* Header Badge */}
 <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-8">
 <div>
 <div className="flex items-center gap-2 mb-2">
 <span className="px-3 py-1 rounded-full text-xs font-black bg-gradient-to-r from-gym-accent to-gym-purple text-gym-dark uppercase tracking-wider flex items-center gap-1.5 shadow-lg shadow-gym-accent/20">
 <Sparkles className="w-4 h-4 fill-current" />
 <span>NUTRITION & MACRO ENGINE</span>
 </span>
 <span className="text-xs text-gray-400 font-bold">
 • Calibrated for {weightKg} kg / {heightCm} cm ({goal})
 </span>
 </div>
 <h1 className="text-3xl font-black text-white tracking-tight">
 Calorie, Macro & Protein Optimizer
 </h1>
 <p className="text-sm text-gray-400 mt-1">
 Precision biological fueling recommendations customized to your BMI Index and training strategy.
 </p>
 </div>
 </div>

 {/* Top Cards: TDEE, Caloric Target, Water, and Protein Target */}
 <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
 {/* Daily Caloric Target */}
 <div className="p-5 rounded-2xl bg-gradient-to-br from-gym-card via-gym-dark to-gym-card border-2 border-gym-accent/40 shadow-xl relative overflow-hidden">
 <div className="flex items-center justify-between mb-2">
 <span className="text-xs font-extrabold uppercase text-gym-accent tracking-wider">
 Daily Caloric Target
 </span>
 <Flame className="w-5 h-5 text-gym-accent animate-pulse" />
 </div>
 <div className="text-3xl font-black text-white">{targetCalories} <span className="text-base font-bold text-gray-400">kcal/day</span></div>
 <span className="text-[11px] font-semibold text-gray-300 block mt-2">
 {goalLabel}
 </span>
 </div>

 {/* Daily Protein Intake */}
 <div className="p-5 rounded-2xl bg-gym-card border border-white/10 shadow-xl">
 <div className="flex items-center justify-between mb-2">
 <span className="text-xs font-extrabold uppercase text-gym-purple tracking-wider">
 Daily Protein Target
 </span>
 <Zap className="w-5 h-5 text-gym-purple" />
 </div>
 <div className="text-3xl font-black text-white">{proteinGrams} <span className="text-base font-bold text-gray-400">g/day</span></div>
 <span className="text-[11px] font-semibold text-gray-400 block mt-2">
 2.2g per kg • Essential for fiber repair
 </span>
 </div>

 {/* Carbohydrates Fuel */}
 <div className="p-5 rounded-2xl bg-gym-card border border-white/10 shadow-xl">
 <div className="flex items-center justify-between mb-2">
 <span className="text-xs font-extrabold uppercase text-amber-400 tracking-wider">
 Carbohydrate Fuel
 </span>
 <Activity className="w-5 h-5 text-amber-400" />
 </div>
 <div className="text-3xl font-black text-white">{carbGrams} <span className="text-base font-bold text-gray-400">g/day</span></div>
 <span className="text-[11px] font-semibold text-gray-400 block mt-2">
 Glycogen replenishment & gym power
 </span>
 </div>

 {/* Hydration Target */}
 <div className="p-5 rounded-2xl bg-gym-card border border-white/10 shadow-xl">
 <div className="flex items-center justify-between mb-2">
 <span className="text-xs font-extrabold uppercase text-cyan-400 tracking-wider">
 Water Intake Target
 </span>
 <Droplets className="w-5 h-5 text-cyan-400 animate-bounce" />
 </div>
 <div className="text-3xl font-black text-white">{waterLiters} <span className="text-base font-bold text-gray-400">L / day</span></div>
 <span className="text-[11px] font-semibold text-gray-400 block mt-2">
 Intra-muscular hydration & ATP pump
 </span>
 </div>
 </div>

 {/* Main Content Grid: Macro Progress Bar & Meal Timing */}
 <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 mb-8">
 {/* Left: Visual Macro Ratio & Distribution (7 cols) */}
 <div className="lg:col-span-7 bg-gym-card border border-white/10 rounded-2xl p-6 shadow-xl">
 <h2 className="text-lg font-black text-white mb-4 flex items-center gap-2">
 <Utensils className="w-5 h-5 text-gym-accent" />
 <span>Calibrated Macro Breakdown</span>
 </h2>

 <div className="space-y-6">
 {/* Protein Bar */}
 <div>
 <div className="flex items-center justify-between text-sm font-bold mb-1.5">
 <span className="text-gym-purple uppercase">Protein ({proteinGrams}g)</span>
 <span className="text-white">{proteinCalories} kcal ({Math.round((proteinCalories / targetCalories) * 100)}%)</span>
 </div>
 <div className="w-full h-3 bg-white/5 rounded-full overflow-hidden border border-white/10">
 <div
 className="h-full bg-gradient-to-r from-gym-purple to-pink-500"
 style={{ width: `${Math.round((proteinCalories / targetCalories) * 100)}%` }}
 />
 </div>
 </div>

 {/* Carbs Bar */}
 <div>
 <div className="flex items-center justify-between text-sm font-bold mb-1.5">
 <span className="text-amber-400 uppercase">Carbohydrates ({carbGrams}g)</span>
 <span className="text-white">{carbCalories} kcal ({Math.round((carbCalories / targetCalories) * 100)}%)</span>
 </div>
 <div className="w-full h-3 bg-white/5 rounded-full overflow-hidden border border-white/10">
 <div
 className="h-full bg-gradient-to-r from-amber-400 to-orange-500"
 style={{ width: `${Math.round((carbCalories / targetCalories) * 100)}%` }}
 />
 </div>
 </div>

 {/* Fats Bar */}
 <div>
 <div className="flex items-center justify-between text-sm font-bold mb-1.5">
 <span className="text-cyan-400 uppercase">Healthy Fats ({fatGrams}g)</span>
 <span className="text-white">{fatCalories} kcal ({Math.round((fatCalories / targetCalories) * 100)}%)</span>
 </div>
 <div className="w-full h-3 bg-white/5 rounded-full overflow-hidden border border-white/10">
 <div
 className="h-full bg-gradient-to-r from-cyan-400 to-blue-500"
 style={{ width: `${Math.round((fatCalories / targetCalories) * 100)}%` }}
 />
 </div>
 </div>

 {/* Summary Box */}
 <div className="p-4 rounded-xl bg-gym-dark/80 border border-white/10 text-xs text-gray-300 leading-relaxed">
 <strong className="text-gym-accent block mb-1">Dietary Guidance Note:</strong>
 Consume <span className="text-white font-bold">{proteinGrams}g of protein</span> distributed across 4–5 meals to stimulate continuous muscle protein synthesis (MPS). Pair high-glycemic carbohydrates around your workout window for maximum energy and glycogen replenishment.
 </div>
 </div>
 </div>

 {/* Right: Supplementation Advisor (5 cols) */}
 <div className="lg:col-span-5 bg-gym-card border border-white/10 rounded-2xl p-6 shadow-xl flex flex-col justify-between">
 <div>
 <h2 className="text-lg font-black text-white mb-4 flex items-center gap-2">
 <Award className="w-5 h-5 text-gym-accent" />
 <span>Evidence-Based Supplement Advisor</span>
 </h2>

 <div className="space-y-3">
 {supplements.map((s, idx) => (
 <div key={idx} className="p-3.5 rounded-xl bg-gym-dark/90 border border-white/5 hover:border-gym-accent/30 transition-all">
 <div className="flex items-center justify-between mb-1">
 <span className="text-xs font-black text-white uppercase">{s.name}</span>
 <span className="px-2 py-0.5 rounded bg-gym-accent/20 text-gym-accent font-bold text-[10px]">
 {s.dosage}
 </span>
 </div>
 <p className="text-[11px] text-gray-400 leading-snug">{s.why}</p>
 </div>
 ))}
 </div>
 </div>

 <div className="mt-6 pt-4 border-t border-white/10 text-center">
 <span className="text-xs text-gray-500 font-semibold">
 * All recommendations are calibrated to your biological BMI Index & Training Strategy.
 </span>
 </div>
 </div>
 </div>

 {/* Bottom Section: Pre & Post Workout Fueling Timings */}
 <div className="bg-gym-card border border-white/10 rounded-2xl p-6 shadow-xl">
 <h2 className="text-lg font-black text-white mb-6 flex items-center gap-2">
 <Clock className="w-5 h-5 text-gym-purple" />
 <span>Optimal Nutrient Timing & Meal Examples</span>
 </h2>

 <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
 {mealTiming.map((meal, idx) => (
 <div key={idx} className="p-5 rounded-2xl bg-gym-dark/90 border border-white/10 relative">
 <span className="text-xs font-black text-gym-accent uppercase block mb-1">
 {meal.time}
 </span>
 <h3 className="text-base font-black text-white mb-3">{meal.title}</h3>

 <ul className="space-y-1.5 mb-4">
 {meal.items.map((item, i) => (
 <li key={i} className="text-xs text-gray-300 flex items-center gap-2">
 <div className="w-1.5 h-1.5 rounded-full bg-gym-purple shrink-0" />
 <span>{item}</span>
 </li>
 ))}
 </ul>

 <div className="pt-3 border-t border-white/10">
 <span className="text-[11px] font-bold text-gray-400">{meal.macros}</span>
 </div>
 </div>
 ))}
 </div>
 </div>
 </div>
 );
};

export default NutritionOptimizer;
