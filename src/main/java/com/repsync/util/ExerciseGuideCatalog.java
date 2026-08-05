package com.repsync.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Scientific anatomical and biomechanical reference catalog for all exercises.
 * Maps every exercise name to:
 * 1. Specific Anatomical Target Region (e.g. Upper Chest / Clavicular Head, Tricep Long Head, etc.)
 * 2. Exact Machine and Equipment Setup
 * 3. Detailed Step-by-Step Biomechanical Execution Form Guide
 * 4. Illustration Type for biomechanical graphic rendering
 */
public class ExerciseGuideCatalog {

    public static class GuideInfo {
        public final String targetRegion;
        public final String machineSetup;
        public final String formGuide;
        public final String illustrationType;

        public GuideInfo(String targetRegion, String machineSetup, String formGuide, String illustrationType) {
            this.targetRegion = targetRegion;
            this.machineSetup = machineSetup;
            this.formGuide = formGuide;
            this.illustrationType = illustrationType;
        }
    }

    private static final Map<String, GuideInfo> CATALOG = new HashMap<>();

    static {
        // --- CHEST EXERCISES (Upper Clavicular, Middle Sternal, Lower Costal) ---
        register("Bench Press",
            "Middle Chest (Sternal Head) & Anterior Deltoid",
            "Flat Olympic Bench Press Station + 20kg Barbell + Safety Racks",
            "1. Setup: Lie flat with eyes directly under the bar. Grip bar slightly wider than shoulder width.\n" +
            "2. Descent: Lower barbell under control to mid-chest (nipple line), keeping elbows tucked at 45–60 degrees.\n" +
            "3. Drive: Press bar upward explosively by driving shoulders into bench and extending arms to lockout.\n" +
            "4. Form Tip: Maintain a natural lower-back arch and keep shoulder blades retracted throughout.",
            "BENCH_PRESS");

        register("Incline Dumbbell Press",
            "Upper Chest (Clavicular Head) & Front Shoulder",
            "Adjustable Incline Bench (set backrest to 30°–45° angle) + Pair of Dumbbells",
            "1. Setup: Sit on incline bench with dumbbells resting vertically on thighs. Kick them up to shoulder level.\n" +
            "2. Press: Press dumbbells upward until arms are extended over your upper chest/eyes.\n" +
            "3. Lower: Control dumbbells down until they reach upper chest level, keeping wrists straight.\n" +
            "4. Form Tip: A 30-degree incline maximizes Clavicular Head activation without overloading the front deltoids.",
            "INCLINE_PRESS");

        register("Decline Bench Press",
            "Lower Chest (Costal/Abdominal Head) & Triceps",
            "Decline Bench Station + Barbell + Leg Support Pads",
            "1. Setup: Hook legs securely into decline bench foot pads. Grip barbell slightly wider than shoulders.\n" +
            "2. Lower: Bring barbell down to the lower chest / sternum base.\n" +
            "3. Press: Push upward to full arm extension, squeezing the lower pectorals.\n" +
            "4. Form Tip: Do not bounce the bar off your chest; pause for 0.5s at the bottom.",
            "BENCH_PRESS");

        register("Cable Chest Fly",
            "Inner Chest Stretch (Sternal & Clavicular Junction)",
            "Dual Cable Crossover Machine + Two D-Handle Attachments (set pulleys at shoulder height)",
            "1. Setup: Stand in center of cable tower with one foot forward for stability. Grasp D-handles with slight elbow bend.\n" +
            "2. Fly: Bring handles together in front of chest in a hugging motion until hands meet.\n" +
            "3. Squeeze: Hold peak isometric contraction for 1 second.\n" +
            "4. Stretch: Open arms slowly until you feel a deep pectoral stretch without over-extending shoulders.",
            "CABLE_FLY");

        register("Chest Dips",
            "Lower Chest (Costal Head) & Outer Pec Border",
            "Parallel Dip Bar Station / Assisted Dip Machine",
            "1. Setup: Mount parallel bars with arms locked. Lean torso forward at a 30-degree angle.\n" +
            "2. Descent: Lower body by bending elbows until upper arms are parallel to floor.\n" +
            "3. Drive: Press upward through palms while keeping chin down and chest tilted forward.\n" +
            "4. Form Tip: Leaning forward targets the Lower Chest; staying upright shifts tension to triceps.",
            "DIPS");

        register("Push Ups",
            "Full Pectoral Group & Core Stabilization",
            "Flat Gym Floor / Exercise Mat",
            "1. Setup: Place hands shoulder-width apart on floor. Brace core into a straight plank from head to heels.\n" +
            "2. Descent: Lower chest until it is 1 inch from floor, elbows at 45 degrees.\n" +
            "3. Press: Push through palms to return to starting lockout.\n" +
            "4. Form Tip: Never let hips sag; tighten glutes and abs throughout.",
            "BENCH_PRESS");

        // --- TRICEPS EXERCISES (Long Head, Lateral Head, Medial Head) ---
        register("EZ-Bar Skull Crushers",
            "Tricep Long Head (Overhead Stretch & Arm Mass)",
            "Flat Utility Bench + EZ-Curl Bar (loaded with smaller plates)",
            "1. Setup: Lie on flat bench holding EZ-bar with shoulder-width grip above forehead.\n" +
            "2. Descent: Keeping upper arms stationary and vertical, bend elbows to lower bar toward top of forehead.\n" +
            "3. Extension: Extend elbows to drive bar back up to starting lockout.\n" +
            "4. Form Tip: Allowing elbows to drift slightly backward increases Long Head stretch at the bottom.",
            "SKULL_CRUSHER");

        register("Cable Tricep Pushdown",
            "Tricep Lateral Head (Outer Horseshoe)",
            "High Pulley Cable Tower + V-Bar or Straight Bar Attachment",
            "1. Setup: Stand facing cable tower. Grasp V-bar with overhand grip, pinning elbows securely to your ribs.\n" +
            "2. Pushdown: Extend elbows to press bar down to upper thighs until arms are straight.\n" +
            "3. Lockout: Flex triceps hard at bottom for 1 second.\n" +
            "4. Form Tip: Never let elbows drift forward or sway your torso; movement is purely at elbow joint.",
            "CABLE_PUSHDOWN");

        register("Rope Pushdown",
            "Tricep Medial & Lateral Head (Lockout Separation)",
            "High Pulley Cable Tower + Heavy-Duty Double Rope Attachment",
            "1. Setup: Grasp rope ends with neutral grip, elbows tucked at sides.\n" +
            "2. Extension: Press rope down and pull the two rope ends apart (outward flare) at the very bottom.\n" +
            "3. Squeeze: Squeeze triceps at full lockout.\n" +
            "4. Form Tip: The outward flare at the bottom engages the Medial Head and sharpens horseshoe detail.",
            "CABLE_PUSHDOWN");

        register("Overhead Dumbbell Extension",
            "Tricep Long Head (Maximum Overhead Stretch)",
            "Seated Utility Bench (with low back support) + Single Heavy Dumbbell",
            "1. Setup: Sit upright holding one dumbbell with both hands under the top plate, arms extended overhead.\n" +
            "2. Descent: Bend elbows to lower dumbbell behind your head until forearms are below horizontal.\n" +
            "3. Extension: Press dumbbell back overhead to lockout.\n" +
            "4. Form Tip: Keeping elbows pointing forward rather than flaring sideways maximizes Long Head stretch.",
            "SKULL_CRUSHER");

        register("Tricep Dips",
            "All 3 Tricep Heads (Heavy Compound Lockout)",
            "Parallel Dip Bar Station or Flat Gym Bench",
            "1. Setup: Keep torso vertical and upright (do not lean forward like in chest dips).\n" +
            "2. Lower: Lower body until elbows reach 90 degrees.\n" +
            "3. Press: Drive upward to full tricep extension.\n" +
            "4. Form Tip: Keep elbows pointing straight back.",
            "DIPS");

        // --- BACK EXERCISES (Upper Width, Mid Thickness, Lower Posterior Chain) ---
        register("Deadlift",
            "Lower Back (Erector Spinae), Lats & Hamstrings",
            "Olympic Barbell + Bumper Plates + Lifting Platform",
            "1. Setup: Stand with feet hip-width apart, shins 1 inch from bar. Hinge hips back and grasp bar.\n" +
            "2. Lift: Brace core, pull slack out of bar, and drive floor away with legs while extending hips.\n" +
            "3. Lockout: Stand tall with shoulders neutral and glutes squeezed.\n" +
            "4. Form Tip: Keep bar dragging against shins and thighs; never round lower back.",
            "DEADLIFT");

        register("Barbell Row",
            "Mid-Back (Rhomboids, Middle Traps & Lats Thickness)",
            "Olympic Barbell + Weight Plates",
            "1. Setup: Hinge hips back until torso is at 45 degrees, knees slightly bent, arms hanging straight.\n" +
            "2. Row: Pull barbell to upper abdomen / lower ribcage, driving elbows toward ceiling.\n" +
            "3. Squeeze: Retract shoulder blades together at top of row.\n" +
            "4. Form Tip: Keep torso angle frozen; avoid using lower back momentum to jerk weight.",
            "ROW");

        register("Seated Cable Row",
            "Lower Rhomboids, Lats & Scapular Retraction",
            "Low Cable Pulley Row Station + Close-Grip Double-D Triangle Handle",
            "1. Setup: Sit on row bench with feet on footplates, knees slightly bent, torso upright.\n" +
            "2. Pull: Pull handle into navel/upper waist while keeping chest elevated.\n" +
            "3. Squeeze: Squeeze shoulder blades together for 1 second.\n" +
            "4. Return: Extend arms forward until you feel lat stretch without rounding spine.",
            "ROW");

        register("Pull Ups",
            "Upper Lats & Teres Major (V-Taper Width)",
            "Overhead Pull-Up Bar Station",
            "1. Setup: Grasp bar slightly wider than shoulders with overhand grip.\n" +
            "2. Pull: Drive elbows down toward floor to lift chest up to the bar.\n" +
            "3. Lower: Control descent back to full arm hang.\n" +
            "4. Form Tip: Initiate movement by pulling shoulder blades down before bending elbows.",
            "PULLDOWN");

        register("Lat Pulldown",
            "Lateral Latissimus Dorsi (Outer Wing Width)",
            "Lat Pulldown Machine + Wide Overhead Bar + Thigh Hold-Down Pads",
            "1. Setup: Sit under pulldown machine, adjusting thigh pads securely over knees.\n" +
            "2. Pull: Pull wide bar down to upper collarbone/chest while leaning back 10 degrees.\n" +
            "3. Squeeze: Squeeze lats at bottom, elbows pointing down.\n" +
            "4. Form Tip: Never pull bar behind neck; always pull to front chest.",
            "PULLDOWN");

        register("Face Pulls",
            "Rear Delts, Rotator Cuff & Upper Traps (Posture)",
            "High Pulley Cable Tower + Rope Attachment",
            "1. Setup: Set cable pulley to face/eye height. Grasp rope ends with overhand grip.\n" +
            "2. Pull: Pull rope directly toward face while externally rotating hands outward past ears.\n" +
            "3. Hold: Squeeze rear deltoids and upper back for 1 second.\n" +
            "4. Form Tip: Essential for shoulder health and counteracting pressing tightness.",
            "ROW");

        // --- LEGS EXERCISES (Quads, Hamstrings, Glutes, Calves) ---
        register("Barbell Squat",
            "Quadriceps (All 4 Heads) & Gluteus Maximus",
            "Squat Rack / Power Cage + Olympic Barbell + Safety Pins",
            "1. Setup: Rest barbell across upper trapezius. Stand with feet shoulder-width apart, toes slightly out.\n" +
            "2. Descent: Break at hips and knees simultaneously, descending until thighs are parallel to floor.\n" +
            "3. Drive: Push through whole foot to stand back up, driving hips forward.\n" +
            "4. Form Tip: Keep chest proud and knees tracking in line with toes.",
            "SQUAT");

        register("Leg Press",
            "Quadriceps & Hamstring Co-Contraction",
            "45-Degree Sled Leg Press Machine + Weight Plates",
            "1. Setup: Sit on sled seat with back flat against pad. Place feet shoulder-width on platform.\n" +
            "2. Lower: Release safety handles and lower sled until knees form 90-degree angle.\n" +
            "3. Press: Press sled upward without locking knees completely at top.\n" +
            "4. Form Tip: Never let lower back lift off seat pad at bottom of stroke.",
            "LEG_PRESS");

        register("Leg Extension",
            "Quadriceps Isolation (Vastus Medialis, Lateralis & Rectus Femoris)",
            "Seated Leg Extension Machine + Adjustable Shin Pad",
            "1. Setup: Adjust backrest so knees align with machine pivot point. Hook shins under lower pad.\n" +
            "2. Extend: Extend legs upward until knees are straight and quads fully contracted.\n" +
            "3. Lower: Control weight down slowly.\n" +
            "4. Form Tip: Great for teardrop quad muscle development.",
            "LEG_PRESS");

        register("Lying Leg Curl",
            "Hamstring Isolation (Biceps Femoris & Semitendinosus)",
            "Lying Hamstring Curl Machine + Padded Roller",
            "1. Setup: Lie face down on machine pad with roller resting against back of lower calves.\n" +
            "2. Curl: Curl heels toward glutes by flexing hamstrings.\n" +
            "3. Squeeze: Hold peak hamstring contraction for 1 second.\n" +
            "4. Form Tip: Keep hips pressed flat against bench pad.",
            "LEG_PRESS");

        register("Romanian Deadlift",
            "Hamstrings (Stretch) & Gluteus Maximus (Hip-Hinge)",
            "Olympic Barbell + Weight Plates",
            "1. Setup: Stand holding barbell at hip level with shoulder-width grip.\n" +
            "2. Hinge: Push hips backward while keeping knees slightly bent, lowering bar along shins.\n" +
            "3. Stretch: Lower until you feel deep hamstring stretch (mid-shin level).\n" +
            "4. Drive: Drive hips forward to return to standing.",
            "DEADLIFT");

        register("Standing Calf Raises",
            "Gastrocnemius & Soleus (Lower Leg Calves)",
            "Standing Calf Raise Machine + Shoulder Pads",
            "1. Setup: Place balls of feet on platform edge, shoulders under pads.\n" +
            "2. Raise: Rise up onto toes as high as possible, holding peak calf contraction for 1s.\n" +
            "3. Lower: Lower heels below platform edge for full stretch.\n" +
            "4. Form Tip: Avoid bouncing; control the bottom stretch.",
            "SQUAT");

        register("Barbell Hip Thrust",
            "Gluteus Maximus (Peak Hip Extension)",
            "Flat Utility Bench + Olympic Barbell + Foam Hip Protector Pad",
            "1. Setup: Sit on floor with upper back against bench edge. Place padded barbell across hips.\n" +
            "2. Drive: Drive through heels to lift hips until thighs and torso form flat horizontal tabletop.\n" +
            "3. Squeeze: Squeeze glutes hard at top for 2 seconds.\n" +
            "4. Form Tip: Keep chin tucked and ribs down.",
            "LEG_PRESS");

        register("Lunges",
            "Gluteus Maximus & Unilateral Quad Balance",
            "Pair of Dumbbells or Barbell",
            "1. Setup: Stand tall holding dumbbells at sides.\n" +
            "2. Step: Step forward 2-3 feet and lower trailing knee to 1 inch above floor.\n" +
            "3. Push: Push off front heel to return to standing.\n" +
            "4. Form Tip: Keep torso upright and front knee over ankle.",
            "SQUAT");

        // --- SHOULDERS EXERCISES (Anterior, Lateral, Posterior Deltoid) ---
        register("Overhead Press",
            "Anterior Deltoid (Front Head) & Upper Chest/Triceps",
            "Standing Olympic Rack + Barbell",
            "1. Setup: Grip bar at shoulder width. Unrack bar across collarbone.\n" +
            "2. Press: Press barbell overhead, moving head slightly back to let bar pass face.\n" +
            "3. Lockout: Lock arms overhead with bar directly over mid-foot.\n" +
            "4. Form Tip: Brace abs and glutes to prevent excessive lumbar hyperextension.",
            "OHP");

        register("Dumbbell Shoulder Press",
            "Anterior & Lateral Deltoids (Shoulder Mass)",
            "90° Upright Seated Utility Bench + 2 Dumbbells",
            "1. Setup: Sit upright holding dumbbells at ear level, palms facing forward.\n" +
            "2. Press: Press dumbbells up until they nearly touch overhead.\n" +
            "3. Lower: Control dumbbells down to ear level.\n" +
            "4. Form Tip: Keep wrists stacked over elbows.",
            "OHP");

        register("Lateral Raises",
            "Lateral Deltoid (Side Head - Shoulder Width)",
            "Standing + 2 Moderate-Weight Dumbbells",
            "1. Setup: Stand tall holding dumbbells in front of thighs with slight elbow bend.\n" +
            "2. Raise: Raise dumbbells out to sides until arms are parallel to floor (T-shape).\n" +
            "3. Lower: Control weights back down slowly.\n" +
            "4. Form Tip: Lead with elbows slightly higher than wrists; imagine pouring a pitcher of water.",
            "LATERAL_RAISE");

        register("Rear Delt Cable Fly",
            "Posterior Deltoid (Rear Shoulder 3D Roundness)",
            "Dual Cable Crossover Machine (High Pulleys)",
            "1. Setup: Grab left cable with right hand and right cable with left hand (crossed cables).\n" +
            "2. Pull: Pull hands out and back horizontally until arms are extended to sides.\n" +
            "3. Squeeze: Squeeze rear shoulder muscles at peak contraction.\n" +
            "4. Form Tip: Keep torso still and focus on pulling with rear deltoids.",
            "LATERAL_RAISE");

        // --- BICEPS & ARMS (Long Head, Short Head, Brachialis) ---
        register("Barbell Curl",
            "Bicep Brachii (Both Long & Short Heads)",
            "Olympic Barbell or EZ-Curl Bar + Standing",
            "1. Setup: Grasp bar with underhand shoulder-width grip, elbows tucked at ribs.\n" +
            "2. Curl: Flex elbows to curl bar toward chin without swinging torso.\n" +
            "3. Lower: Lower bar under 2-second control to full arm extension.\n" +
            "4. Form Tip: Keep upper arms frozen at your sides.",
            "BICEP_CURL");

        register("Incline Dumbbell Curl",
            "Bicep Long Head (Outer Peak & Max Stretch)",
            "Adjustable Incline Bench (45° angle) + Pair of Dumbbells",
            "1. Setup: Sit back on 45° incline bench with arms hanging straight down behind body plane.\n" +
            "2. Curl: Curl dumbbells upward while keeping upper arms pointed straight toward floor.\n" +
            "3. Peak: Squeeze bicep at top, then lower slowly for maximum stretch.\n" +
            "4. Form Tip: The behind-body arm position creates maximum stretch on the Long Head.",
            "BICEP_CURL");

        register("Preacher Curl",
            "Bicep Short Head (Inner Thickness & Lower Bicep)",
            "Preacher Curl Bench Station + EZ-Curl Bar",
            "1. Setup: Rest upper arms flat against angled preacher pad, chest against top edge.\n" +
            "2. Curl: Curl bar up toward shoulders until biceps fully contract.\n" +
            "3. Lower: Lower bar until elbows are nearly straight (do not hyperextend at bottom).\n" +
            "4. Form Tip: Prevents any cheating or swinging momentum.",
            "BICEP_CURL");

        register("Hammer Curls",
            "Brachialis & Brachioradialis (Arm Thickness & Forearm)",
            "Standing + Pair of Dumbbells (Neutral Palms-In Grip)",
            "1. Setup: Hold dumbbells at sides with neutral grip (palms facing each other).\n" +
            "2. Curl: Curl dumbbells upward while maintaining neutral hammer grip.\n" +
            "3. Lower: Lower slowly to starting position.\n" +
            "4. Form Tip: Develops the brachialis muscle underneath the bicep, pushing arm size wider.",
            "BICEP_CURL");

        // --- CORE & CARDIO ---
        register("Plank",
            "Transverse Abdominis & Deep Core Stabilization",
            "Exercise Floor Mat",
            "1. Setup: Support body on forearms and toes, elbows under shoulders.\n" +
            "2. Brace: Squeeze glutes and abdominals to form a rigid plank line.\n" +
            "3. Hold: Breathe steadily through nose while keeping tension.\n" +
            "4. Form Tip: Do not let lower back arch or hips lift.",
            "CORE_PLANK");

        register("Cable Crunches",
            "Rectus Abdominis (Upper & Lower Abs)",
            "High Pulley Cable Station + Rope Attachment + Kneeling Pad",
            "1. Setup: Kneel below high pulley holding rope ends on either side of neck.\n" +
            "2. Crunch: Curl torso downward, bringing elbows toward knees by flexing abs.\n" +
            "3. Squeeze: Hold bottom crunch for 1 second.\n" +
            "4. Form Tip: Do not pull with arms; initiate movement by curling spine.",
            "CORE_PLANK");

        register("Treadmill Running",
            "Cardiovascular Endurance & Aerobic Capacity",
            "Commercial Treadmill Running Machine",
            "1. Setup: Step onto treadmill belt and set incline to 1% to simulate outdoor running.\n" +
            "2. Run: Maintain upright posture with relaxed shoulders and natural arm swing.\n" +
            "3. Pace: Keep cadence steady at target heart rate zone.\n" +
            "4. Form Tip: Land mid-foot under center of gravity rather than heel-striking far out.",
            "CARDIO_RUN");

        register("Cycling",
            "Cardiovascular Endurance & Quad Stamina",
            "Stationary Spin Bike / Upright Cycle Machine",
            "1. Setup: Adjust saddle height so knee has slight 5-10° bend at bottom pedal stroke.\n" +
            "2. Ride: Maintain smooth circular pedaling motion, pulling up as well as pushing down.\n" +
            "3. Form Tip: Keep upper body relaxed and shoulders away from ears.",
            "CARDIO_RUN");

        register("Jump Rope",
            "Cardiovascular Agility & Calf Endurance",
            "Speed Jump Rope",
            "1. Setup: Hold handles at waist height, elbows tucked.\n" +
            "2. Jump: Jump 1 inch off floor, turning rope with wrists rather than full shoulders.\n" +
            "3. Form Tip: Stay light on balls of feet.",
            "CARDIO_RUN");

        register("Burpees",
            "Full Body HIIT & Cardiovascular Power",
            "Open Gym Floor Space",
            "1. Setup: Stand tall, drop into squat, place hands on floor.\n" +
            "2. Jump: Kick feet back into push-up plank, perform push-up, jump feet back to hands, jump vertically.\n" +
            "3. Form Tip: Keep core tight during plank transition.",
            "CARDIO_RUN");

        register("Mountain Climbers",
            "Core Agility & High-Intensity Cardio",
            "Exercise Floor Mat",
            "1. Setup: Assume push-up plank position with hands under shoulders.\n" +
            "2. Drive: Alternately drive knees toward chest in a rapid running motion.\n" +
            "3. Form Tip: Keep hips level and low throughout.",
            "CARDIO_RUN");
    }

