import React, { useState, useEffect } from 'react';
import { Play, Pause, Flame, Zap, CheckCircle2, Activity, RefreshCw, Eye, ShieldAlert } from 'lucide-react';

const ExerciseAnimationGuide = ({ exerciseName, muscleGroup, goal, gender }) => {
  const [isPlaying, setIsPlaying] = useState(true);
  const [tempoPhase, setTempoPhase] = useState(0); // 0 = Eccentric (3s), 1 = Pause (1s), 2 = Concentric (1s)
  const [timer, setTimer] = useState(0);

  useEffect(() => {
    if (!isPlaying) return;
    const interval = setInterval(() => {
      setTimer((prev) => {
        const next = (prev + 1) % 50; // 5 seconds total cycle (10 ticks per sec)
        if (next < 30) {
          setTempoPhase(0); // 0 - 3s Eccentric (Lowering)
        } else if (next < 40) {
          setTempoPhase(1); // 3 - 4s Pause (Isometric hold)
        } else {
          setTempoPhase(2); // 4 - 5s Concentric (Exploding up)
        }
        return next;
      });
    }, 100);
    return () => clearInterval(interval);
  }, [isPlaying]);

  // Determine movement type for animated SVG
  const getMovementType = () => {
    const name = (exerciseName || '').toUpperCase();
    const mg = (muscleGroup || '').toUpperCase();
    if (name.includes('SQUAT') || name.includes('LEG') || name.includes('THRUST') || mg === 'LEGS') return 'SQUAT';
    if (name.includes('DEADLIFT') || name.includes('ROW') || name.includes('PULL') || mg === 'BACK') return 'PULL';
    if (name.includes('PRESS') || name.includes('OHP') || mg === 'SHOULDERS') return 'OVERHEAD';
    if (name.includes('CURL') || name.includes('TRICEP') || mg === 'ARMS') return 'CURL';
    if (name.includes('PLANK') || name.includes('CRUNCH') || mg === 'CORE') return 'CORE';
    return 'PRESS'; // Default Chest / Pressing
  };

  const movementType = getMovementType();

  const getFormCues = () => {
    switch (movementType) {
      case 'SQUAT':
        return [
          { step: '1. Setup & Stance', text: 'Feet shoulder-width apart, toes slightly out (15-30°). Keep chest proud & brace core.' },
          { step: '2. Eccentric Descent (3s)', text: 'Push hips back & bend knees, tracking knees over toes until thighs reach parallel.' },
          { step: '3. Concentric Drive (1s)', text: 'Drive forcefully through mid-foot & heels back to standing without hyperextending.' }
        ];
      case 'PULL':
        return [
          { step: '1. Neutral Spine Setup', text: 'Hinge at hips with a flat back, chest up, and lats engaged before initiating pull.' },
          { step: '2. Eccentric Stretch (3s)', text: 'Lower weight under control until full lat stretch is achieved without rounding lower back.' },
          { step: '3. Concentric Contraction (1s)', text: 'Drive elbows back and squeeze shoulder blades together at peak contraction.' }
        ];
      case 'OVERHEAD':
        return [
          { step: '1. Stable Core Brace', text: 'Plant feet firmly, squeeze glutes & brace abdominals to prevent lumbar arching.' },
          { step: '2. Controlled Descent (3s)', text: 'Lower dumbbells/bar to shoulder level with elbows tucked slightly forward.' },
          { step: '3. Vertical Drive (1s)', text: 'Press overhead in a straight vertical line, locking out over mid-foot.' }
        ];
      case 'CURL':
        return [
          { step: '1. Pin Elbows to Ribs', text: 'Keep elbows fixed at your sides throughout the entire range of motion.' },
          { step: '2. Full Extension (3s)', text: 'Lower weight slowly to complete arm extension for maximum micro-fiber stretch.' },
          { step: '3. Peak Contraction (1s)', text: 'Curl upward and squeeze biceps hard at the top without swinging torso.' }
        ];
      default: // PRESS (Bench / Chest / Pushups)
        return [
          { step: '1. Scapular Retraction', text: 'Pinch shoulder blades together & keep feet firmly planted on the floor.' },
          { step: '2. Controlled Lowering (3s)', text: 'Lower bar smoothly to mid-sternum with elbows tucked at 45–60 degrees.' },
          { step: '3. Explosive Lockout (1s)', text: 'Press upward in a smooth arc toward eye level, squeezing pectorals at the top.' }
        ];
    }
  };

  const formCues = getFormCues();

  // Render high-visibility widescreen animated SVG diagram
  const renderAnimatedDiagram = () => {
    const isLowering = tempoPhase === 0;
    const isPaused = tempoPhase === 1;

    // Movement coordinates depending on movementType
    let barY = 80;
    let arrowDir = '↓';
    let phaseText = 'LOWERING (3S)';
    let glowColor = '#00f5d4'; // Cyan

    if (tempoPhase === 0) {
      barY = movementType === 'SQUAT' ? 140 : movementType === 'OVERHEAD' ? 130 : 120;
      arrowDir = '↓';
      phaseText = 'ECCENTRIC LOWERING (3s)';
      glowColor = '#ff9500'; // Orange
    } else if (tempoPhase === 1) {
      barY = movementType === 'SQUAT' ? 150 : movementType === 'OVERHEAD' ? 140 : 130;
      arrowDir = '⏸️';
      phaseText = 'ISOMETRIC PAUSE (1s)';
      glowColor = '#ff3b30'; // Red
    } else {
      barY = movementType === 'SQUAT' ? 90 : movementType === 'OVERHEAD' ? 70 : 65;
      arrowDir = '↑';
      phaseText = 'CONCENTRIC DRIVE (1s)';
      glowColor = '#00f5d4'; // Cyan
    }

    return (
      <div className="relative w-full h-72 sm:h-80 bg-gradient-to-b from-gym-dark via-[#111622] to-gym-card rounded-2xl border-2 border-gym-accent/50 shadow-2xl shadow-gym-accent/15 overflow-hidden flex flex-col items-center justify-center p-4">
        {/* Widescreen Cyber Grid Background */}
        <div className="absolute inset-0 bg-[linear-gradient(to_right,#80808018_1px,transparent_1px),linear-gradient(to_bottom,#80808018_1px,transparent_1px)] bg-[size:20px_20px] pointer-events-none" />

        {/* Live Top Visual Rep HUD Banner */}
        <div className="absolute top-3 inset-x-3 z-20 flex items-center justify-between gap-2">
          <div
            className={`px-3 py-1.5 rounded-xl font-black text-xs uppercase tracking-wider flex items-center gap-2 border shadow-lg ${tempoPhase === 1
              ? 'bg-red-500/20 text-red-400 border-red-500/50 animate-pulse'
              : tempoPhase === 0
                ? 'bg-amber-500/20 text-amber-400 border-amber-500/50'
                : 'bg-gym-accent/20 text-gym-accent border-gym-accent/50'
              }`}
          >
            <span className="text-base">{arrowDir}</span>
            <span>{phaseText}</span>
          </div>

          <div className="px-3 py-1.5 rounded-xl bg-black/70 border border-white/20 text-xs font-black text-white flex items-center gap-2">
            <span className="text-gray-400 font-semibold">TARGET:</span>
            <span className="text-gym-accent uppercase">{muscleGroup || 'CHEST'}</span>
          </div>
        </div>

        {/* High-Resolution Animated Biomechanical SVG HUD (Widescreen 400x220) */}
        <svg viewBox="0 0 400 220" className="w-full h-56 max-w-md z-10 my-auto transition-all duration-300">
          <defs>
            <filter id="neon-glow" x="-20%" y="-20%" width="140%" height="140%">
              <feGaussianBlur stdDeviation="6" result="blur" />
              <feMerge>
                <feMergeNode in="blur" />
                <feMergeNode in="SourceGraphic" />
              </feMerge>
            </filter>
          </defs>

          {/* Base Platform / Bench / Floor */}
          <rect x="50" y="190" width="300" height="10" rx="5" fill="#334155" stroke="#475569" strokeWidth="2" />

          {/* High-Contrast Silver Athlete Body Framework */}
          <g>
            {/* Athlete Torso & Head */}
            <circle cx="200" cy="80" r="16" fill="#e2e8f0" stroke="#94a3b8" strokeWidth="2" />
            <rect x="180" y="100" width="40" height="55" rx="12" fill="#cbd5e1" stroke="#64748b" strokeWidth="2" />
            {/* Arms / Legs Framework */}
            <line x1="180" y1="110" x2="140" y2="135" stroke="#94a3b8" strokeWidth="8" strokeLinecap="round" />
            <line x1="220" y1="110" x2="260" y2="135" stroke="#94a3b8" strokeWidth="8" strokeLinecap="round" />
          </g>

          {/* Glowing Target Muscle Group Visualization (Intense Neon Pulse) */}
          <circle
            cx="200"
            cy="125"
            r="28"
            fill={glowColor}
            fillOpacity="0.45"
            filter="url(#neon-glow)"
            className="animate-pulse"
          />
          <circle
            cx="200"
            cy="125"
            r="16"
            fill={glowColor}
            fillOpacity="0.8"
            className="animate-ping"
          />

          {/* Animated Barbell / Dumbbell Movement Trajectory */}
          <g
            className="transition-all duration-500 ease-out"
            style={{ transform: `translateY(${barY - 80}px)` }}
          >
            {/* Main Steel Barbell */}
            <line x1="70" y1="80" x2="330" y2="80" stroke="#f8fafc" strokeWidth="7" strokeLinecap="round" />
            {/* Weight Plates Left */}
            <rect x="75" y="62" width="12" height="36" rx="3" fill="#00f5d4" stroke="#0f172a" strokeWidth="2" />
            <rect x="61" y="66" width="10" height="28" rx="2" fill="#38bdf8" stroke="#0f172a" strokeWidth="1.5" />
            {/* Weight Plates Right */}
            <rect x="313" y="62" width="12" height="36" rx="3" fill="#00f5d4" stroke="#0f172a" strokeWidth="2" />
            <rect x="329" y="66" width="10" height="28" rx="2" fill="#38bdf8" stroke="#0f172a" strokeWidth="1.5" />

            {/* Glowing Motion Guidance Arrows */}
            {tempoPhase === 0 && (
              <g fill="#ff9500" filter="url(#neon-glow)">
                <polygon points="200,95 190,85 210,85" />
                <polygon points="120,95 112,85 128,85" />
                <polygon points="280,95 272,85 288,85" />
              </g>
            )}
            {tempoPhase === 2 && (
              <g fill="#00f5d4" filter="url(#neon-glow)">
                <polygon points="200,65 190,75 210,75" />
                <polygon points="120,65 112,75 128,75" />
                <polygon points="280,65 272,75 288,75" />
              </g>
            )}
          </g>
        </svg>

        {/* Live HUD Bottom Status Bar */}
        <div className="absolute bottom-2 inset-x-3 flex items-center justify-between text-[11px] font-extrabold z-20">
          <span className="px-3 py-1 rounded-lg bg-black/80 text-gym-accent border border-gym-accent/40 flex items-center gap-1.5 shadow-md">
            <Activity className="w-3.5 h-3.5 animate-pulse" />
            <span>BIO-ANIMATION ACTIVE</span>
          </span>
          <span className="px-3 py-1 rounded-lg bg-black/80 text-white border border-white/20">
            EXERCISE: {exerciseName || 'BENCH PRESS'}
          </span>
        </div>
      </div>
    );
  };

  const tempoDescriptions = [
    {
      label: '3s Eccentric (Lowering ↓)',
      color: 'text-amber-300 bg-amber-500/15 border-amber-500/40 shadow-lg shadow-amber-500/5',
      note: 'Inhale deeply & lower weight under control to maximize micro-fiber stretch.'
    },
    {
      label: '1s Isometric Stretch (Pause ⏸️)',
      color: 'text-red-300 bg-red-500/15 border-red-500/40 shadow-lg shadow-red-500/5',
      note: 'Hold peak tension at bottom. Do not bounce or use momentum.'
    },
    {
      label: '1s Concentric (Explosive Drive ↑)',
      color: 'text-gym-accent bg-gym-accent/15 border-gym-accent/40 shadow-lg shadow-gym-accent/5',
      note: 'Exhale forcefully & drive weight to lockout with full muscle contraction.'
    }
  ];

  const currentTempo = tempoDescriptions[tempoPhase];

  return (
    <div className="w-full bg-gradient-to-b from-gym-card/90 to-gym-dark/95 border border-gym-accent/40 rounded-2xl p-4 md:p-6 mt-4 shadow-2xl">
      {/* Top Header Badge */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-5 pb-3.5 border-b border-white/10">
        <div className="flex items-center gap-2.5">
          <span className="px-3.5 py-1.5 rounded-xl text-xs font-black bg-gradient-to-r from-gym-accent to-gym-purple text-gym-dark uppercase tracking-wider flex items-center gap-1.5 shadow-lg shadow-gym-accent/20">
            <Flame className="w-4 h-4 fill-current" />
            <span>#1 MOST INTENSE FOR {muscleGroup}</span>
          </span>
          <span className="text-xs text-gray-300 font-bold hidden md:inline">
            • Calibrated for {gender || 'MALE'} ({goal || 'STRENGTH'})
          </span>
        </div>

        <button
          onClick={() => setIsPlaying(!isPlaying)}
          className="self-start sm:self-auto px-3.5 py-1.5 rounded-xl bg-white/10 hover:bg-white/20 text-white text-xs font-bold transition-all flex items-center gap-1.5 border border-white/20 shadow-md"
        >
          {isPlaying ? <Pause className="w-3.5 h-3.5 text-gym-accent" /> : <Play className="w-3.5 h-3.5 text-gym-accent" />}
          <span>{isPlaying ? 'Pause Animation' : 'Play Animation'}</span>
        </button>
      </div>

      {/* Main Grid: Widescreen Animated Diagram on Left, Live Rep Coach & Form Cues on Right */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
        {/* Animated Biomechanical Diagram (7 Columns on large screen) */}
        <div className="lg:col-span-7">
          <div className="text-xs font-extrabold uppercase tracking-wider text-gym-accent mb-2.5 flex items-center gap-1.5">
            <Eye className="w-4 h-4" />
            <span>Interactive Biomechanical Widescreen Motion Coach</span>
          </div>
          {renderAnimatedDiagram()}
        </div>

        {/* Live Pulsing Rep Tempo Coach & Step-by-Step Form Checklist (5 Columns on large screen) */}
        <div className="lg:col-span-5 flex flex-col justify-between space-y-4">
          <div>
            <div className="text-xs font-extrabold uppercase tracking-wider text-white mb-2.5 flex items-center gap-1.5">
              <RefreshCw className="w-4 h-4 text-gym-purple animate-spin" />
              <span>Real-Time Rep Tempo & Breathing Coach</span>
            </div>

            {/* Active Tempo Highlight Box */}
            <div className={`p-4 rounded-xl border transition-all duration-300 ${currentTempo.color}`}>
              <div className="flex items-center justify-between mb-1.5">
                <span className="text-sm font-black uppercase tracking-wide">
                  {currentTempo.label}
                </span>
                <span className="text-xs font-black px-2.5 py-0.5 rounded-md bg-black/50 text-white">
                  Phase {tempoPhase + 1} / 3
                </span>
              </div>
              <p className="text-xs leading-relaxed font-semibold mt-1">
                {currentTempo.note}
              </p>
            </div>
          </div>

          {/* Step-by-Step Biomechanical Execution Cues */}
          <div className="p-4 rounded-xl bg-gym-dark/90 border border-white/10 shadow-lg">
            <span className="text-xs font-black text-gym-accent uppercase tracking-wider block mb-2 flex items-center gap-1.5">
              <CheckCircle2 className="w-4 h-4 text-gym-accent" />
              <span>Biomechanical Form Checklist ({movementType}):</span>
            </span>
            <div className="space-y-2">
              {formCues.map((cue, idx) => (
                <div key={idx} className="flex items-start gap-2 bg-white/5 p-2.5 rounded-lg border border-white/5">
                  <div className="w-2 h-2 rounded-full bg-gym-accent mt-1 shrink-0" />
                  <div>
                    <strong className="text-xs font-bold text-white block">{cue.step}</strong>
                    <span className="text-[11px] text-gray-300 leading-snug">{cue.text}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ExerciseAnimationGuide;
