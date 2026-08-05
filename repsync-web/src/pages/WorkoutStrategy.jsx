import React, { useEffect, useState } from 'react';
import axiosClient from '../api/axiosClient';
import { useAuth } from '../context/AuthContext';
import {
 Compass,
 Target,
 ChevronDown,
 ChevronUp,
 Zap,
 Info,
 CheckCircle2,
 RefreshCw,
 Play,
 Calendar,
 Check,
 Award,
 Sparkles,
 Flame,
 Filter,
 Activity,
 HeartPulse,
 ShieldCheck,
 ArrowRight,
 Dumbbell,
 Clock,
 Layers,
 CheckSquare,
 Square,
 Settings
} from 'lucide-react';
import LiveWorkoutCompanion from '../components/LiveWorkoutCompanion';
import GoalSelectionModal from '../components/GoalSelectionModal';
import ExerciseAnimationGuide from '../components/ExerciseAnimationGuide';
import ProfileOptionsModal from '../components/ProfileOptionsModal';
import LiveGymModeModal from '../components/LiveGymModeModal';

const stripHtml = (html) => {
 if (!html) return '';
 return html.replace(/<[^>]*>?/gm, ' ').replace(/\s+/g, ' ').trim();
};

// Explicit Monday - Sunday weekly schedule with unmistakable guidance on what day to workout what muscle
const WEEKLY_SPLIT_TEMPLATES = {
 STRENGTH: [
 { day: 1, dayName: 'Monday', name: 'Monday (Day 1) — Complete Push Matrix', focus: 'Chest, Shoulders & Triceps (All Muscle Heads)', isRest: false, targetGroups: ['CHEST', 'SHOULDERS', 'ARMS'], summary: 'Complete upper body push workout targeting Upper/Mid/Lower Chest, Front/Side Delts, and Long/Lateral Tricep heads.', flagshipNames: 'Flat Bench, Incline DB Press, OHP, Lateral Raise, Skullcrushers, Cable Fly' },
 { day: 2, dayName: 'Tuesday', name: 'Tuesday (Day 2) — Active Recovery', focus: 'Core & Mobility', isRest: true, note: '30 mins light cardio & stretching to speed up CNS recovery.', summary: 'No heavy lifting. Focus on core stability and joint mobility.', flagshipNames: 'Hanging Leg Raises & Plank' },
 { day: 3, dayName: 'Wednesday', name: 'Wednesday (Day 3) — Complete Pull Matrix', focus: 'Back, Rear Delts & Biceps (All Muscle Heads)', isRest: false, targetGroups: ['BACK', 'ARMS', 'SHOULDERS'], summary: 'Complete posterior chain workout targeting Upper Lats, Mid-Back Rhomboids, Rear Deltoids, and Bicep Long/Short heads.', flagshipNames: 'Barbell Deadlift, Pull-Ups, Barbell Row, Face Pulls, Incline Curl, Hammer Curl' },
 { day: 4, dayName: 'Thursday', name: 'Thursday (Day 4) — Full Rest', focus: 'Rest & CNS Recovery', isRest: true, note: '8 hours sleep, hydration, and high protein intake.', summary: 'Complete rest day to allow central nervous system recovery.', flagshipNames: 'Rest • Hydration & Nutrition' },
 { day: 5, dayName: 'Friday', name: 'Friday (Day 5) — Complete Lower Body Matrix', focus: 'Quads, Hamstrings, Glutes & Calves', isRest: false, targetGroups: ['LEGS', 'CORE'], summary: 'Complete leg workout targeting Quad Sweep, Hamstring Hip Hinge, Gluteus Maximus, and Calves.', flagshipNames: 'Back Squat (ATG), RDL, Hip Thrust, Leg Extension, Standing Calf Raise' },
 { day: 6, dayName: 'Saturday', name: 'Saturday (Day 6) — Accessories & Weak Points', focus: 'Shoulder Cap, Arm Sculpt & Core', isRest: false, targetGroups: ['SHOULDERS', 'ARMS', 'CORE'], summary: 'Hypertrophy accessory work for shoulder cap width, arm supersets, and lower abs.', flagshipNames: 'OHP, Lateral Raises, Curls, Tricep Pushdown & Hanging Leg Raises' },
 { day: 7, dayName: 'Sunday', name: 'Sunday (Day 7) — Complete Rest', focus: 'Rest & Nutrition Preparation', isRest: true, note: 'Pre-week recovery & nutrition preparation.', summary: 'Rest and prepare your meals/hydration for the upcoming week.', flagshipNames: 'Complete Rest Day' }
 ],
 MUSCLE_GAIN: [
 { day: 1, dayName: 'Monday', name: 'Monday (Day 1) — Push Hypertrophy Matrix', focus: 'Chest, Shoulders & Triceps (4x10)', isRest: false, targetGroups: ['CHEST', 'SHOULDERS', 'ARMS'], summary: 'High-volume hypertrophy matrix covering all 6 chest, shoulder, and tricep muscle heads.', flagshipNames: 'Incline DB Press, Flat Bench, Seated OHP, DB Lateral Raise, Skullcrushers, Rope Pushdown' },
 { day: 2, dayName: 'Tuesday', name: 'Tuesday (Day 2) — Pull Hypertrophy Matrix', focus: 'Back, Rear Delts & Biceps (4x10)', isRest: false, targetGroups: ['BACK', 'ARMS', 'SHOULDERS'], summary: 'High-volume back, traps, rear deltoids, and bicep peak hypertrophy workout.', flagshipNames: 'Lat Pulldown, Barbell Row, Deadlift, Face Pulls, Incline DB Curl, Preacher Curl' },
 { day: 3, dayName: 'Wednesday', name: 'Wednesday (Day 3) — Leg & Calf Sculpt', focus: 'Quads, Hamstrings & Glutes (4x10)', isRest: false, targetGroups: ['LEGS', 'CORE'], summary: 'Dedicated lower body hypertrophy session for quads, hamstrings, glutes, and calves.', flagshipNames: 'Barbell Squat, RDL, Hip Thrust, Leg Extension, Seated Calf Raise' },
 { day: 4, dayName: 'Thursday', name: 'Thursday (Day 4) — Active Recovery', focus: 'Mobility & Foam Rolling', isRest: true, note: 'Let muscle fibers repair and rebuild for maximum hypertrophy.', summary: 'Rest day to promote muscle protein synthesis and muscle growth.', flagshipNames: 'Foam Rolling & Core Mobility' },
 { day: 5, dayName: 'Friday', name: 'Friday (Day 5) — Upper Body Pump', focus: 'Chest, Back & Shoulders (4x10)', isRest: false, targetGroups: ['CHEST', 'BACK', 'SHOULDERS'], summary: 'Upper body hypertrophy pump session for width and thickness.', flagshipNames: 'Bench Press, Pull-Ups, Arnold Press, Cable Fly, Seated Row' },
 { day: 6, dayName: 'Saturday', name: 'Saturday (Day 6) — Arms & Core Sculpt', focus: 'Biceps, Triceps & Abs (4x10)', isRest: false, targetGroups: ['ARMS', 'CORE'], summary: 'Arm isolation and core definition workout.', flagshipNames: 'EZ-Bar Curl, Skullcrushers, Hammer Curl, Cable Crunch, Leg Raises' },
 { day: 7, dayName: 'Sunday', name: 'Sunday (Day 7) — Rest & Growth Day', focus: 'Rest Day', isRest: true, note: 'Full systemic recovery and protein synthesis.', summary: 'Rest day for muscle tissue repair and growth.', flagshipNames: 'Complete Rest Day' }
 ],
 FAT_LOSS: [
 { day: 1, dayName: 'Monday', name: 'Monday (Day 1) — Upper Body Shred Circuit', focus: 'Chest, Back & HIIT Cardio (3x15)', isRest: false, targetGroups: ['CHEST', 'BACK', 'CARDIO', 'SHOULDERS'], summary: 'High-tempo metabolic circuit targeting chest, back, shoulders, and cardiovascular burn.', flagshipNames: 'Push-Up Circuit, Lat Pulldown, OHP, Cable Fly, HIIT Sprint' },
 { day: 2, dayName: 'Tuesday', name: 'Tuesday (Day 2) — Lower Body Shred & Core', focus: 'Legs, Glutes & Abs Circuit (3x15)', isRest: false, targetGroups: ['LEGS', 'CORE'], summary: 'High-calorie burn leg circuit and abdominal conditioning.', flagshipNames: 'Goblet Squat, Walking Lunge, Hip Thrust, Mountain Climbers, Plank' },
 { day: 3, dayName: 'Wednesday', name: 'Wednesday (Day 3) — Aerobic Fat Burn', focus: 'Cardio & Endurance (30-45 mins)', isRest: false, targetGroups: ['CARDIO', 'LEGS'], summary: 'Steady-state aerobic fat burning and stamina workout.', flagshipNames: 'Treadmill Incline Run / Rowing Ergometer' },
 { day: 4, dayName: 'Thursday', name: 'Thursday (Day 4) — Active Walk & Recovery', focus: '10,000 Steps Target', isRest: true, note: 'Maintain NEAT (Non-Exercise Activity Thermogenesis) to burn fat.', summary: 'Active recovery day. Aim for 10,000 steps and light stretching.', flagshipNames: '10,000 Steps Walking Goal' },
 { day: 5, dayName: 'Friday', name: 'Friday (Day 5) — Full Body Metabolic Circuit', focus: 'Chest, Back, Legs & Shoulders (3x15)', isRest: false, targetGroups: ['CHEST', 'BACK', 'LEGS', 'SHOULDERS'], summary: 'Full-body metabolic conditioning circuit for maximum calorie burn.', flagshipNames: 'Dumbbell Circuit, Squats, Overhead Press, Lat Pulldown' },
 { day: 6, dayName: 'Saturday', name: 'Saturday (Day 6) — Cardio & Core Burn', focus: 'HIIT Cardio & Abs', isRest: false, targetGroups: ['CARDIO', 'CORE', 'ARMS'], summary: 'High-intensity interval cardio and abdominal definition.', flagshipNames: 'HIIT Sprint Intervals, Plank, Cable Crunch, Arm Superset' },
 { day: 7, dayName: 'Sunday', name: 'Sunday (Day 7) — Complete Reset', focus: 'Rest Day & Hydration', isRest: true, note: 'Rehydrate and reset your metabolic hormones.', summary: 'Rest day to recover joints and replenish electrolytes.', flagshipNames: 'Complete Rest Day' }
 ],
 ENDURANCE: [
 { day: 1, dayName: 'Monday', name: 'Monday (Day 1) — Aerobic Stamina', focus: 'Long-Duration Cardio & Legs (3x20)', isRest: false, targetGroups: ['CARDIO', 'LEGS'], summary: 'Aerobic capacity and leg endurance training.', flagshipNames: 'Long-Duration Tempo Run & Lunge Circuit' },
 { day: 2, dayName: 'Tuesday', name: 'Tuesday (Day 2) — Upper Body Stamina', focus: 'High-Rep Muscular Endurance', isRest: false, targetGroups: ['CHEST', 'BACK', 'ARMS', 'SHOULDERS'], summary: 'High-rep muscular endurance for upper body.', flagshipNames: 'High-Rep Bench Press, Seated Row, OHP, Curls' },
 { day: 3, dayName: 'Wednesday', name: 'Wednesday (Day 3) — VO2 Max Intervals', focus: 'HIIT & Cardio Intervals', isRest: false, targetGroups: ['CARDIO', 'SHOULDERS'], summary: 'High-intensity interval training to increase VO2 max.', flagshipNames: 'HIIT Rowing & Shoulder Press Endurance' },
 { day: 4, dayName: 'Thursday', name: 'Thursday (Day 4) — Mobility & Yoga', focus: 'Active Recovery', isRest: true, note: 'Joint mobility and myofascial release for endurance athletes.', summary: 'Active recovery and stretching.', flagshipNames: 'Yoga & Joint Mobility' },
 { day: 5, dayName: 'Friday', name: 'Friday (Day 5) — Lower Body Endurance', focus: 'Leg Stamina & Core (3x20)', isRest: false, targetGroups: ['LEGS', 'CORE'], summary: 'Muscular endurance for quads, glutes, hamstrings, and abs.', flagshipNames: 'Bodyweight Squat Endurance, RDL, Plank' },
 { day: 6, dayName: 'Saturday', name: 'Saturday (Day 6) — Continuous Circuit', focus: 'Full Body Stamina Challenge', isRest: false, targetGroups: ['CHEST', 'BACK', 'LEGS', 'CARDIO'], summary: 'Continuous circuit challenge for total body endurance.', flagshipNames: 'Full Body Endurance Circuit' },
 { day: 7, dayName: 'Sunday', name: 'Sunday (Day 7) — Complete Rest', focus: 'Rest & Recovery', isRest: true, note: 'Hydration and electrolyte replenishment.', summary: 'Rest day for cardiovascular recovery.', flagshipNames: 'Complete Rest Day' }
 ]
};