    private static void register(String name, String targetRegion, String machineSetup, String formGuide, String illustrationType) {
        CATALOG.put(name.toLowerCase(), new GuideInfo(targetRegion, machineSetup, formGuide, illustrationType));
    }

    /**
     * Get GuideInfo for any exercise name, returning a scientifically accurate fallback if not found.
     */
    public static GuideInfo getGuide(String exerciseName, String defaultGroup) {
        if (exerciseName != null) {
            GuideInfo info = CATALOG.get(exerciseName.toLowerCase());
            if (info != null) return info;

            // Partial matching for custom exercises
            String lower = exerciseName.toLowerCase();
            if (lower.contains("incline") && lower.contains("press")) {
                return new GuideInfo("Upper Chest (Clavicular Head)", "Adjustable Incline Bench (30°–45°) + Dumbbells/Barbell",
                    "1. Setup on 30-45° incline bench.\n2. Press weight upward over eyes.\n3. Control descent to upper chest.", "INCLINE_PRESS");
            }
            if (lower.contains("decline") && lower.contains("press")) {
                return new GuideInfo("Lower Chest (Costal Head)", "Decline Bench Station + Barbell",
                    "1. Secure feet in decline pads.\n2. Press bar to lower chest.\n3. Keep elbows at 45° angle.", "BENCH_PRESS");
            }
            if (lower.contains("bench") || lower.contains("press") && lower.contains("chest")) {
                return new GuideInfo("Middle Chest (Sternal Head)", "Flat Olympic Bench + Barbell/Dumbbells",
                    "1. Lie flat on bench.\n2. Lower bar to mid-chest.\n3. Drive upward to lockout.", "BENCH_PRESS");
            }
            if (lower.contains("fly") || lower.contains("crossover")) {
                return new GuideInfo("Inner Chest & Pectoral Stretch", "Cable Crossover Machine + D-Handles",
                    "1. Stand in cable tower.\n2. Bring handles together in hugging arc.\n3. Squeeze chest at center.", "CABLE_FLY");
            }
            if (lower.contains("skull") || lower.contains("extension")) {
                return new GuideInfo("Tricep Long Head (Overhead Mass)", "Flat Bench + EZ-Curl Bar / Cable",
                    "1. Keep upper arms vertical.\n2. Lower bar toward forehead/behind head.\n3. Extend elbows to lockout.", "SKULL_CRUSHER");
            }
            if (lower.contains("pushdown") || lower.contains("tricep")) {
                return new GuideInfo("Tricep Lateral & Medial Head", "High Pulley Cable Tower + V-Bar / Rope",
                    "1. Pin elbows to sides.\n2. Press cable attachment down to thighs.\n3. Squeeze triceps at bottom.", "CABLE_PUSHDOWN");
            }
            if (lower.contains("squat")) {
                return new GuideInfo("Quadriceps (All 4 Heads) & Glutes", "Squat Rack + Olympic Barbell",
                    "1. Bar across shoulders.\n2. Squat until thighs parallel to floor.\n3. Drive through whole foot.", "SQUAT");
            }
            if (lower.contains("deadlift") || lower.contains("rdl")) {
                return new GuideInfo("Posterior Chain (Hamstrings, Glutes & Lats)", "Olympic Barbell + Bumper Plates",
                    "1. Hinge hips back with flat spine.\n2. Lower bar along shins.\n3. Drive hips forward to lockout.", "DEADLIFT");
            }
            if (lower.contains("row")) {
                return new GuideInfo("Mid-Back (Rhomboids, Traps & Back Thickness)", "Barbell / Cable Row Station",
                    "1. Keep torso angle stable.\n2. Row weight toward upper abdomen.\n3. Squeeze shoulder blades together.", "ROW");
            }
            if (lower.contains("pull") || lower.contains("lat")) {
                return new GuideInfo("Lats (V-Taper Back Width)", "Lat Pulldown Machine / Pull-Up Bar",
                    "1. Grasp wide bar.\n2. Pull down to upper chest.\n3. Keep elbows pointing straight down.", "PULLDOWN");
            }
            if (lower.contains("overhead") || lower.contains("ohp") || lower.contains("shoulder")) {
                return new GuideInfo("Anterior & Lateral Deltoids", "Standing / Seated Olympic Rack + Barbell/Dumbbells",
                    "1. Press weight overhead from collarbone.\n2. Lock arms directly over mid-foot.\n3. Keep core braced.", "OHP");
            }
            if (lower.contains("raise") || lower.contains("lateral")) {
                return new GuideInfo("Lateral Deltoid (Side Shoulder Width)", "Standing + Pair of Dumbbells / Cable",
                    "1. Raise arms out to sides to shoulder level.\n2. Slight bend in elbows.\n3. Control descent.", "LATERAL_RAISE");
            }
            if (lower.contains("curl") || lower.contains("bicep")) {
                return new GuideInfo("Bicep Brachii (Long & Short Heads)", "Standing / Incline Bench + Barbell/Dumbbells",
                    "1. Keep elbows tucked at ribs.\n2. Curl weight toward shoulders.\n3. Control descent to full stretch.", "BICEP_CURL");
            }
        }

        // Generic fallback based on muscle group
        String grp = defaultGroup != null ? defaultGroup.toUpperCase() : "GENERAL";
        return switch (grp) {
            case "CHEST" -> new GuideInfo("Pectoralis Major (Upper, Middle, Lower Heads)", "Olympic Bench Press / Dumbbells / Cable Machine",
                "1. Keep chest proud and shoulder blades retracted.\n2. Lower weight under control.\n3. Press to lockout.", "BENCH_PRESS");
            case "BACK" -> new GuideInfo("Lats, Rhomboids & Traps (Back Width & Thickness)", "Lat Pulldown / Cable Row Machine / Barbell",
                "1. Initiate pulling with shoulder blades.\n2. Pull bar toward chest/abdomen.\n3. Hold peak contraction.", "PULLDOWN");
            case "LEGS" -> new GuideInfo("Quadriceps, Hamstrings, Glutes & Calves", "Squat Rack / 45° Leg Press Machine",
                "1. Keep knees tracking over toes.\n2. Maintain flat lower back.\n3. Drive through whole foot.", "SQUAT");
            case "SHOULDERS" -> new GuideInfo("Deltoids (Anterior, Lateral & Posterior Heads)", "Standing Olympic Rack / Seated Bench + Dumbbells",
                "1. Press overhead or raise to sides.\n2. Keep wrists stacked over elbows.\n3. Maintain core posture.", "OHP");
            case "ARMS" -> new GuideInfo("Biceps Brachii & Triceps Brachii (3 Heads)", "Cable Pulley Station / EZ-Curl Bar / Dumbbells",
                "1. Keep upper arms stationary.\n2. Flex or extend at elbow joint only.\n3. Squeeze at lockout.", "BICEP_CURL");
            case "CORE" -> new GuideInfo("Abdominal Wall (Rectus & Transverse Abdominis)", "Floor Mat / High Pulley Cable Station",
                "1. Squeeze glutes and abdominals.\n2. Maintain neutral lower back.\n3. Breathe steadily.", "CORE_PLANK");
            default -> new GuideInfo("Full Body Biomechanical Compound", "Gym Training Equipment / Free Weights",
                "1. Maintain proper posture and alignment.\n2. Execute movement under control.\n3. Focus on target muscle.", "BENCH_PRESS");
        };
    }

