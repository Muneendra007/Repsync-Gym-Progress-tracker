import React from 'react';

/**
 * BioDigitalTwin Component
 * A stylized 2D anatomical heatmap representing the user's muscle volume/fatigue.
 * 
 * @param {Object} heatData - Object mapping muscle groups to heat percentage (0-100)
 * e.g., { chest: 80, back: 30, shoulders: 50, arms: 90, core: 20, legs: 100 }
 */
const BioDigitalTwin = ({ heatData = {} }) => {
  // Helper to determine the color and glow based on heat level (0-100)
  const getHeatStyle = (heat) => {
    const value = heat || 0;
    if (value === 0) {
      return {
        fill: '#1e293b', // Slate 800 (Cold/Inactive)
        stroke: '#334155',
        glow: 'none',
        animate: false
      };
    }
    
    if (value < 40) {
      return {
        fill: '#0ea5e9', // Sky Blue (Warming up)
        stroke: '#38bdf8',
        glow: 'drop-shadow(0 0 8px rgba(14, 165, 233, 0.4))',
        animate: false
      };
    }
    
    if (value < 80) {
      return {
        fill: '#00f5d4', // Cyber Cyan (Active)
        stroke: '#34d399',
        glow: 'drop-shadow(0 0 12px rgba(0, 245, 212, 0.6))',
        animate: false
      };
    }
    
    // High heat (80+) -> On Fire
    return {
      fill: '#f97316', // Orange (Hot)
      stroke: '#fb923c',
      glow: 'drop-shadow(0 0 16px rgba(249, 115, 22, 0.8))',
      animate: true
    };
  };

  const getStyle = (muscleGroup) => {
    const style = getHeatStyle(heatData[muscleGroup]);
    return {
      fill: style.fill,
      stroke: style.stroke,
      strokeWidth: '1.5',
      filter: style.glow,
      transition: 'all 0.5s ease-in-out'
    };
  };

  const getAnimClass = (muscleGroup) => {
    return getHeatStyle(heatData[muscleGroup]).animate ? 'animate-pulse' : '';
  };

  return (
    <div className="w-full mx-auto flex flex-col items-center">
      <div className="relative w-full aspect-[1/2] max-h-[350px]">
        {/* Background scanning grid */}
        <div className="absolute inset-0 bg-[linear-gradient(to_right,#80808012_1px,transparent_1px),linear-gradient(to_bottom,#80808012_1px,transparent_1px)] bg-[size:10px_10px] rounded-2xl pointer-events-none" />
        
        {/* The SVG Avatar */}
        <svg viewBox="0 0 200 400" className="w-full h-full drop-shadow-2xl z-10 relative">
          
          {/* Head (Static, doesn't grow) */}
          <circle cx="100" cy="40" r="18" fill="#1e293b" stroke="#475569" strokeWidth="2" />
          <path d="M 88 55 L 112 55 L 105 70 L 95 70 Z" fill="#1e293b" />

          {/* NECK / TRAPS (Back/Shoulders proxy) */}
          <polygon 
            points="88,55 70,80 100,80" 
            style={getStyle('back')} 
            className={getAnimClass('back')} 
          />
          <polygon 
            points="112,55 130,80 100,80" 
            style={getStyle('back')} 
            className={getAnimClass('back')} 
          />

          {/* SHOULDERS (Deltoids) */}
          <polygon 
            points="70,80 50,95 60,115 80,95" 
            style={getStyle('shoulders')} 
            className={getAnimClass('shoulders')} 
          />
          <polygon 
            points="130,80 150,95 140,115 120,95" 
            style={getStyle('shoulders')} 
            className={getAnimClass('shoulders')} 
          />

          {/* CHEST (Pecs) */}
          <polygon 
            points="80,95 100,80 100,120 75,120" 
            style={getStyle('chest')} 
            className={getAnimClass('chest')} 
          />
          <polygon 
            points="120,95 100,80 100,120 125,120" 
            style={getStyle('chest')} 
            className={getAnimClass('chest')} 
          />

          {/* LATS (Back peeking out side) */}
          <polygon 
            points="75,120 60,115 65,150 78,145" 
            style={getStyle('back')} 
            className={getAnimClass('back')} 
          />
          <polygon 
            points="125,120 140,115 135,150 122,145" 
            style={getStyle('back')} 
            className={getAnimClass('back')} 
          />

          {/* ARMS (Biceps/Triceps) */}
          <polygon 
            points="60,115 45,150 55,160 70,125" 
            style={getStyle('arms')} 
            className={getAnimClass('arms')} 
          />
          <polygon 
            points="140,115 155,150 145,160 130,125" 
            style={getStyle('arms')} 
            className={getAnimClass('arms')} 
          />
          {/* Forearms */}
          <polygon 
            points="45,150 35,190 45,195 55,160" 
            style={getStyle('arms')} 
            className={getAnimClass('arms')} 
          />
          <polygon 
            points="155,150 165,190 155,195 145,160" 
            style={getStyle('arms')} 
            className={getAnimClass('arms')} 
          />

          {/* CORE (Abs) */}
          <polygon 
            points="75,120 100,120 100,145 78,145" 
            style={getStyle('core')} 
            className={getAnimClass('core')} 
          />
          <polygon 
            points="125,120 100,120 100,145 122,145" 
            style={getStyle('core')} 
            className={getAnimClass('core')} 
          />
          <polygon 
            points="78,145 100,145 100,170 82,175" 
            style={getStyle('core')} 
            className={getAnimClass('core')} 
          />
          <polygon 
            points="122,145 100,145 100,170 118,175" 
            style={getStyle('core')} 
            className={getAnimClass('core')} 
          />
          <polygon 
            points="82,175 100,170 100,195 85,195" 
            style={getStyle('core')} 
            className={getAnimClass('core')} 
          />
          <polygon 
            points="118,175 100,170 100,195 115,195" 
            style={getStyle('core')} 
            className={getAnimClass('core')} 
          />

          {/* PELVIS (Static) */}
          <polygon 
            points="85,195 100,195 100,215 90,225" 
            fill="#1e293b" stroke="#334155" strokeWidth="1.5" 
          />
          <polygon 
            points="115,195 100,195 100,215 110,225" 
            fill="#1e293b" stroke="#334155" strokeWidth="1.5" 
          />

          {/* LEGS (Quads) */}
          <polygon 
            points="85,195 60,200 65,260 90,260" 
            style={getStyle('legs')} 
            className={getAnimClass('legs')} 
          />
          <polygon 
            points="115,195 140,200 135,260 110,260" 
            style={getStyle('legs')} 
            className={getAnimClass('legs')} 
          />
          
          {/* LEGS (Calves) */}
          <polygon 
            points="65,260 70,320 85,320 90,260" 
            style={getStyle('legs')} 
            className={getAnimClass('legs')} 
          />
          <polygon 
            points="135,260 130,320 115,320 110,260" 
            style={getStyle('legs')} 
            className={getAnimClass('legs')} 
          />

          {/* FEET (Static) */}
          <polygon points="70,320 65,340 85,340 85,320" fill="#1e293b" stroke="#475569" strokeWidth="1.5" />
          <polygon points="130,320 135,340 115,340 115,320" fill="#1e293b" stroke="#475569" strokeWidth="1.5" />

        </svg>

        {/* Scanning laser line animation effect */}
        <div className="absolute top-0 left-0 w-full h-0.5 bg-gym-accent shadow-[0_0_10px_rgba(0,245,212,0.8)] animate-[scan_3s_ease-in-out_infinite]" />
      </div>

      <style jsx>{`
        @keyframes scan {
          0% { top: 0%; opacity: 0; }
          10% { opacity: 1; }
          90% { opacity: 1; }
          100% { top: 100%; opacity: 0; }
        }
      `}</style>
    </div>
  );
};

export default BioDigitalTwin;
