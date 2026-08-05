import React, { useState, useEffect } from 'react';
import { Award, Flame, Zap, Trophy, ChevronRight, ShieldAlert, Sparkles, Star } from 'lucide-react';
import { getGamificationState, BADGES_CATALOG } from '../utils/gamification';

const GamificationBar = ({ onOpenBadgeModal }) => {
 const [stats, setStats] = useState(getGamificationState());
 const [showAllBadges, setShowAllBadges] = useState(false);

 useEffect(() => {
 const handleStorageChange = () => {
 setStats(getGamificationState());
 };
 window.addEventListener('storage', handleStorageChange);
 return () => window.removeEventListener('storage', handleStorageChange);
 }, []);

 const unlockedBadges = BADGES_CATALOG.filter((b) => stats.badges.includes(b.id));

 return (
 <div className="w-full bg-gradient-to-r from-gym-card via-gym-dark to-gym-card border-2 border-gym-accent/30 rounded-2xl p-4 md:p-6 shadow-2xl relative overflow-hidden">
 {/* Subtle Glowing Cyber Overlay */}
 <div className="absolute -top-12 -right-12 w-48 h-48 bg-gym-accent/10 rounded-full blur-3xl pointer-events-none" />

 <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-6 relative z-10">
 {/* Left: Athlete Level & XP Progress */}
 <div className="flex items-center gap-4">
 <div className={`w-14 h-14 rounded-2xl bg-gradient-to-tr ${stats.badgeColor} flex flex-col items-center justify-center text-gym-dark shadow-lg shadow-gym-accent/20 shrink-0 border border-white/20`}>
 <Trophy className="w-6 h-6 fill-current text-gym-dark" />
 <span className="text-[10px] font-black uppercase mt-0.5">LVL {stats.level}</span>
 </div>

 <div className="flex-1 min-w-[200px]">
 <div className="flex items-center justify-between gap-2 mb-1">
 <span className="text-sm font-black text-white flex items-center gap-1.5">
 <span>{stats.levelName}</span>
 <Sparkles className="w-4 h-4 text-gym-accent" />
 </span>
 <span className="text-xs font-bold text-gym-accent">
 {stats.xp} / {stats.nextLevelXP} XP
 </span>
 </div>

 {/* XP Progress Bar */}
 <div className="w-full h-2.5 bg-white/10 rounded-full overflow-hidden border border-white/10">
 <div
 className={`h-full bg-gradient-to-r ${stats.badgeColor} transition-all duration-500`}
 style={{ width: `${stats.progressPercent}%` }}
 />
 </div>
 <span className="text-[11px] text-gray-400 font-semibold block mt-1">
 {stats.nextLevelXP - stats.xp} XP needed to reach Level {stats.level + 1}
 </span>
 </div>
 </div>

 {/* Center: Live Gym Streak */}
 <div className="flex items-center gap-3 px-4 py-3 rounded-xl bg-amber-500/10 border border-amber-500/30">
 <div className="w-10 h-10 rounded-xl bg-amber-500/20 flex items-center justify-center">
 <Flame className="w-6 h-6 text-amber-400 fill-current animate-bounce" />
 </div>
 <div>
 <span className="text-xs font-bold uppercase text-amber-400 tracking-wider block">
 Active Gym Streak
 </span>
 <span className="text-lg font-black text-white">
 {stats.streak} Days <span className="text-xs text-gray-300 font-semibold">• Keep pushing!</span>
 </span>
 </div>
 </div>

 {/* Right: Unlocked Badges Carousel/Widget */}
 <div className="flex items-center gap-3">
 <div className="flex items-center -space-x-2">
 {unlockedBadges.slice(0, 4).map((badge) => (
 <div
 key={badge.id}
 className="w-10 h-10 rounded-full bg-gym-dark border-2 border-gym-accent flex items-center justify-center text-lg shadow-md cursor-pointer hover:scale-110 transition-transform"
 title={`${badge.title} — ${badge.description}`}
 >
 {badge.icon}
 </div>
 ))}
 </div>

 <button
 onClick={() => setShowAllBadges(!showAllBadges)}
 className="px-3.5 py-2 rounded-xl bg-white/10 hover:bg-white/15 text-xs font-bold text-white transition-colors border border-white/10 flex items-center gap-1"
 >
 <Award className="w-4 h-4 text-gym-accent" />
 <span>{unlockedBadges.length} Badges</span>
 </button>
 </div>
 </div>

 {/* Expanded Badges Showcase */}
 {showAllBadges && (
 <div className="mt-6 pt-5 border-t border-white/10 grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3 animate-fadeIn">
 {BADGES_CATALOG.map((b) => {
 const isUnlocked = stats.badges.includes(b.id);
 return (
 <div
 key={b.id}
 className={`p-3 rounded-xl border text-center transition-all ${
 isUnlocked
 ? 'bg-gym-accent/10 border-gym-accent/40 text-white shadow-lg'
 : 'bg-white/5 border-white/5 text-gray-500 opacity-60'
 }`}
 >
 <div className="text-2xl mb-1">{b.icon}</div>
 <div className="text-xs font-bold truncate">{b.title}</div>
 <div className="text-[10px] text-gray-400 mt-0.5 leading-tight">{b.description}</div>
 </div>
 );
 })}
 </div>
 )}
 </div>
 );
};

export default GamificationBar;