    /**
     * Returns a structured anatomical breakdown explanation for any workout split name.
     */
    public static String getAnatomicalOverview(String workoutName) {
        if (workoutName == null) return "Focus on balanced form, full range of motion, and progressive overload across all target muscles.";
        String lower = workoutName.toLowerCase();

        if (lower.contains("chest") && lower.contains("tri")) {
            return "<html><div style='line-height:1.4'>" +
                "<b>🫁 Chest Anatomical Regions (3 Heads):</b><br>" +
                "• <b>Upper Chest (Clavicular Head):</b> Trained via <i>Incline Dumbbell Press</i> (30°–45° incline bench)<br>" +
                "• <b>Middle Chest (Sternal Head):</b> Trained via <i>Flat Barbell Bench Press</i><br>" +
                "• <b>Lower Chest (Costal Head):</b> Trained via <i>Chest Dips / Decline Press</i><br><br>" +
                "<b>🦾 Tricep Anatomical Regions (3 Heads):</b><br>" +
                "• <b>Long Head (Overhead Mass):</b> Trained via <i>EZ-Bar Skull Crushers</i><br>" +
                "• <b>Lateral Head (Outer Horseshoe):</b> Trained via <i>Cable Pushdown (V-Bar)</i><br>" +
                "• <b>Medial Head (Lockout):</b> Trained via <i>Rope Pushdown with bottom flare</i></div></html>";
        }
        if (lower.contains("back") && lower.contains("bi")) {
            return "<html><div style='line-height:1.4'>" +
                "<b>🦍 Back Anatomical Regions:</b><br>" +
                "• <b>Upper Lats (V-Taper Width):</b> Trained via <i>Pull Ups / Lat Pulldown</i><br>" +
                "• <b>Mid-Back / Rhomboids (Thickness):</b> Trained via <i>Barbell Row / Seated Cable Row</i><br>" +
                "• <b>Lower Back & Posterior Chain:</b> Trained via <i>Deadlift / Romanian Deadlift</i><br><br>" +
                "<b>💪 Bicep Anatomical Regions:</b><br>" +
                "• <b>Long Head (Outer Peak):</b> Trained via <i>Incline Dumbbell Curl</i><br>" +
                "• <b>Short Head (Inner Width):</b> Trained via <i>Preacher Curl / Barbell Curl</i><br>" +
                "• <b>Brachialis (Forearm Thickness):</b> Trained via <i>Hammer Curls</i></div></html>";
        }
        if (lower.contains("leg")) {
            return "<html><div style='line-height:1.4'>" +
                "<b>🦵 Leg Anatomical Muscle Groups:</b><br>" +
                "• <b>Quadriceps (4 Front Thigh Heads):</b> Trained via <i>Barbell Back Squat & Leg Press</i><br>" +
                "• <b>Hamstrings (Rear Thigh):</b> Trained via <i>Romanian Deadlift & Lying Leg Curl</i><br>" +
                "• <b>Gluteus Maximus/Medius (Hip Extension):</b> Trained via <i>Barbell Hip Thrust & Lunges</i><br>" +
                "• <b>Calves (Gastrocnemius & Soleus):</b> Trained via <i>Standing Calf Raises</i></div></html>";
        }
        if (lower.contains("shoulder") || lower.contains("arm")) {
            return "<html><div style='line-height:1.4'>" +
                "<b>🛡️ Shoulder Deltoid Heads (3 Heads):</b><br>" +
                "• <b>Anterior Deltoid (Front Head):</b> Trained via <i>Overhead Press (OHP) / DB Press</i><br>" +
                "• <b>Lateral Deltoid (Side Width):</b> Trained via <i>Dumbbell Lateral Raises</i><br>" +
                "• <b>Posterior Deltoid (Rear 3D Roundness):</b> Trained via <i>Rear Delt Cable Fly / Face Pulls</i><br><br>" +
                "<b>💪 Arm Supersets:</b><br>" +
                "• Bicep Peak (Long Head) & Inner Thickness (Short Head)<br>" +
                "• Tricep Long Head Overhead Stretch & Outer Horseshoe Pushdown</div></html>";
        }

        return "<html><div style='line-height:1.4'><b>🏋️ Comprehensive Muscle Group Training:</b><br>" +
               "Covers all major anatomical heads with proper equipment selection and biomechanical form guidance.</div></html>";
    }
}