// COMPREHENSIVE ANATOMICAL MATRIX: Covers EVERY specific sub-muscle variation for each workout day
const DAILY_ANATOMICAL_WORKOUT_MATRIX = {
 1: [
 {
 id: 101,
 name: 'Barbell Flat Bench Press',
 muscleGroup: 'CHEST',
 subMuscleTarget: 'Sternocostal Head — Overall Chest Thickness & Mid Pecs',
 exerciseType: 'STRENGTH',
 equipment: 'Barbell & Flat Bench',
 sets: 4,
 reps: 8,
 defaultWeight: 65,
 restSeconds: 90,
 rpe: 8,
 targetRegion: 'Sternocostal Pectoralis Major & Anterior Deltoid',
 machineSetup: 'Adjust flat bench. Lie with eyes under barbell. Grip slightly wider than shoulder width.',
 formGuide: 'Lower bar under control to mid-chest (3s eccentric). Pause 1 second at bottom without bouncing. Press explosively to lockout.'
 },
 {
 id: 102,
 name: 'Incline Dumbbell Press',
 muscleGroup: 'CHEST',
 subMuscleTarget: 'Clavicular Head — Upper Chest Shelf & Front Collarbone',
 exerciseType: 'STRENGTH',
 equipment: 'Dumbbells & 30° Incline Bench',
 sets: 4,
 reps: 10,
 defaultWeight: 26,
 restSeconds: 75,
 rpe: 8,
 targetRegion: 'Clavicular Pectoralis Major (Upper Pecs)',
 machineSetup: 'Set adjustable bench to 30-45 degrees angle. Keep feet planted firmly on the floor.',
 formGuide: 'Lower dumbbells deep until stretch across upper collarbone. Press upward in an arc, squeezing pecs at the top.'
 },
 {
 id: 103,
 name: 'Cable Pec Fly / Chest Dips',
 muscleGroup: 'CHEST',
 subMuscleTarget: 'Inner Chest Separation & Lower Pec Stretch',
 exerciseType: 'STRENGTH',
 equipment: 'Cable Machine / Dip Station',
 sets: 3,
 reps: 12,
 defaultWeight: 20,
 restSeconds: 60,
 rpe: 8,
 targetRegion: 'Costal Pectoralis & Sternal Border',
 machineSetup: 'Set pulleys to shoulder height or grasp parallel dip bars with slight forward torso tilt.',
 formGuide: 'Maintain slight elbow bend. Sweep arms together in a hugging motion and hold peak squeeze for 2 seconds.'
 },
 {
 id: 104,
 name: 'Standing Barbell Military Press (OHP)',
 muscleGroup: 'SHOULDERS',
 subMuscleTarget: 'Anterior Deltoid — Overhead Push Power & Front Shoulder',
 exerciseType: 'STRENGTH',
 equipment: 'Barbell & Rack',
 sets: 4,
 reps: 6,
 defaultWeight: 45,
 restSeconds: 90,
 rpe: 8,
 targetRegion: 'Anterior Deltoid & Upper Trapezius',
 machineSetup: 'Hold barbell at collarbone height, elbows tucked directly under wrists.',
 formGuide: 'Press bar overhead in a straight vertical path, moving head back slightly then locking out overhead.'
 },
 {
 id: 105,
 name: 'Dumbbell Lateral Raise',
 muscleGroup: 'SHOULDERS',
 subMuscleTarget: 'Lateral Deltoid — Shoulder Cap Width & 3D Roundness',
 exerciseType: 'STRENGTH',
 equipment: 'Dumbbells',
 sets: 4,
 reps: 15,
 defaultWeight: 12,
 restSeconds: 60,
 rpe: 8,
 targetRegion: 'Lateral / Acromial Deltoid Head',
 machineSetup: 'Stand upright with dumbbells at sides, slight bend in elbows.',
 formGuide: 'Raise dumbbells outward until arms are parallel to floor. Lead slightly with elbows and pause at top.'
 },
 {
 id: 106,
 name: 'EZ-Bar Skullcrushers / Overhead Extension',
 muscleGroup: 'ARMS',
 subMuscleTarget: 'Triceps Long Head — Arm Bulk & Posterior Upper Arm Mass',
 exerciseType: 'STRENGTH',
 equipment: 'EZ-Bar & Flat Bench',
 sets: 4,
 reps: 10,
 defaultWeight: 32,
 restSeconds: 75,
 rpe: 8,
 targetRegion: 'Triceps Brachii Long Head',
 machineSetup: 'Lie flat holding EZ-bar with shoulder-width overhand grip above forehead.',
 formGuide: 'Keep elbows pointing up and stationary. Lower bar toward top of forehead, then extend elbows to lockout.'
 },
 {
 id: 107,
 name: 'Rope Tricep Cable Pushdown',
 muscleGroup: 'ARMS',
 subMuscleTarget: 'Triceps Lateral & Medial Heads — Horseshoe Definition',
 exerciseType: 'STRENGTH',
 equipment: 'Cable Pulley & Rope Attachment',
 sets: 3,
 reps: 15,
 defaultWeight: 25,
 restSeconds: 60,
 rpe: 8,
 targetRegion: 'Triceps Lateral Head (Outer Horseshoe)',
 machineSetup: 'Set high cable pulley with rope attachment. Keep upper arms pinned against ribs.',
 formGuide: 'Push rope down until arms lock out, spreading the rope ends outward at the bottom for peak squeeze.'
 }
 ],
 3: [
 {
 id: 301,
 name: 'Weighted Wide-Grip Pull-Ups / Lat Pulldown',
 muscleGroup: 'BACK',
 subMuscleTarget: 'Latissimus Dorsi — V-Taper Back Width & Upper Lats',
 exerciseType: 'STRENGTH',
 equipment: 'Pull-up Bar / Lat Pulldown Machine',
 sets: 4,
 reps: 8,
 defaultWeight: 60,
 restSeconds: 90,
 rpe: 8,
 targetRegion: 'Upper & Lateral Latissimus Dorsi',
 machineSetup: 'Grip bar wider than shoulder width with overhand grip.',
 formGuide: 'Drive elbows down and back toward hip pockets. Lift chest to bar and hold peak contraction.'
 },
 {
 id: 302,
 name: 'Barbell Bent-Over Row',
 muscleGroup: 'BACK',
 subMuscleTarget: 'Rhomboids & Mid-Back — Back Thickness & Upper Back Density',
 exerciseType: 'STRENGTH',
 equipment: 'Barbell',
 sets: 4,
 reps: 8,
 defaultWeight: 65,
 restSeconds: 90,
 rpe: 8,
 targetRegion: 'Rhomboids, Middle Trapezius & Lats',
 machineSetup: 'Hinge hips back to 45 degree torso angle. Grip barbell shoulder-width apart.',
 formGuide: 'Pull barbell into belly button while squeezing shoulder blades together. Do not use torso momentum.'
 },
 {
 id: 303,
 name: 'Barbell Deadlift',
 muscleGroup: 'BACK',
 subMuscleTarget: 'Erector Spinae & Posterior Chain — Spinal Erectors & Core',
 exerciseType: 'STRENGTH',
 equipment: 'Barbell & Olympic Plates',
 sets: 4,
 reps: 5,
 defaultWeight: 100,
 restSeconds: 120,
 rpe: 9,
 targetRegion: 'Erector Spinae, Glutes & Hamstrings',
 machineSetup: 'Stand with mid-foot under barbell. Hip-width stance, arms outside knees.',
 formGuide: 'Brace core, keep back flat, push floor away through heels and drive hips forward to standing lockout.'
 },
 {
 id: 304,
 name: 'Rope Face Pulls / Rear Delt Fly',
 muscleGroup: 'SHOULDERS',
 subMuscleTarget: 'Posterior Deltoid & External Rotators — Rear Shoulder Health',
 exerciseType: 'STRENGTH',
 equipment: 'Cable Pulley & Rope',
 sets: 4,
 reps: 15,
 defaultWeight: 22,
 restSeconds: 60,
 rpe: 8,
 targetRegion: 'Posterior Deltoid & Infraspinatus',
 machineSetup: 'Set pulley at eye level. Grasp rope ends with overhand thumbs-up grip.',
 formGuide: 'Pull rope toward nose while rotating knuckles backward. Squeeze rear delts for 2 seconds.'
 },
 {
 id: 305,
 name: 'Incline Dumbbell Bicep Curl',
 muscleGroup: 'ARMS',
 subMuscleTarget: 'Biceps Long Head — Outer Bicep Peak & Stretch',
 exerciseType: 'STRENGTH',
 equipment: 'Dumbbells & 45° Incline Bench',
 sets: 4,
 reps: 10,
 defaultWeight: 16,
 restSeconds: 75,
 rpe: 8,
 targetRegion: 'Biceps Brachii Long Head (Peak)',
 machineSetup: 'Sit back on 45-degree incline bench with arms hanging vertically behind torso.',
 formGuide: 'Keep upper arm stationary. Supinate wrists and curl dumbbells toward shoulders with full stretch at bottom.'
 },
 {
 id: 306,
 name: 'EZ-Bar Preacher Curl / Hammer Curl',
 muscleGroup: 'ARMS',
 subMuscleTarget: 'Biceps Short Head & Brachialis — Inner Arm Bulk & Forearm Width',
 exerciseType: 'STRENGTH',
 equipment: 'EZ-Bar / Dumbbells',
 sets: 3,
 reps: 12,
 defaultWeight: 25,
 restSeconds: 60,
 rpe: 8,
 targetRegion: 'Brachialis & Biceps Short Head',
 machineSetup: 'Rest armpits firmly against preacher pad or stand with neutral hammer grip.',
 formGuide: 'Curl bar/dumbbells upward without lifting elbows off pad. Control eccentric lowering for 3 seconds.'
 }
 ],
 5: [
 {
 id: 501,
 name: 'Barbell Back Squat (ATG)',
 muscleGroup: 'LEGS',
 subMuscleTarget: 'Quadriceps — Overall Leg Mass, Vastus Lateralis & Tear Drop',
 exerciseType: 'STRENGTH',
 equipment: 'Barbell & Squat Rack',
 sets: 4,
 reps: 6,
 defaultWeight: 85,
 restSeconds: 120,
 rpe: 9,
 targetRegion: 'Quadriceps Femoris & Gluteus Maximus',
 machineSetup: 'Set barbell across upper traps in rack. Unrack with braced core and step back.',
 formGuide: 'Squat deep below parallel (ATG). Keep chest proud and knees tracking in line with toes.'
 },
 {
 id: 502,
 name: 'Romanian Deadlift (RDL)',
 muscleGroup: 'LEGS',
 subMuscleTarget: 'Hamstrings — Hip Hinge Stretch & Hamstring Sweep',
 exerciseType: 'STRENGTH',
 equipment: 'Barbell & Plates',
 sets: 4,
 reps: 10,
 defaultWeight: 75,
 restSeconds: 90,
 rpe: 8,
 targetRegion: 'Biceps Femoris & Semitendinosus (Hamstrings)',
 machineSetup: 'Stand holding barbell at hip level with overhand grip, knees slightly unlocked.',
 formGuide: 'Push hips backward while keeping barbell sliding down thighs until deep hamstring stretch. Squeeze glutes up.'
 },
 {
 id: 503,
 name: 'Barbell Hip Thrust',
 muscleGroup: 'LEGS',
 subMuscleTarget: 'Gluteus Maximus — Peak Hip Extension & Glute Roundness',
 exerciseType: 'STRENGTH',
 equipment: 'Barbell, Bench & Hip Pad',
 sets: 4,
 reps: 10,
 defaultWeight: 80,
 restSeconds: 90,
 rpe: 8,
 targetRegion: 'Gluteus Maximus',
 machineSetup: 'Rest upper back against flat bench, barbell across hips with protective pad.',
 formGuide: 'Drive hips upward through heels until torso is parallel to floor. Hold peak glute contraction for 2 seconds.'
 },
 {
 id: 504,
 name: 'Leg Extension / Bulgarian Split Squat',
 muscleGroup: 'LEGS',
 subMuscleTarget: 'Vastus Medialis — Quad Tear Drop & Single-Leg Balance',
 exerciseType: 'STRENGTH',
 equipment: 'Leg Extension Machine / Dumbbells',
 sets: 4,
 reps: 12,
 defaultWeight: 40,
 restSeconds: 60,
 rpe: 8,
 targetRegion: 'Vastus Medialis Obliquus (VMO)',
 machineSetup: 'Adjust shin pad against lower ankles. Sit back firmly against seat.',
 formGuide: 'Extend legs until knees lock out. Hold for 1 second at peak contraction.'
 },
 {
 id: 505,
 name: 'Standing Calf Raise',
 muscleGroup: 'LEGS',
 subMuscleTarget: 'Gastrocnemius & Soleus — Calf Diamond & Lower Leg Density',
 exerciseType: 'STRENGTH',
 equipment: 'Calf Raise Machine / Smith Machine',
 sets: 4,
 reps: 15,
 defaultWeight: 50,
 restSeconds: 60,
 rpe: 8,
 targetRegion: 'Gastrocnemius & Soleus',
 machineSetup: 'Place balls of feet on calf step with heels hanging off edge.',
 formGuide: 'Lower heels deep for 2 seconds full stretch, then rise on toes as high as possible and squeeze.'
 }
 ],
 6: [
 {
 id: 601,
 name: 'Seated Dumbbell Shoulder Press',
 muscleGroup: 'SHOULDERS',
 subMuscleTarget: 'Anterior & Medial Deltoid — Overall Shoulder Cap Thickness',
 exerciseType: 'STRENGTH',
 equipment: 'Dumbbells & Upright Bench',
 sets: 4,
 reps: 10,
 defaultWeight: 22,
 restSeconds: 75,
 rpe: 8,
 targetRegion: 'Anterior & Lateral Deltoid',
 machineSetup: 'Set bench to 90 degrees vertical backrest. Raise dumbbells to shoulder height.',
 formGuide: 'Press dumbbells overhead in an arc until they nearly touch above head.'
 },
 {
 id: 602,
 name: 'Cable Lateral Raise (Single-Arm)',
 muscleGroup: 'SHOULDERS',
 subMuscleTarget: 'Lateral Deltoid — Constant Cable Tension for Shoulder Width',
 exerciseType: 'STRENGTH',
 equipment: 'Low Cable Pulley',
 sets: 4,
 reps: 15,
 defaultWeight: 10,
 restSeconds: 45,
 rpe: 8,
 targetRegion: 'Acromial / Lateral Deltoid',
 machineSetup: 'Stand sideways to low cable pulley holding handle across torso.',
 formGuide: 'Raise arm outward until shoulder height. Control eccentric lowering without letting weight stack touch.'
 },
 {
 id: 603,
 name: 'Barbell Curl & Skullcrusher Superset',
 muscleGroup: 'ARMS',
 subMuscleTarget: 'Bicep Peak & Tricep Horseshoe — Total Upper Arm Mass',
 exerciseType: 'STRENGTH',
 equipment: 'Barbell & EZ-Bar',
 sets: 4,
 reps: 12,
 defaultWeight: 30,
 restSeconds: 60,
 rpe: 8,
 targetRegion: 'Biceps Brachii & Triceps Brachii',
 machineSetup: 'Perform barbell curl immediately followed by EZ-bar skullcrusher with zero rest between moves.',
 formGuide: 'Focus on full pump and antagonist muscle stretch across front and back of arms.'
 },
 {
 id: 604,
 name: 'Hanging Leg Raises',
 muscleGroup: 'CORE',
 subMuscleTarget: 'Lower Rectus Abdominis & Hip Flexors — Lower Ab Definition',
 exerciseType: 'STRENGTH',
 equipment: 'Pull-up Bar',
 sets: 4,
 reps: 15,
 defaultWeight: 0,
 restSeconds: 45,
 rpe: 8,
 targetRegion: 'Lower Rectus Abdominis',
 machineSetup: 'Hang from pull-up bar with overhand grip.',
 formGuide: 'Raise legs smoothly toward chest by tilting pelvis forward. Do not use swinging momentum.'
 },
 {
 id: 605,
 name: 'Weighted Cable Crunch / Plank',
 muscleGroup: 'CORE',
 subMuscleTarget: 'Upper Abs & Transverse Abdominis — Deep Core Stability & Six Pack',
 exerciseType: 'STRENGTH',
 equipment: 'High Cable Pulley & Rope',
 sets: 4,
 reps: 15,
 defaultWeight: 35,
 restSeconds: 45,
 rpe: 8,
 targetRegion: 'Upper Rectus Abdominis & Core',
 machineSetup: 'Kneel below high cable pulley holding rope behind ears.',
 formGuide: 'Crunch torso downward, bringing elbows toward knees while flexing abs hard.'
 }
 ]
};

