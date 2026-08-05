// REPSYNC Gym — Gamified XP, Level, Streak & Badge Engine

export const LEVEL_THRESHOLDS = [
 { level: 1, name: 'Novice Lifter', xpRequired: 0, badgeColor: 'from-blue-500 to-cyan-500' },
 { level: 2, name: 'Dedicated Trainee', xpRequired: 300, badgeColor: 'from-cyan-500 to-teal-500' },
 { level: 3, name: 'Iron Grinder', xpRequired: 800, badgeColor: 'from-emerald-500 to-green-500' },
 { level: 4, name: 'Strength Specialist', xpRequired: 1500, badgeColor: 'from-yellow-500 to-amber-500' },
 { level: 5, name: 'Elite Athlete', xpRequired: 2500, badgeColor: 'from-amber-500 to-orange-500' },
 { level: 6, name: 'Beast Mode', xpRequired: 4000, badgeColor: 'from-orange-500 to-red-500' },
 { level: 7, name: 'Gym Titan', xpRequired: 6000, badgeColor: 'from-purple-500 to-indigo-500' },
 { level: 8, name: 'Olympus Legend', xpRequired: 9000, badgeColor: 'from-fuchsia-500 to-pink-500' },
 { level: 9, name: 'Cyber Biomechanical Master', xpRequired: 13000, badgeColor: 'from-pink-500 to-rose-500' },
 { level: 10, name: 'REPSYNC Immortal', xpRequired: 18000, badgeColor: 'from-gym-accent to-gym-purple' }
];

export const BADGES_CATALOG = [
 {
 id: 'FIRST_SET',
 title: 'First Rep Logged',
 description: 'Completed your first set in REPSYNC Gym.',
 icon: '⚡',
 category: 'STRENGTH'
 },
 {
 id: 'STREAK_3',
 title: '3-Day Fire Streak',
 description: 'Trained for 3 days in a row.',
 icon: '🔥',
 category: 'STREAK'
 },
 {
 id: 'STREAK_7',
 title: '7-Day Iron Athlete',
 description: 'Trained for 7 consecutive days.',
 icon: '🏆',
 category: 'STREAK'
 },
 {
 id: 'BENCH_CLUB',
 title: 'Chest Day Warrior',
 description: 'Completed 5+ chest & shoulder sets.',
 icon: '💥',
 category: 'STRENGTH'
 },
 {
 id: 'CALORIE_MASTER',
 title: 'Nutrition Calibrated',
 description: 'Optimized calorie & macro targets.',
 icon: '🥗',
 category: 'NUTRITION'
 },
 {
 id: 'LIVE_MODE_HERO',
 title: 'Live Gym Titan',
 description: 'Used Full-Screen Live Gym Mode at the rack.',
 icon: '📲',
 category: 'TRAINING'
 }
];

export const getGamificationState = () => {
 try {
 const xp = parseInt(localStorage.getItem('repsync_xp') || '150', 10);
 const streak = parseInt(localStorage.getItem('repsync_streak') || '3', 10);
 const badgesRaw = localStorage.getItem('repsync_badges');
 const badges = badgesRaw ? JSON.parse(badgesRaw) : ['FIRST_SET', 'CALORIE_MASTER'];

 // Determine level
 let currentLevelObj = LEVEL_THRESHOLDS[0];
 let nextLevelObj = LEVEL_THRESHOLDS[1];

 for (let i = LEVEL_THRESHOLDS.length - 1; i >= 0; i--) {
 if (xp >= LEVEL_THRESHOLDS[i].xpRequired) {
 currentLevelObj = LEVEL_THRESHOLDS[i];
 nextLevelObj = LEVEL_THRESHOLDS[i + 1] || LEVEL_THRESHOLDS[i];
 break;
 }
 }

 const currentLevelXP = currentLevelObj.xpRequired;
 const nextLevelXP = nextLevelObj.xpRequired;
 const progressPercent =
 nextLevelXP === currentLevelXP
 ? 100
 : Math.min(100, Math.round(((xp - currentLevelXP) / (nextLevelXP - currentLevelXP)) * 100));

 return {
 xp,
 streak,
 badges,
 level: currentLevelObj.level,
 levelName: currentLevelObj.name,
 badgeColor: currentLevelObj.badgeColor,
 currentLevelXP,
 nextLevelXP,
 progressPercent
 };
 } catch (e) {
 return {
 xp: 150,
 streak: 3,
 badges: ['FIRST_SET'],
 level: 1,
 levelName: 'Novice Lifter',
 badgeColor: 'from-blue-500 to-cyan-500',
 currentLevelXP: 0,
 nextLevelXP: 300,
 progressPercent: 50
 };
 }
};

export const addXP = (amount, newBadgeId = null) => {
 try {
 const state = getGamificationState();
 const nextXP = state.xp + amount;
 localStorage.setItem('repsync_xp', String(nextXP));

 if (newBadgeId && !state.badges.includes(newBadgeId)) {
 const nextBadges = [...state.badges, newBadgeId];
 localStorage.setItem('repsync_badges', JSON.stringify(nextBadges));
 }
 return getGamificationState();
 } catch (e) {
 return getGamificationState();
 }
};