const WorkoutStrategy = () => {
 const { user } = useAuth();
 const [selectedGoal, setSelectedGoal] = useState(user?.fitnessGoal || 'STRENGTH');
 const [planData, setPlanData] = useState(null);
 const [loading, setLoading] = useState(true);
 const [expandedExercise, setExpandedExercise] = useState(null);
 const [isSessionOpen, setIsSessionOpen] = useState(false);
 const [isGoalModalOpen, setIsGoalModalOpen] = useState(false);
 const [isProfileModalOpen, setIsProfileModalOpen] = useState(false);
 const [selectedDay, setSelectedDay] = useState(1);
 const [selectedMuscleFilter, setSelectedMuscleFilter] = useState('ALL');
 const [liveGymExercise, setLiveGymExercise] = useState(null);

 // Completed exercises tracking for the week
 const [completedExercises, setCompletedExercises] = useState(() => {
 const saved = localStorage.getItem('completed_weekly_exercises');
 return saved ? JSON.parse(saved) : {};
 });

 // Individual Set-by-Set Checkbox state tracking
 // Format: key = `${selectedGoal}_day${selectedDay}_ex${id}_set${setIdx}`, value = boolean
 const [completedSets, setCompletedSets] = useState(() => {
 const saved = localStorage.getItem('completed_individual_sets');
 return saved ? JSON.parse(saved) : {};
 });

 // 1. BMI Calculation & Category Determination
 const userWeight = user?.weightKg || 75;
 const userHeight = user?.heightCm || 175;
 const bmiValue = parseFloat((userWeight / Math.pow(userHeight / 100, 2)).toFixed(1));

 const getBmiCategoryInfo = (bmi) => {
 if (bmi < 18.5) {
 return {
 label: 'Underweight',
 color: 'text-gym-warning border-gym-warning bg-gym-warning/10',
 suggestedGoal: 'MUSCLE_GAIN',
 suggestedGoalName: 'Hypertrophy (Muscle Gain)',
 rationale: 'Prioritizes caloric retention, 4x10 moderate volume, and longer rest periods for tissue synthesis.'
 };
 }
 if (bmi < 25.0) {
 return {
 label: 'Normal Weight',
 color: 'text-gym-success border-gym-success bg-gym-success/10',
 suggestedGoal: 'STRENGTH',
 suggestedGoalName: 'Max Strength (5x5)',
 rationale: 'Optimal physiological state for neuromuscular power progression and heavy 5x5 compound lifts.'
 };
 }
 if (bmi < 30.0) {
 return {
 label: 'Overweight',
 color: 'text-gym-warning border-gym-warning bg-gym-warning/10',
 suggestedGoal: 'FAT_LOSS',
 suggestedGoalName: 'Fat Loss (Metabolic Circuit)',
 rationale: 'Prioritizes high-calorie expenditure circuits (3x15) and cardio conditioning while protecting joints.'
 };
 }
 return {
 label: 'Obese',
 color: 'text-gym-danger border-gym-danger bg-gym-danger/10',
 suggestedGoal: 'FAT_LOSS',
 suggestedGoalName: 'Fat Loss & Conditioning',
 rationale: 'Focuses on low-impact metabolic circuits, continuous calorie burn, and joint-friendly movements.'
 };
 };

 const bmiInfo = getBmiCategoryInfo(bmiValue);

 const goals = [
 { id: 'STRENGTH', label: 'Max Strength', color: 'text-gym-accent border-gym-accent', bg: 'bg-gym-accent/10' },
 { id: 'MUSCLE_GAIN', label: 'Hypertrophy', color: 'text-gym-purple border-gym-purple', bg: 'bg-gym-purple/10' },
 { id: 'FAT_LOSS', label: 'Fat Loss', color: 'text-gym-warning border-gym-warning', bg: 'bg-gym-warning/10' },
 { id: 'ENDURANCE', label: 'Endurance', color: 'text-gym-success border-gym-success', bg: 'bg-gym-success/10' },
 ];

 const muscleFilterOptions = [
 { id: 'ALL', label: 'All Target Muscles' },
 { id: 'CHEST', label: 'Chest' },
 { id: 'BACK', label: 'Back' },
 { id: 'LEGS', label: 'Legs' },
 { id: 'SHOULDERS', label: 'Shoulders' },
 { id: 'ARMS', label: 'Arms' },
 { id: 'CORE', label: 'Core' },
 { id: 'CARDIO', label: 'Cardio' }
 ];

 // Fetch plan from backend, or fall back seamlessly to offline BMI-calibrated generation
 const fetchPlan = async (goal) => {
 setLoading(true);
 try {
 const res = await axiosClient.get(`/workouts/plan/${goal}`);
 setPlanData(res.data);
 } catch (err) {
 console.warn('Backend plan fetch unavailable, using client-side BMI determination engine:', err.message);
 setPlanData({
 goal: goal,
 strategyName: `${goal} Strategy (BMI Calibrated)`,
 description: `Customized 7-day training schedule generated by your BMI (${bmiValue} - ${bmiInfo.label}) and ${goal} target.`,
 anatomicalOverview: `Targets compound muscle groups with optimal sets and rest periods for BMI category ${bmiInfo.label}.`,
 personalizationSummary: `Calibrated for ${userWeight}kg / ${userHeight}cm (${user?.gender || 'MALE'}, BMI ${bmiValue}) • ${bmiInfo.label}`,
 exercises: DAILY_ANATOMICAL_WORKOUT_MATRIX[1] || []
 });
 } finally {
 setLoading(false);
 }
 };

 useEffect(() => {
 if (user?.fitnessGoal) {
 setSelectedGoal(user.fitnessGoal);
 }
 }, [user?.fitnessGoal]);

 useEffect(() => {
 fetchPlan(selectedGoal);
 }, [selectedGoal]);

 const toggleExpand = (id) => {
 setExpandedExercise(expandedExercise === id ? null : id);
 };

 const handleLogExercise = async (ex) => {
 const key = `${selectedGoal}_day${selectedDay}_${ex.id || ex.name}`;
 const newCompleted = { ...completedExercises, [key]: !completedExercises[key] };
 setCompletedExercises(newCompleted);
 localStorage.setItem('completed_weekly_exercises', JSON.stringify(newCompleted));

 if (!completedExercises[key]) {
 try {
 await axiosClient.post('/analytics/prs/check', {
 exerciseId: ex.id || 1,
 exerciseName: ex.name,
 weightKg: ex.defaultWeight || 50,
 reps: ex.reps || 10
 });
 } catch (err) {
 // Silently log
 }
 }
 };

 // Toggle individual set checkbox and automatically check full exercise completion when all sets are done
 const handleToggleSet = (ex, totalSets, setIdx) => {
 const setKey = `${selectedGoal}_day${selectedDay}_ex${ex.id || ex.name}_set${setIdx}`;
 const nextVal = !completedSets[setKey];
 const newSets = { ...completedSets, [setKey]: nextVal };
 setCompletedSets(newSets);
 localStorage.setItem('completed_individual_sets', JSON.stringify(newSets));

 // Check if all sets are now completed
 let allDone = true;
 for (let i = 1; i <= totalSets; i++) {
 const checkKey = `${selectedGoal}_day${selectedDay}_ex${ex.id || ex.name}_set${i}`;
 if (i === setIdx) {
 if (!nextVal) allDone = false;
 } else if (!newSets[checkKey]) {
 allDone = false;
 }
 }

 const exKey = `${selectedGoal}_day${selectedDay}_${ex.id || ex.name}`;
 if (allDone && !completedExercises[exKey]) {
 const newExCompleted = { ...completedExercises, [exKey]: true };
 setCompletedExercises(newExCompleted);
 localStorage.setItem('completed_weekly_exercises', JSON.stringify(newExCompleted));
 } else if (!allDone && completedExercises[exKey]) {
 const newExCompleted = { ...completedExercises, [exKey]: false };
 setCompletedExercises(newExCompleted);
 localStorage.setItem('completed_weekly_exercises', JSON.stringify(newExCompleted));
 }
 };

 // Determine exercises scheduled for selectedDay
 const scheduleTemplate = WEEKLY_SPLIT_TEMPLATES[selectedGoal] || WEEKLY_SPLIT_TEMPLATES.STRENGTH;
 const currentDayConfig = scheduleTemplate.find((d) => d.day === selectedDay) || scheduleTemplate[0];

 // RETRIEVE COMPLETE ANATOMICAL MUSCLE MATRIX FOR SELECTED DAY (ALL SPECIFIC SUB-MUSCLE VARIATIONS)
 const getAnatomicalExercisesForDay = () => {
 if (currentDayConfig.isRest) return [];

 // Pull full anatomical matrix for this day (or fallback to Day 1 / Day 3 / Day 5)
 const rawMatrix = DAILY_ANATOMICAL_WORKOUT_MATRIX[selectedDay] ||
 (selectedDay === 1 ? DAILY_ANATOMICAL_WORKOUT_MATRIX[1] :
 selectedDay === 3 ? DAILY_ANATOMICAL_WORKOUT_MATRIX[3] :
 selectedDay === 5 ? DAILY_ANATOMICAL_WORKOUT_MATRIX[5] :
 DAILY_ANATOMICAL_WORKOUT_MATRIX[6]) ||
 DAILY_ANATOMICAL_WORKOUT_MATRIX[1];

 // Filter by selected muscle filter pill if user clicked one
 if (selectedMuscleFilter === 'ALL') {
 return rawMatrix;
 }
 return rawMatrix.filter((ex) => ex.muscleGroup === selectedMuscleFilter);
 };

 const dailyExercises = getAnatomicalExercisesForDay();

 // Progress calculations
 const totalWeeklyExercises = 24; // Comprehensive weekly target
 const completedCount = Object.keys(completedExercises).filter((k) => k.startsWith(`${selectedGoal}_`) && completedExercises[k]).length;
 const progressPercent = Math.min(100, Math.round((completedCount / totalWeeklyExercises) * 100));

 return (
 <div className="min-h-screen pb-20">
 {/* Header Banner */}
 <div className="relative overflow-hidden bg-gradient-to-b from-gym-card to-gym-dark border-b border-white/10 pt-10 pb-12 px-6">
 <div className="absolute -top-10 -right-10 w-96 h-96 bg-gym-purple/10 rounded-full blur-3xl pointer-events-none" />
 <div className="max-w-7xl mx-auto">
 <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
 <div>
 <span className="text-xs font-bold uppercase tracking-widest text-gym-accent mb-2 block flex items-center gap-2">
 <Compass className="w-4 h-4" /> Strategy Optimizer • Complete Sub-Muscle Variation Matrix
 </span>
 <h1 className="text-4xl md:text-5xl font-black text-white tracking-tight">
 7-Day Weekly <span className="text-gym-accent">Training Plan</span>
 </h1>
 <p className="text-gray-400 mt-2 max-w-2xl text-sm md:text-base">
 Anatomically curated variations targeting <strong className="text-white">every specific sub-muscle head</strong> with exact sets, reps, and real-time set-by-set progress tracking calibrated for <strong className="text-white">BMI Index ({bmiValue} — {bmiInfo.label})</strong>.
 </p>
 </div>

 <div className="flex flex-col sm:flex-row items-center gap-3">
 <button
 onClick={() => setIsProfileModalOpen(true)}
 className="w-full sm:w-auto px-4 py-2.5 rounded-xl bg-gym-accent/20 hover:bg-gym-accent/30 text-gym-accent border border-gym-accent/40 text-xs font-bold transition-colors flex items-center justify-center gap-2 shadow-lg shadow-gym-accent/10"
 >
 <Settings className="w-4 h-4" />
 <span>Edit Profile</span>
 </button>

 <button
 onClick={() => setIsGoalModalOpen(true)}
 className="w-full sm:w-auto px-4 py-2.5 rounded-xl bg-gym-purple/20 hover:bg-gym-purple/30 text-gym-purple border border-gym-purple/40 text-xs font-bold transition-colors flex items-center justify-center gap-2"
 >
 <Sparkles className="w-4 h-4" />
 <span>Change Goal</span>
 </button>

 {/* Goal Switcher Tabs */}
 <div className="flex flex-wrap items-center gap-2 bg-gym-dark/80 p-1.5 rounded-2xl border border-white/10">
 {goals.map((g) => {
 const isSelected = selectedGoal === g.id;
 return (
 <button
 key={g.id}
 onClick={() => setSelectedGoal(g.id)}
 className={`px-4 py-2 rounded-xl text-sm font-bold transition-all duration-300 ${
 isSelected
 ? `${g.bg} ${g.color} border shadow-lg shadow-black/40`
 : 'text-gray-400 hover:text-white hover:bg-white/5'
 }`}
 >
 {g.label}
 </button>
 );
 })}
 </div>
 </div>
 </div>
 </div>
 </div>

 <div className="max-w-7xl mx-auto px-6 mt-8">
 {/* STEP 1 -> STEP 2 -> STEP 3: BMI & GOAL DETERMINATION ENGINE CARD */}
 <div className="glass-card mb-8 bg-gradient-to-r from-gym-card via-gym-dark to-gym-card border-gym-accent/40 overflow-hidden shadow-2xl">
 <div className="flex items-center justify-between pb-4 mb-4 border-b border-white/10">
 <div className="flex items-center gap-3">
 <div className="p-3 rounded-2xl bg-gym-accent/15 text-gym-accent border border-gym-accent/30">
 <Activity className="w-6 h-6" />
 </div>
 <div>
 <span className="text-xs font-extrabold uppercase tracking-widest text-gym-accent">
 Step 1 • Anatomical Determination Engine
 </span>
 <h2 className="text-xl font-black text-white">
 How Your BMI & Goal Shaped Your 7-Day Workout Schedule
 </h2>
 </div>
 </div>
 <span className="hidden sm:inline-block text-xs font-extrabold px-3 py-1.5 rounded-full bg-white/10 text-gray-300 border border-white/10">
 Live Calibration Active
 </span>
 </div>

 <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
 {/* Step 1: Determined BMI */}
 <div className="bg-gym-dark/80 p-4 rounded-2xl border border-white/5 flex flex-col justify-between">
 <div>
 <span className="text-xs font-bold text-gray-400 uppercase tracking-wider block mb-1">
 Step 1 • Your BMI Analysis
 </span>
 <div className="flex items-baseline gap-2 mt-1">
 <span className="text-3xl font-black text-white">{bmiValue}</span>
 <span className={`text-xs px-2.5 py-0.5 rounded-full border font-bold ${bmiInfo.color}`}>
 {bmiInfo.label}
 </span>
 </div>
 <p className="text-xs text-gray-400 mt-2 leading-relaxed">
 Calculated from Weight ({userWeight}kg) & Height ({userHeight}cm).
 </p>
 </div>
 <div className="mt-3 pt-3 border-t border-white/10 flex items-center justify-between">
 <span className="text-[11px] text-gray-300 font-semibold">
 Suggested Goal: <span className="text-gym-accent">{bmiInfo.suggestedGoalName}</span>
 </span>
 <button
 onClick={() => setIsProfileModalOpen(true)}
 className="px-2.5 py-1 rounded-lg bg-gym-accent/20 hover:bg-gym-accent/30 text-gym-accent text-[11px] font-extrabold border border-gym-accent/40 flex items-center gap-1.5 transition-colors"
 >
 <Settings className="w-3.5 h-3.5" />
 <span>Edit Weight/Height</span>
 </button>
 </div>
 </div>

 {/* Step 2: Selected Goal Calibration */}
 <div className="bg-gym-dark/80 p-4 rounded-2xl border border-white/5 flex flex-col justify-between">
 <div>
 <span className="text-xs font-bold text-gray-400 uppercase tracking-wider block mb-1">
 Step 2 • Active Goal Strategy
 </span>
 <div className="flex items-center gap-2 mt-1">
 <span className="text-2xl font-black text-gym-purple">{selectedGoal}</span>
 </div>
 <p className="text-xs text-gray-300 mt-2 leading-relaxed font-medium">
 {bmiInfo.rationale}
 </p>
 </div>
 <div className="mt-3 pt-3 border-t border-white/10 text-[11px] font-bold text-gym-success flex items-center gap-1.5">
 <CheckCircle2 className="w-3.5 h-3.5" />
 <span>BMI & Goal Matrix Synchronized</span>
 </div>
 </div>

 {/* Step 3: 7-Day Plan Synthesis */}
 <div className="bg-gym-dark/80 p-4 rounded-2xl border border-white/5 flex flex-col justify-between">
 <div>
 <span className="text-xs font-bold text-gray-400 uppercase tracking-wider block mb-1">
 Step 3 • Weekly Plan Synthesis
 </span>
 <p className="text-xs text-gray-200 leading-relaxed">
 Based on <strong className="text-gym-accent">BMI {bmiValue} ({bmiInfo.label})</strong> targeting <strong className="text-white">{selectedGoal}</strong>:
 </p>
 <ul className="text-xs text-gray-400 space-y-1 mt-2 pl-2">
 <li className="flex items-center gap-1.5">
 <span className="w-1.5 h-1.5 rounded-full bg-gym-accent shrink-0" />
 <span>Includes 6–7 specific variations per day for every muscle head.</span>
 </li>
 <li className="flex items-center gap-1.5">
 <span className="w-1.5 h-1.5 rounded-full bg-gym-purple shrink-0" />
 <span>Interactive Set-by-Set checkbox tracking for every lift.</span>
 </li>
 </ul>
 </div>
 <div className="mt-3 pt-3 border-t border-white/10 text-[11px] text-gray-400">
 Select Day 1 to Day 7 below to explore your split.
 </div>
 </div>
 </div>
 </div>

 {/* ==================================================================================== */}
 {/* MONDAY TO SUNDAY MASTER TABLE — AT A GLANCE (EXACTLY WHAT DAY WHAT YOU SHOULD WORKOUT) */}
 {/* ==================================================================================== */}
 <div className="glass-card mb-8 border-gym-accent/30 overflow-hidden">
 <div className="flex flex-col md:flex-row md:items-center justify-between gap-3 mb-6 pb-4 border-b border-white/10">
 <div>
 <span className="text-xs font-black uppercase tracking-widest text-gym-accent flex items-center gap-2">
 <Calendar className="w-4 h-4" /> Unmistakable Weekly Schedule Guide
 </span>
 <h2 className="text-2xl font-black text-white mt-1">
 Monday to Sunday — Complete Sub-Muscle Variation Schedule
 </h2>
 <p className="text-sm text-gray-400">
 Each workout day covers the entire anatomical complement of variations for Chest, Shoulders, Triceps, Back, and Legs.
 </p>
 </div>
 <span className="text-xs font-bold px-3 py-1.5 rounded-xl bg-gym-purple/20 text-gym-purple border border-gym-purple/40 self-start md:self-center">
 Strategy: {selectedGoal}
 </span>
 </div>

 <div className="grid grid-cols-1 md:grid-cols-7 gap-3">
 {scheduleTemplate.map((d) => {
 const isSelected = selectedDay === d.day;
 return (
 <div
 key={d.day}
 onClick={() => setSelectedDay(d.day)}
 className={`p-4 rounded-2xl border transition-all cursor-pointer flex flex-col justify-between ${
 isSelected
 ? 'bg-gym-accent/15 border-gym-accent shadow-xl scale-[1.03]'
 : d.isRest
 ? 'bg-gym-dark/60 border-white/5 hover:border-white/20'
 : 'bg-gym-card/80 border-white/10 hover:border-white/30'
 }`}
 >
 <div>
 <div className="flex items-center justify-between mb-2">
 <span className={`text-xs font-black uppercase tracking-wider ${isSelected ? 'text-gym-accent' : 'text-white'}`}>
 {d.dayName}
 </span>
 <span
 className={`text-[10px] px-2 py-0.5 rounded-full font-bold uppercase ${
 d.isRest
 ? 'bg-gym-purple/20 text-gym-purple border border-gym-purple/30'
 : 'bg-gym-accent/20 text-gym-accent border border-gym-accent/30'
 }`}
 >
 {d.isRest ? 'Rest Day' : 'Workout'}
 </span>
 </div>

 <h4 className="text-sm font-black text-white mb-1">
 {d.focus}
 </h4>
 <p className="text-[11px] text-gray-400 leading-snug mb-3">
 {d.summary}
 </p>
 </div>

 <div className="pt-2 border-t border-white/10">
 <span className="text-[10px] font-bold text-gray-300 block mb-0.5">
 {d.isRest ? 'Recovery Action:' : 'Muscle Heads Target:'}
 </span>
 <span className="text-[11px] font-semibold text-gym-accent block line-clamp-2">
 {d.flagshipNames}
 </span>
 </div>
 </div>
 );
 })}
 </div>
 </div>

 {/* ==================================================================================== */}
 {/* DETAILED DAILY INSTRUCTION BOX FOR THE SELECTED DAY */}
 {/* ==================================================================================== */}
 <div className="glass-card mb-8 bg-gradient-to-r from-gym-dark via-gym-card to-gym-dark border-white/15">
 <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
 <div>
 <div className="flex items-center gap-2 mb-1">
 <span className="px-3 py-1 rounded-full text-xs font-black bg-gym-accent text-gym-dark uppercase tracking-wider">
 {currentDayConfig.dayName} (Day {currentDayConfig.day})
 </span>
 <span
 className={`text-xs font-extrabold px-3 py-1 rounded-full border ${
 currentDayConfig.isRest
 ? 'bg-gym-purple/20 text-gym-purple border-gym-purple/40'
 : 'bg-gym-success/20 text-gym-success border-gym-success/40'
 }`}
 >
 {currentDayConfig.isRest ? '🧘 REST & RECOVERY DAY' : '🏋️‍♂️ COMPLETE ANATOMICAL MATRIX WORKOUT'}
 </span>
 </div>
 <h2 className="text-3xl font-black text-white mt-2">
 WHAT YOU SHOULD {currentDayConfig.isRest ? 'DO TODAY' : 'WORKOUT TODAY'}: <span className="text-gym-accent">{currentDayConfig.focus}</span>
 </h2>
 <p className="text-sm text-gray-300 mt-1 max-w-3xl">
 {currentDayConfig.summary}
 </p>
 </div>

 {!currentDayConfig.isRest && (
 <button
 onClick={() => setIsSessionOpen(true)}
 className="px-5 py-3 rounded-xl bg-gradient-to-r from-gym-accent to-gym-purple hover:from-gym-purple hover:to-gym-accent text-gym-dark font-black text-sm transition-all flex items-center justify-center gap-2 shadow-xl shadow-gym-accent/20 shrink-0"
 >
 <Play className="w-4 h-4 fill-current" />
 Start Today's Workout
 </button>
 )}
 </div>
 </div>

 {/* Target Muscle Filter Pills */}
 {!currentDayConfig.isRest && (
 <div className="flex flex-wrap items-center gap-2 mb-8 bg-gym-card/60 p-3 rounded-2xl border border-white/5">
 <span className="text-xs font-bold text-gray-400 uppercase tracking-wider flex items-center gap-1.5 mr-2">
 <Filter className="w-3.5 h-3.5 text-gym-accent" />
 <span>Filter Specific Muscle:</span>
 </span>
 {muscleFilterOptions.map((mf) => {
 const isSelected = selectedMuscleFilter === mf.id;
 return (
 <button
 key={mf.id}
 onClick={() => setSelectedMuscleFilter(mf.id)}
 className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all ${
 isSelected
 ? 'bg-gym-accent text-gym-dark shadow-md scale-105'
 : 'bg-white/5 text-gray-400 hover:bg-white/10 hover:text-white'
 }`}
 >
 {mf.label}
 </button>
 );
 })}
 </div>
 )}

 {loading ? (
 <div className="flex flex-col items-center justify-center py-20 text-gray-400 gap-3">
 <RefreshCw className="w-8 h-8 animate-spin text-gym-accent" />
 <p className="text-sm font-medium">Calibrating anatomical strategy & weekly schedule...</p>
 </div>
 ) : (
 <>
 {/* Exercise List for the selected Day */}
 <div className="flex items-center justify-between mb-6">
 <div>
 <h3 className="text-2xl font-black text-white flex items-center gap-2">
 <span>{currentDayConfig.isRest ? 'Recovery & Mobility Guide' : `${currentDayConfig.dayName} — Complete Anatomical Sub-Muscle Variations`}</span>
 </h3>
 <p className="text-sm text-gray-400">
 {currentDayConfig.isRest
 ? 'Rest days are critical for protein synthesis and neurological recovery.'
 : 'Each exercise targets a specific anatomical head. Use the Set-by-Set checkboxes below to track your reps & progress in real time!'}
 </p>
 </div>
 {!currentDayConfig.isRest && (
 <span className="text-xs font-bold text-gym-accent bg-gym-accent/10 px-3 py-1.5 rounded-full border border-gym-accent/30">
 {dailyExercises.length} Specialized Variations
 </span>
 )}
 </div>

 {currentDayConfig.isRest ? (
 <div className="glass-card text-center py-16 bg-gym-card/60 border-white/10">
 <div className="w-16 h-16 rounded-full bg-gym-success/10 text-gym-success flex items-center justify-center mx-auto mb-4 border border-gym-success/30">
 <CheckCircle2 className="w-8 h-8" />
 </div>
 <h4 className="text-2xl font-black text-white mb-2">{currentDayConfig.dayName} — Active Recovery & Restoration Day</h4>
 <p className="text-gray-300 max-w-xl mx-auto text-sm leading-relaxed mb-6 font-medium">
 {currentDayConfig.note} Take a 30-minute light walk, perform foam rolling, and ensure optimal hydration and sleep so your central nervous system is fresh for your next workout day.
 </p>
 </div>
 ) : dailyExercises.length > 0 ? (
 <div className="space-y-6">
 {dailyExercises.map((ex, idx) => {
 const isExpanded = expandedExercise === (ex.id || idx);
 const key = `${selectedGoal}_day${selectedDay}_${ex.id || ex.name}`;
 const isCompleted = !!completedExercises[key];
 const totalSets = ex.sets || 4;

 // Count how many sets are completed for this exercise
 let finishedSetCount = 0;
 for (let s = 1; s <= totalSets; s++) {
 const sKey = `${selectedGoal}_day${selectedDay}_ex${ex.id || ex.name}_set${s}`;
 if (completedSets[sKey]) finishedSetCount++;
 }

 return (
 <div
 key={ex.id || idx}
 className={`glass-card transition-all duration-300 overflow-hidden ${
 isCompleted ? 'border-gym-success/50 bg-gym-success/5 shadow-lg' : 'hover:border-gym-accent/50'
 }`}
 >
 {/* Top Exercise Summary Row */}
 <div
 className="flex flex-col md:flex-row md:items-center justify-between gap-4 cursor-pointer pb-4 border-b border-white/10"
 onClick={() => toggleExpand(ex.id || idx)}
 >
 <div className="flex items-center gap-4">
 <button
 type="button"
 onClick={(e) => {
 e.stopPropagation();
 handleLogExercise(ex);
 }}
 className={`w-12 h-12 rounded-2xl flex items-center justify-center font-black border transition-all shrink-0 ${
 isCompleted
 ? 'bg-gym-success text-gym-dark border-gym-success shadow-lg'
 : 'bg-gradient-to-br from-gym-accent/20 to-gym-purple/20 text-gym-accent border-white/10 hover:border-gym-accent'
 }`}
 >
 {isCompleted ? <Check className="w-7 h-7 stroke-[3]" /> : idx + 1}
 </button>
 <div>
 <div className="flex items-center gap-2 flex-wrap">
 <h4
 className={`text-xl font-black transition-colors ${
 isCompleted ? 'text-gray-300 line-through' : 'text-white group-hover:text-gym-accent'
 }`}
 >
 {ex.name}
 </h4>
 <span className="text-xs px-2.5 py-0.5 rounded-full bg-white/10 text-gray-300 font-semibold uppercase">
 {ex.muscleGroup}
 </span>
 <span className="text-[11px] px-3 py-1 rounded-full bg-gradient-to-r from-gym-accent/20 to-gym-purple/20 text-gym-accent border border-gym-accent/40 font-extrabold flex items-center gap-1.5">
 <Target className="w-3.5 h-3.5 fill-current" />
 <span>{ex.subMuscleTarget || 'Primary Muscle Target'}</span>
 </span>
 {isCompleted && (
 <span className="text-xs px-3 py-1 rounded-full bg-gym-success/20 text-gym-success font-extrabold flex items-center gap-1">
 <CheckCircle2 className="w-3.5 h-3.5" />
 <span>All {totalSets} Sets Completed & Logged</span>
 </span>
 )}
 </div>
 <p className="text-xs text-gray-400 mt-1.5">
 Equipment: <span className="text-gray-200 font-bold">{ex.equipment || 'Standard'}</span> • Type: <span className="text-gray-200 font-bold">{ex.exerciseType || 'STRENGTH'}</span>
 </p>
 </div>
 </div>

 {/* Sets / Reps / Rest Metrics */}
 <div className="flex items-center justify-between md:justify-end gap-6 pt-2 md:pt-0">
 <div className="text-center">
 <span className="text-[10px] text-gray-400 uppercase font-bold block">Sets</span>
 <span className="text-xl font-black text-white">{totalSets}</span>
 </div>
 <div className="text-center">
 <span className="text-[10px] text-gray-400 uppercase font-bold block">Reps/Set</span>
 <span className="text-xl font-black text-gym-accent">{ex.reps || 10}</span>
 </div>
 <div className="text-center">
 <span className="text-[10px] text-gray-400 uppercase font-bold block">Weight</span>
 <span className="text-xl font-black text-gym-purple">{ex.defaultWeight || 50}kg</span>
 </div>
 <div className="text-center">
 <span className="text-[10px] text-gray-400 uppercase font-bold block">Rest</span>
 <span className="text-xl font-black text-gray-200">{ex.restSeconds || 90}s</span>
 </div>

 <button className="p-2 rounded-lg bg-white/5 hover:bg-white/10 text-gray-400 hover:text-white transition-colors">
 {isExpanded ? <ChevronUp className="w-5 h-5" /> : <ChevronDown className="w-5 h-5" />}
 </button>
 </div>
 </div>

 {/* ============================================================================== */}
 {/* NEW: INTERACTIVE SET-BY-SET TRACKER & REP CHECKLIST FOR EVERY EXERCISE CARD */}
 {/* ============================================================================== */}
 <div className="pt-4 bg-gym-dark/50 p-4 rounded-2xl border border-white/5 my-3">
 <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-3">
 <div className="flex items-center gap-2">
 <Layers className="w-4 h-4 text-gym-accent" />
 <span className="text-xs font-black uppercase tracking-wider text-white">
 Track Progress • Check Off Each Set ({finishedSetCount} / {totalSets} Sets Done):
 </span>
 </div>
 <div className="w-full sm:w-48 bg-white/5 rounded-full h-2 overflow-hidden border border-white/10">
 <div
 className="bg-gradient-to-r from-gym-accent to-gym-success h-full transition-all duration-300"
 style={{ width: `${Math.round((finishedSetCount / totalSets) * 100)}%` }}
 />
 </div>
 </div>

 <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
 {Array.from({ length: totalSets }, (_, i) => i + 1).map((sIdx) => {
 const setKey = `${selectedGoal}_day${selectedDay}_ex${ex.id || ex.name}_set${sIdx}`;
 const isSetChecked = !!completedSets[setKey];
 return (
 <button
 key={sIdx}
 type="button"
 onClick={(e) => {
 e.stopPropagation();
 handleToggleSet(ex, totalSets, sIdx);
 }}
 className={`p-2.5 rounded-xl border text-left font-bold text-xs transition-all flex items-center justify-between ${
 isSetChecked
 ? 'bg-gym-success/20 border-gym-success text-white shadow-md'
 : 'bg-gym-card/80 border-white/10 text-gray-300 hover:border-gym-accent/50'
 }`}
 >
 <div className="flex flex-col">
 <span className={`text-[10px] uppercase font-extrabold ${isSetChecked ? 'text-gym-success' : 'text-gray-400'}`}>
 Set {sIdx}
 </span>
 <span className="text-xs font-black text-white">
 {ex.reps || 10} Reps @ {ex.defaultWeight || 50}kg
 </span>
 </div>
 <div className="ml-2">
 {isSetChecked ? (
 <CheckSquare className="w-5 h-5 text-gym-success fill-gym-success/20" />
 ) : (
 <Square className="w-5 h-5 text-gray-400" />
 )}
 </div>
 </button>
 );
 })}
 </div>
 </div>

 {/* Launch Live Gym Mode Button (Feature 6) */}
 <div className="mt-4 flex flex-col sm:flex-row sm:items-center justify-between gap-3 pt-3 border-t border-white/10">
 <span className="text-xs text-gray-400 font-semibold">
 Standing at the gym rack? Switch to distraction-free mode:
 </span>
 <button
 type="button"
 onClick={(e) => {
 e.stopPropagation();
 setLiveGymExercise({
 exercise: ex,
 dayName: selectedDayObj?.dayName || 'Today',
 muscleGroup: ex.muscleGroup
 });
 }}
 className="px-4 py-2 rounded-xl bg-gradient-to-r from-gym-accent to-gym-purple text-gym-dark font-black text-xs uppercase tracking-wider flex items-center justify-center gap-2 shadow-lg shadow-gym-accent/20 hover:scale-105 transition-transform"
 >
 <Flame className="w-4 h-4 fill-current" />
 <span>Launch Live Gym Mode (Rack UI)</span>
 </button>
 </div>

 {/* Expandable Biomechanical Guide & Animated Rep Coach */}
 {isExpanded && (
 <div className="mt-4 pt-4 border-t border-white/10 animate-fadeIn">
 {/* Machine Setup and Form Guide Grid */}
 <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
 <div className="bg-gym-dark/60 rounded-xl p-4 border border-white/5">
 <h5 className="text-xs font-bold uppercase tracking-wider text-gym-accent mb-2 flex items-center gap-2">
 <Target className="w-4 h-4" /> Machine & Equipment Setup
 </h5>
 <p className="text-xs text-gray-300 leading-relaxed whitespace-pre-line">
 {stripHtml(ex.machineSetup) || 'Adjust seat height so handles align with chest level. Secure safety stops.'}
 </p>
 </div>

 <div className="bg-gym-dark/60 rounded-xl p-4 border border-white/5">
 <h5 className="text-xs font-bold uppercase tracking-wider text-gym-purple mb-2 flex items-center gap-2">
 <CheckCircle2 className="w-4 h-4" /> Form & Biomechanical Guide
 </h5>
 <p className="text-xs text-gray-300 leading-relaxed whitespace-pre-line">
 {stripHtml(ex.formGuide) || 'Maintain neutral spine, brace core, control eccentric phase for full muscle tension.'}
 </p>
 </div>
 </div>

 {/* Live Interactive Biomechanical SVG Animation & Rep Coach */}
 <ExerciseAnimationGuide
 exerciseName={ex.name}
 muscleGroup={ex.muscleGroup}
 goal={selectedGoal}
 gender={user?.gender || 'MALE'}
 />
 </div>
 )}
 </div>
 );
 })}
 </div>
 ) : (
 <div className="glass-card text-center py-16">
 <p className="text-gray-400 font-medium">No exercises match the selected muscle filter for this day. Try selecting "All Target Muscles".</p>
 </div>
 )}
 </>
 )}
 </div>

 <LiveWorkoutCompanion
 isOpen={isSessionOpen}
 onClose={() => setIsSessionOpen(false)}
 planData={planData}
 userWeight={user?.weightKg || 75}
 />

 <GoalSelectionModal
 isOpen={isGoalModalOpen}
 onClose={() => setIsGoalModalOpen(false)}
 onGoalUpdated={(newGoal) => {
 setSelectedGoal(newGoal);
 }}
 />

 <ProfileOptionsModal
 isOpen={isProfileModalOpen}
 onClose={() => setIsProfileModalOpen(false)}
 />

 <LiveGymModeModal
 isOpen={!!liveGymExercise}
 onClose={() => setLiveGymExercise(null)}
 exercise={liveGymExercise?.exercise}
 dayName={liveGymExercise?.dayName}
 muscleGroup={liveGymExercise?.muscleGroup}
 goal={selectedGoal}
 gender={user?.gender}
 />
 </div>
 );
};

export default WorkoutStrategy;
