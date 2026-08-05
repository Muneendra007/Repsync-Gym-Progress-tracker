package com.repsync.ui.components;

import com.repsync.ui.ThemeManager;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Animated Neon-Glow Holographic Exercise Illustration Panel.
 * Renders smooth motion-cycling human figures with glowing neon muscle overlays,
 * pulsing particle effects, and a sci-fi HUD frame. Each exercise type animates
 * through its range of motion on a continuous loop.
 */
public class ExerciseIllustrationPanel extends JPanel {

    private String illustrationType;
    private String exerciseName;
    private String targetRegion;
    private String machineSetup;

    // Animation state
    private float animPhase = 0f;       // 0..1 phase of the exercise motion cycle
    private float glowPulse = 0f;       // 0..1 glow intensity cycle
    private float hudScanY = 0f;        // scan-line Y position
    private final Timer animTimer;
    private final List<Particle> particles = new ArrayList<>();
    private final Random rng = new Random();

    // Neon palette
    private static final Color NEON_CYAN = new Color(0, 255, 255);
    private static final Color NEON_MAGENTA = new Color(255, 0, 200);
    private static final Color NEON_RED = new Color(255, 50, 50);
    private static final Color NEON_GREEN = new Color(50, 255, 120);
    private static final Color NEON_BLUE = new Color(80, 140, 255);
    private static final Color BODY_FILL = new Color(200, 220, 240);
    private static final Color BODY_OUTLINE = new Color(120, 160, 220);

    public ExerciseIllustrationPanel(String illustrationType, String exerciseName, String targetRegion, String machineSetup) {
        this.illustrationType = illustrationType != null ? illustrationType.toUpperCase() : "BENCH_PRESS";
        this.exerciseName = exerciseName != null ? exerciseName : "Exercise";
        this.targetRegion = targetRegion != null ? targetRegion : "Target Muscle";
        this.machineSetup = machineSetup != null ? machineSetup : "Gym Equipment";
        setOpaque(false);
        setPreferredSize(new Dimension(440, 280));
        setMinimumSize(new Dimension(320, 220));

        // Seed particles
        for (int i = 0; i < 18; i++) {
            particles.add(new Particle(rng));
        }

        // 30 FPS animation timer
        animTimer = new Timer(33, e -> {
            animPhase = (animPhase + 0.012f) % 1.0f;
            glowPulse = (glowPulse + 0.025f) % 1.0f;
            hudScanY = (hudScanY + 1.5f);
            if (hudScanY > getHeight() + 20) hudScanY = -20;
            for (Particle p : particles) p.update(getWidth(), getHeight());
            repaint();
        });
        animTimer.start();
    }

    public void setExerciseData(String illustrationType, String exerciseName, String targetRegion, String machineSetup) {
        this.illustrationType = illustrationType != null ? illustrationType.toUpperCase() : "BENCH_PRESS";
        this.exerciseName = exerciseName != null ? exerciseName : "Exercise";
        this.targetRegion = targetRegion != null ? targetRegion : "Target Muscle";
        this.machineSetup = machineSetup != null ? machineSetup : "Gym Equipment";
        repaint();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        animTimer.stop();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (!animTimer.isRunning()) animTimer.start();
    }

    // ── Motion easing: smooth sinusoidal 0→1→0 ──
    private float ease(float t) {
        return (float) (0.5 - 0.5 * Math.cos(t * 2.0 * Math.PI));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        int w = getWidth();
        int h = getHeight();

        // 1. Background card with deep gradient
        RoundRectangle2D.Float card = new RoundRectangle2D.Float(0, 0, w - 1, h - 1, 22, 22);
        GradientPaint bgGrad = ThemeManager.isDarkMode()
            ? new GradientPaint(0, 0, new Color(8, 12, 28), w, h, new Color(14, 18, 36))
            : new GradientPaint(0, 0, new Color(225, 235, 245), w, h, new Color(240, 245, 250));
        g2.setPaint(bgGrad);
        g2.fill(card);

        // 2. Hexagonal grid pattern (subtle)
        drawHexGrid(g2, w, h);

        // 3. Floating particles
        for (Particle p : particles) p.draw(g2);

        // 4. HUD scan-line
        drawScanLine(g2, w, h);

        // 5. Draw the animated exercise figure
        drawIllustration(g2, w, h);

        // 6. HUD corner brackets
        drawHUDFrame(g2, w, h);

        // 7. Card border glow
        float glow = ease(glowPulse);
        g2.setColor(new Color(NEON_CYAN.getRed(), NEON_CYAN.getGreen(), NEON_CYAN.getBlue(), (int) (30 + 25 * glow)));
        g2.setStroke(new BasicStroke(2f));
        g2.draw(card);

        g2.dispose();
    }

    // ══════════════════════════════════════════════════
    //  ANIMATED EXERCISE ILLUSTRATIONS
    // ══════════════════════════════════════════════════

    private void drawIllustration(Graphics2D g2, int w, int h) {
        int cx = w / 2;
        int cy = h / 2 + 10;
        float t = ease(animPhase); // 0→1→0 smooth motion

        switch (illustrationType) {
            case "INCLINE_PRESS" -> drawInclinePress(g2, cx, cy, t);
            case "BENCH_PRESS"   -> drawBenchPress(g2, cx, cy, t);
            case "DIPS"          -> drawDips(g2, cx, cy, t);
            case "CABLE_FLY"     -> drawCableFly(g2, cx, cy, t);
            case "SKULL_CRUSHER" -> drawSkullCrusher(g2, cx, cy, t);
            case "CABLE_PUSHDOWN"-> drawCablePushdown(g2, cx, cy, t);
            case "SQUAT"         -> drawSquat(g2, cx, cy, t);
            case "LEG_PRESS"     -> drawLegPress(g2, cx, cy, t);
            case "DEADLIFT"      -> drawDeadlift(g2, cx, cy, t);
            case "ROW"           -> drawSeatedRow(g2, cx, cy, t);
            case "PULLDOWN"      -> drawPulldown(g2, cx, cy, t);
            case "OHP"           -> drawOHP(g2, cx, cy, t);
            case "LATERAL_RAISE" -> drawLateralRaise(g2, cx, cy, t);
            case "BICEP_CURL"    -> drawBicepCurl(g2, cx, cy, t);
            case "CORE_PLANK"    -> drawPlank(g2, cx, cy, t);
            case "CARDIO_RUN"    -> drawCardioRun(g2, cx, cy, t);
            default              -> drawBenchPress(g2, cx, cy, t);
        }

        // Exercise name label at bottom
        drawExerciseLabel(g2, w, h);
    }

    // ─────────────────────────────────────────────────
    //  EACH EXERCISE WITH ANIMATED MOTION PHASE (t)
    // ─────────────────────────────────────────────────

    private void drawInclinePress(Graphics2D g2, int cx, int cy, float t) {
        // Incline bench
        drawEquipmentLine(g2, cx - 75, cy + 55, cx + 25, cy - 15);
        drawEquipmentRect(g2, cx - 70, cy + 50, cx + 22, cy - 12, 14);

        // Body on incline — arms animate up/down
        int armEndY = (int) (cy - 30 - 35 * t);
        drawNeonHead(g2, cx + 22, cy - 30);
        drawNeonTorso(g2, cx - 25, cy + 5, 48, 34, true);
        drawNeonLeg(g2, cx - 25, cy + 25, cx - 55, cy + 35, cx - 55, cy + 65);
        drawNeonArm(g2, cx + 5, cy - 5, cx + 5, armEndY, cx + 5, armEndY - 14);

        // Dumbbell follows arms
        drawEquipmentBar(g2, cx - 12, armEndY - 20, 34, 14);

        // Pulsing muscle glow on upper chest
        drawMuscleGlow(g2, cx + 2, cy - 16, 24, 18);
        drawMotionTrail(g2, cx + 5, cy - 30, cx + 5, armEndY - 14, t, "Clavicular Drive");
    }

    private void drawBenchPress(Graphics2D g2, int cx, int cy, float t) {
        // Flat bench
        drawEquipmentLine(g2, cx - 80, cy + 20, cx + 80, cy + 20);
        drawEquipmentRect(g2, cx - 80, cy + 10, cx + 80, cy + 26, 8);

        // Lying body — barbell goes up/down
        int barbellY = (int) (cy - 15 - 40 * t);
        drawNeonHead(g2, cx + 45, cy + 5);
        drawNeonTorso(g2, cx - 25, cy + 5, 60, 28, true);
        drawNeonLeg(g2, cx - 25, cy + 15, cx - 55, cy + 30, cx - 55, cy + 60);
        drawNeonArm(g2, cx + 15, cy + 5, cx + 15, barbellY, cx + 15, barbellY - 15);

        // Barbell
        g2.setColor(new Color(203, 213, 225));
        g2.setStroke(new BasicStroke(5f));
        g2.drawLine(cx - 60, barbellY, cx + 60, barbellY);
        drawEquipmentBar(g2, cx - 55, barbellY - 20, 12, 40);
        drawEquipmentBar(g2, cx + 43, barbellY - 20, 12, 40);

        drawMuscleGlow(g2, cx + 5, cy - 2, 24, 18);
        drawMotionTrail(g2, cx + 15, cy + 5, cx + 15, barbellY - 15, t, "Sternal Press");
    }

    private void drawSquat(Graphics2D g2, int cx, int cy, float t) {
        // Rack uprights
        drawEquipmentLine(g2, cx - 60, cy + 65, cx - 60, cy - 65);
        drawEquipmentLine(g2, cx + 60, cy + 65, cx + 60, cy - 65);

        // Body squats down with t
        int sqDepth = (int) (30 * t);
        int headY = cy - 55 + sqDepth;
        int kneeX = (int) (cx + 15 + 10 * t);
        drawNeonHead(g2, cx - 10, headY);
        drawNeonTorso(g2, cx - 25, cy - 30 + sqDepth, 32, 50, false);
        drawNeonLeg(g2, cx - 25, cy + 15 + sqDepth / 2, kneeX, cy + 20 + sqDepth, cx + 5, cy + 65);

        // Barbell on shoulders
        int barY = cy - 35 + sqDepth;
        g2.setColor(new Color(203, 213, 225));
        g2.setStroke(new BasicStroke(6f));
        g2.drawLine(cx - 65, barY, cx + 45, barY);

        drawMuscleGlow(g2, cx - 10, cy + 10 + sqDepth / 2, 30, 18);
        drawMotionTrail(g2, cx + 15, cy + 25 + sqDepth, cx + 15, cy - 25, t, "Quad Drive");
    }

    private void drawDeadlift(Graphics2D g2, int cx, int cy, float t) {
        // Platform
        drawEquipmentLine(g2, cx - 60, cy + 60, cx + 60, cy + 60);
        drawEquipmentBar(g2, cx - 45, cy + 30, 32, 32);

        // Hip hinge — torso angle animates
        int torsoTilt = (int) (25 * (1 - t));
        drawNeonHead(g2, cx + 25 - torsoTilt, cy - 50 + torsoTilt);
        drawNeonTorso(g2, cx + 15 - torsoTilt / 2, cy - 30 + torsoTilt / 2, 32, 45, false);
        drawNeonLeg(g2, cx - 15, cy - 5, cx, cy + 25, cx - 5, cy + 60);
        drawNeonArm(g2, cx + 5, cy - 25 + torsoTilt / 2, cx - 25, cy + 45 - torsoTilt, cx - 25, cy + 45 - torsoTilt);

        drawMuscleGlow(g2, cx - 5, cy - 22, 24, 18);
        drawMotionTrail(g2, cx - 35, cy + 25, cx - 10, cy - 35, t, "Hip Hinge");
    }

    private void drawSeatedRow(Graphics2D g2, int cx, int cy, float t) {
        // Cable station + bench
        drawEquipmentLine(g2, cx - 110, cy + 50, cx + 40, cy + 50);
        g2.setColor(new Color(71, 85, 105));
        g2.fillRect(cx - 110, cy - 60, 20, 110);
        drawEquipmentRect(g2, cx - 30, cy + 20, cx + 20, cy + 50, 8);

        // Cable — handle position animates with t
        int handleX = (int) (cx - 90 + 75 * t);
        g2.setColor(new Color(NEON_CYAN.getRed(), NEON_CYAN.getGreen(), NEON_CYAN.getBlue(), 120));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawLine(cx - 90, cy + 20, handleX, cy - 5);

        drawNeonLeg(g2, cx - 15, cy + 25, cx + 10, cy + 20, cx + 25, cy + 20);
        drawNeonTorso(g2, cx - 25, cy - 35, 34, 55, false);
        drawNeonHead(g2, cx - 25, cy - 55);
        drawNeonArm(g2, cx - 15, cy - 30, handleX - 5, cy - 10, handleX, cy - 5);

        drawMuscleGlow(g2, cx - 34, cy - 35, 18, 28);
        drawMotionTrail(g2, cx - 90, cy - 5, handleX, cy - 5, t, "Retraction");
    }

    private void drawPulldown(Graphics2D g2, int cx, int cy, float t) {
        // Pulldown station
        drawEquipmentLine(g2, cx - 40, cy + 60, cx + 40, cy + 60);
        drawEquipmentLine(g2, cx - 60, cy - 70, cx + 60, cy - 70);

        // Bar descends with t
        int barY = (int) (cy - 68 + 43 * t);
        int armEndY = (int) (cy - 50 + 25 * t);
        drawNeonHead(g2, cx, cy - 45);
        drawNeonTorso(g2, cx - 15, cy - 20, 30, 50, false);
        drawNeonLeg(g2, cx - 15, cy + 25, cx + 20, cy + 25, cx + 20, cy + 60);
        drawNeonArm(g2, cx, cy - 25, cx - 35, armEndY, cx - 45, barY);

        // Cable
        g2.setColor(new Color(NEON_CYAN.getRed(), NEON_CYAN.getGreen(), NEON_CYAN.getBlue(), 80));
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(cx, cy - 70, cx - 45, barY);

        drawMuscleGlow(g2, cx - 20, cy - 20, 16, 32);
        drawMotionTrail(g2, cx - 45, cy - 68, cx - 25, cy - 25, t, "Lat Pulldown");
    }

    private void drawDips(Graphics2D g2, int cx, int cy, float t) {
        // Parallel bars
        drawEquipmentLine(g2, cx - 50, cy + 65, cx - 50, cy - 10);
        drawEquipmentLine(g2, cx - 60, cy - 10, cx - 10, cy - 10);

        // Body dips down with t
        int dipDepth = (int) (20 * t);
        drawNeonHead(g2, cx - 20, cy - 55 + dipDepth);
        drawNeonTorso(g2, cx - 25, cy - 30 + dipDepth, 30, 55, false);
        drawNeonLeg(g2, cx - 25, cy + 25 + dipDepth, cx - 40, cy + 55, cx - 40, cy + 55);
        drawNeonArm(g2, cx - 15, cy - 25 + dipDepth, cx - 35, cy - 10, cx - 35, cy - 10);

        drawMuscleGlow(g2, cx - 15, cy - 15 + dipDepth, 20, 18);
        drawMotionTrail(g2, cx - 20, cy + 20, cx - 20, cy - 25, t, "Chest Dip");
    }

    private void drawCableFly(Graphics2D g2, int cx, int cy, float t) {
        // Cable towers
        drawEquipmentLine(g2, cx - 80, cy - 50, cx + 80, cy - 50);

        // Arms sweep inward with t
        int armSpread = (int) (45 - 40 * t);
        drawNeonHead(g2, cx, cy - 55);
        drawNeonTorso(g2, cx - 15, cy - 30, 30, 55, false);
        drawNeonLeg(g2, cx - 15, cy + 25, cx - 15, cy + 65, cx - 15, cy + 65);
        drawNeonArm(g2, cx - 5, cy - 25, cx - armSpread, cy - 15, cx - (int) (5 + (1 - t) * 40), cy - 5);

        // Cables
        g2.setColor(new Color(NEON_CYAN.getRed(), NEON_CYAN.getGreen(), NEON_CYAN.getBlue(), 80));
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(cx - 80, cy - 50, cx - armSpread, cy - 15);
        g2.drawLine(cx + 80, cy - 50, cx + armSpread, cy - 15);

        drawMuscleGlow(g2, cx - 10, cy - 22, 20, 20);
        drawMotionTrail(g2, cx - 45, cy - 15, cx - 5, cy - 5, t, "Fly Squeeze");
    }

    private void drawSkullCrusher(Graphics2D g2, int cx, int cy, float t) {
        // Flat bench
        drawEquipmentLine(g2, cx - 70, cy + 20, cx + 70, cy + 20);

        // Forearm angle changes with t (lowering toward head)
        int forearmEndX = (int) (cx + 38 - 23 * t);
        int forearmEndY = (int) (cy - 20 - 35 * (1 - t));
        drawNeonHead(g2, cx + 40, cy + 5);
        drawNeonTorso(g2, cx - 25, cy + 5, 55, 28, true);
        drawNeonLeg(g2, cx - 25, cy + 15, cx - 55, cy + 30, cx - 55, cy + 60);
        drawNeonArm(g2, cx + 15, cy + 5, cx + 15, cy - 35, forearmEndX, forearmEndY);

        drawMuscleGlow(g2, cx + 9, cy - 25, 14, 24);
        drawMotionTrail(g2, forearmEndX, forearmEndY, cx + 15, cy - 55, t, "Tricep Stretch");
    }

    private void drawCablePushdown(Graphics2D g2, int cx, int cy, float t) {
        // High cable tower
        drawEquipmentLine(g2, cx + 60, cy + 65, cx + 60, cy - 65);

        // Arms push down with t
        int handY = (int) (cy - 10 + 30 * t);
        g2.setColor(new Color(NEON_CYAN.getRed(), NEON_CYAN.getGreen(), NEON_CYAN.getBlue(), 80));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawLine(cx + 55, cy - 60, cx + 10, handY);

        drawNeonHead(g2, cx - 20, cy - 60);
        drawNeonTorso(g2, cx - 25, cy - 35, 30, 55, false);
        drawNeonLeg(g2, cx - 25, cy + 20, cx - 25, cy + 65, cx - 25, cy + 65);
        drawNeonArm(g2, cx - 10, cy - 30, cx - 5, cy - 10, cx + 10, handY);

        drawMuscleGlow(g2, cx - 14, cy - 28, 14, 24);
        drawMotionTrail(g2, cx + 10, cy - 20, cx + 10, handY, t, "Pushdown");
    }

    private void drawBicepCurl(Graphics2D g2, int cx, int cy, float t) {
        // Forearm curls up with t
        int forearmEndY = (int) (cy - 10 - 30 * t);
        drawNeonHead(g2, cx - 15, cy - 60);
        drawNeonTorso(g2, cx - 20, cy - 35, 30, 55, false);
        drawNeonLeg(g2, cx - 20, cy + 20, cx - 20, cy + 65, cx - 20, cy + 65);
        drawNeonArm(g2, cx, cy - 30, cx + 15, cy - 10, cx + 5, forearmEndY);

        // Dumbbell
        drawEquipmentBar(g2, cx - 5, forearmEndY - 8, 20, 12);

        drawMuscleGlow(g2, cx + 2, cy - 28, 16, 20);
        drawMotionTrail(g2, cx + 15, cy - 10, cx + 5, forearmEndY, t, "Curl Arc");
    }

    private void drawOHP(Graphics2D g2, int cx, int cy, float t) {
        // Arms press overhead with t
        int pressY = (int) (cy - 45 - 30 * t);
        drawNeonHead(g2, cx - 10, cy - 45);
        drawNeonTorso(g2, cx - 15, cy - 25, 30, 55, false);
        drawNeonLeg(g2, cx - 15, cy + 30, cx - 15, cy + 65, cx - 15, cy + 65);
        drawNeonArm(g2, cx, cy - 20, cx - 20, pressY, cx, pressY - 25);

        // Barbell
        g2.setColor(new Color(203, 213, 225));
        g2.setStroke(new BasicStroke(6f));
        g2.drawLine(cx - 45, pressY - 25, cx + 45, pressY - 25);

        drawMuscleGlow(g2, cx - 15, cy - 25, 20, 20);
        drawMotionTrail(g2, cx, cy - 45, cx, pressY - 25, t, "Overhead Drive");
    }

    private void drawLateralRaise(Graphics2D g2, int cx, int cy, float t) {
        // Arms raise laterally with t
        int raiseY = (int) (cy + 5 - 30 * t);
        int spreadX = (int) (15 + 40 * t);
        drawNeonHead(g2, cx, cy - 55);
        drawNeonTorso(g2, cx - 15, cy - 30, 30, 55, false);
        drawNeonLeg(g2, cx - 15, cy + 25, cx - 15, cy + 65, cx - 15, cy + 65);

        // Animated arms spreading outward
        g2.setColor(BODY_FILL);
        g2.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(cx - 15, cy - 25, cx - spreadX, raiseY);
        g2.drawLine(cx + 15, cy - 25, cx + spreadX, raiseY);

        // Dumbbells in hands
        drawEquipmentBar(g2, cx - spreadX - 6, raiseY - 8, 12, 16);
        drawEquipmentBar(g2, cx + spreadX - 6, raiseY - 8, 12, 16);

        drawMuscleGlow(g2, cx - 22, cy - 28, 16, 16);
        drawMuscleGlow(g2, cx + 6, cy - 28, 16, 16);
        drawMotionTrail(g2, cx - 35, cy + 5, cx - spreadX, raiseY, t, "Side Raise");
    }

    private void drawLegPress(Graphics2D g2, int cx, int cy, float t) {
        // 45-deg sled machine
        drawEquipmentLine(g2, cx - 60, cy + 50, cx + 60, cy + 50);
        drawEquipmentLine(g2, cx - 40, cy + 50, cx - 20, cy + 10);
        drawEquipmentLine(g2, cx + 20, cy + 10, cx + 50, cy - 30);
        drawEquipmentBar(g2, cx + 35, cy - 45, 14, 35);

        // Legs extend with t
        int kneeX = (int) (cx + 15 + 20 * t);
        int footY = (int) (cy - 25 + 25 * (1 - t));
        drawNeonHead(g2, cx - 35, cy - 25);
        drawNeonTorso(g2, cx - 25, cy - 5, 30, 30, false);
        drawNeonLeg(g2, cx - 10, cy + 25, kneeX, cy, cx + 35, footY);

        drawMuscleGlow(g2, cx - 2, cy + 4, 24, 18);
        drawMotionTrail(g2, cx + 10, cy + 15, cx + 45, footY, t, "Sled Press");
    }

    private void drawPlank(Graphics2D g2, int cx, int cy, float t) {
        // Floor mat
        drawEquipmentLine(g2, cx - 80, cy + 30, cx + 80, cy + 30);

        // Subtle body tremor for isometric tension
        int tremble = (int) (2 * Math.sin(animPhase * 20));
        drawNeonHead(g2, cx + 55, cy + tremble);
        drawNeonTorso(g2, cx - 15, cy - 5 + tremble, 60, 22, true);
        drawNeonLeg(g2, cx - 15, cy + 2 + tremble, cx - 65, cy + 5, cx - 65, cy + 25);

        // Forearm support
        g2.setColor(BODY_FILL);
        g2.setStroke(new BasicStroke(8f));
        g2.drawLine(cx + 35, cy + 10 + tremble, cx + 50, cy + 25);

        drawMuscleGlow(g2, cx + 5, cy - 2, 28, 16);
        drawMotionTrail(g2, cx + 15, cy + 15, cx + 15, cy - 15, t, "Core Hold");
    }

    private void drawCardioRun(Graphics2D g2, int cx, int cy, float t) {
        // Treadmill belt with animated lines
        drawEquipmentLine(g2, cx - 70, cy + 55, cx + 70, cy + 55);
        // Belt lines scrolling
        g2.setColor(new Color(71, 85, 105, 80));
        g2.setStroke(new BasicStroke(1f));
        for (int i = 0; i < 6; i++) {
            int lx = (int) ((cx - 65 + i * 25 + animPhase * 25) % 140 + cx - 70);
            g2.drawLine(lx, cy + 52, lx, cy + 58);
        }

        // Running stride — alternating leg and arm positions
        int stride = (int) (25 * Math.sin(animPhase * 2 * Math.PI));
        drawNeonHead(g2, cx + 15, cy - 55);
        drawNeonTorso(g2, cx - 10, cy - 30, 30, 50, false);
        drawNeonLeg(g2, cx - 10, cy + 20, cx + stride, cy + 35, cx + stride / 2, cy + 55);
        drawNeonArm(g2, cx + 5, cy - 20, cx - stride / 2, cy - 10, cx - stride, cy - 30);

        drawMuscleGlow(g2, cx + 5, cy + 25, 24, 18);
        drawMotionTrail(g2, cx - 35, cy + 20, cx + 35, cy + 20, t, "Stride");
    }

    // ══════════════════════════════════════════════════
    //  NEON BODY PART RENDERERS
    // ══════════════════════════════════════════════════

    private void drawNeonHead(Graphics2D g2, int hx, int hy) {
        // Outer glow
        g2.setColor(new Color(NEON_BLUE.getRed(), NEON_BLUE.getGreen(), NEON_BLUE.getBlue(), 30));
        g2.fillOval(hx - 17, hy - 17, 34, 34);
        // Head fill
        g2.setColor(BODY_FILL);
        g2.fillOval(hx - 13, hy - 13, 26, 26);
        // Hair
        g2.setColor(new Color(51, 65, 85));
        g2.fillArc(hx - 13, hy - 13, 26, 20, 0, 180);
        // Neon outline
        g2.setColor(BODY_OUTLINE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(hx - 13, hy - 13, 26, 26);
    }

    private void drawNeonTorso(Graphics2D g2, int x, int y, int w, int h, boolean lying) {
        // Outer glow
        g2.setColor(new Color(NEON_BLUE.getRed(), NEON_BLUE.getGreen(), NEON_BLUE.getBlue(), 20));
        g2.fillRoundRect(x - 3, y - 3, w + 6, h + 6, 20, 20);
        // Torso
        RoundRectangle2D.Float torso = new RoundRectangle2D.Float(x, y, w, h, 16, 16);
        g2.setColor(BODY_FILL);
        g2.fill(torso);
        // Center line
        g2.setColor(new Color(148, 163, 184, 100));
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(x + w / 2, y + 4, x + w / 2, y + h - 8);
        // Neon outline
        g2.setColor(BODY_OUTLINE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(torso);
    }

    private void drawNeonArm(Graphics2D g2, int sx, int sy, int ex, int ey, int hx, int hy) {
        // Glow
        g2.setColor(new Color(NEON_BLUE.getRed(), NEON_BLUE.getGreen(), NEON_BLUE.getBlue(), 25));
        g2.setStroke(new BasicStroke(14f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(sx, sy, ex, ey);
        g2.setStroke(new BasicStroke(12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(ex, ey, hx, hy);
        // Upper arm
        g2.setColor(BODY_FILL);
        g2.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(sx, sy, ex, ey);
        // Forearm
        g2.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(ex, ey, hx, hy);
        // Joint
        g2.setColor(BODY_OUTLINE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(ex - 5, ey - 5, 10, 10);
        // Shoulder joint
        g2.drawOval(sx - 4, sy - 4, 8, 8);
    }

    private void drawNeonLeg(Graphics2D g2, int hx, int hy, int kx, int ky, int ax, int ay) {
        // Glow
        g2.setColor(new Color(NEON_BLUE.getRed(), NEON_BLUE.getGreen(), NEON_BLUE.getBlue(), 25));
        g2.setStroke(new BasicStroke(16f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(hx, hy, kx, ky);
        g2.setStroke(new BasicStroke(13f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(kx, ky, ax, ay);
        // Thigh
        g2.setColor(BODY_FILL);
        g2.setStroke(new BasicStroke(12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(hx, hy, kx, ky);
        // Shin
        g2.setStroke(new BasicStroke(9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(kx, ky, ax, ay);
        // Knee joint
        g2.setColor(BODY_OUTLINE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(kx - 5, ky - 5, 10, 10);
    }

    // ══════════════════════════════════════════════════
    //  EFFECTS & OVERLAYS
    // ══════════════════════════════════════════════════

    private void drawMuscleGlow(Graphics2D g2, int x, int y, int mw, int mh) {
        float pulse = ease(glowPulse);
        int expand = (int) (6 * pulse);
        // Outer glow ring
        g2.setColor(new Color(NEON_RED.getRed(), NEON_RED.getGreen(), NEON_RED.getBlue(), (int) (40 + 50 * pulse)));
        g2.fillOval(x - expand - 6, y - expand - 6, mw + (expand + 6) * 2, mh + (expand + 6) * 2);
        // Middle glow
        g2.setColor(new Color(NEON_RED.getRed(), NEON_RED.getGreen(), NEON_RED.getBlue(), (int) (80 + 100 * pulse)));
        g2.fillOval(x - expand, y - expand, mw + expand * 2, mh + expand * 2);
        // Core highlight
        g2.setColor(new Color(255, 120, 120, (int) (140 + 80 * pulse)));
        g2.fillOval(x + 2, y + 2, mw - 4, mh - 4);
    }

    private void drawMotionTrail(Graphics2D g2, int x1, int y1, int x2, int y2, float t, String label) {
        // Animated dashed trail
        float dashOffset = t * 30;
        g2.setColor(new Color(NEON_GREEN.getRed(), NEON_GREEN.getGreen(), NEON_GREEN.getBlue(), 140));
        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                10f, new float[]{6, 8}, dashOffset));
        g2.drawLine(x1, y1, x2, y2);

        // Arrow head
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int as = 10;
        int ax1 = (int) (x2 - as * Math.cos(angle - Math.PI / 6));
        int ay1 = (int) (y2 - as * Math.sin(angle - Math.PI / 6));
        int ax2 = (int) (x2 - as * Math.cos(angle + Math.PI / 6));
        int ay2 = (int) (y2 - as * Math.sin(angle + Math.PI / 6));
        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x2, y2, ax1, ay1);
        g2.drawLine(x2, y2, ax2, ay2);

        // Label
        g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
        g2.setColor(new Color(NEON_GREEN.getRed(), NEON_GREEN.getGreen(), NEON_GREEN.getBlue(), 180));
        int labelX = (x1 + x2) / 2 + 8;
        int labelY = (y1 + y2) / 2 - 5;
        g2.drawString(label, labelX, labelY);
    }

    private void drawEquipmentLine(Graphics2D g2, int x1, int y1, int x2, int y2) {
        g2.setColor(new Color(71, 85, 105, 200));
        g2.setStroke(new BasicStroke(7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x1, y1, x2, y2);
        // Subtle highlight
        g2.setColor(new Color(100, 120, 150, 60));
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x1, y1, x2, y2);
    }

    private void drawEquipmentRect(Graphics2D g2, int x1, int y1, int x2, int y2, int thickness) {
        g2.setColor(new Color(30, 41, 59));
        g2.fillRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
    }

    private void drawEquipmentBar(Graphics2D g2, int x, int y, int bw, int bh) {
        g2.setColor(new Color(203, 213, 225));
        g2.fillRoundRect(x, y, bw, bh, 4, 4);
        g2.setColor(new Color(148, 163, 184));
        g2.drawRoundRect(x, y, bw, bh, 4, 4);
    }

    private void drawExerciseLabel(Graphics2D g2, int w, int h) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        String label = exerciseName.toUpperCase();
        int labelW = fm.stringWidth(label);

        // Label background pill
        int pillX = (w - labelW) / 2 - 12;
        int pillY = h - 28;
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRoundRect(pillX, pillY, labelW + 24, 20, 10, 10);
        g2.setColor(new Color(NEON_CYAN.getRed(), NEON_CYAN.getGreen(), NEON_CYAN.getBlue(), 100));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(pillX, pillY, labelW + 24, 20, 10, 10);

        // Label text
        g2.setColor(NEON_CYAN);
        g2.drawString(label, (w - labelW) / 2, h - 14);
    }

    // ══════════════════════════════════════════════════
    //  BACKGROUND FX
    // ══════════════════════════════════════════════════

    private void drawHexGrid(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(255, 255, 255, 8));
        g2.setStroke(new BasicStroke(0.5f));
        int size = 28;
        float rowH = size * 1.732f;
        for (int row = -1; row * rowH < h + size; row++) {
            for (int col = -1; col * size * 3 < w + size * 3; col++) {
                float x = col * size * 3 + (row % 2) * size * 1.5f;
                float y = row * rowH;
                drawHex(g2, x, y, size);
            }
        }
    }

    private void drawHex(Graphics2D g2, float cx, float cy, int size) {
        Path2D hex = new Path2D.Float();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i - 30);
            float px = cx + (float) (size * Math.cos(angle));
            float py = cy + (float) (size * Math.sin(angle));
            if (i == 0) hex.moveTo(px, py); else hex.lineTo(px, py);
        }
        hex.closePath();
        g2.draw(hex);
    }

    private void drawScanLine(Graphics2D g2, int w, int h) {
        int sy = (int) hudScanY;
        GradientPaint scanGrad = new GradientPaint(0, sy - 15, new Color(0, 255, 255, 0),
                0, sy, new Color(0, 255, 255, 25),
                true);
        g2.setPaint(scanGrad);
        g2.fillRect(0, sy - 15, w, 30);
    }

    private void drawHUDFrame(Graphics2D g2, int w, int h) {
        int size = 20;
        g2.setColor(new Color(NEON_CYAN.getRed(), NEON_CYAN.getGreen(), NEON_CYAN.getBlue(), 80));
        g2.setStroke(new BasicStroke(2f));
        // Top-left
        g2.drawLine(4, 4, 4, 4 + size);
        g2.drawLine(4, 4, 4 + size, 4);
        // Top-right
        g2.drawLine(w - 5, 4, w - 5, 4 + size);
        g2.drawLine(w - 5, 4, w - 5 - size, 4);
        // Bottom-left
        g2.drawLine(4, h - 5, 4, h - 5 - size);
        g2.drawLine(4, h - 5, 4 + size, h - 5);
        // Bottom-right
        g2.drawLine(w - 5, h - 5, w - 5, h - 5 - size);
        g2.drawLine(w - 5, h - 5, w - 5 - size, h - 5);

        // Small data readout top-right
        g2.setFont(new Font("Consolas", Font.PLAIN, 9));
        g2.setColor(new Color(NEON_CYAN.getRed(), NEON_CYAN.getGreen(), NEON_CYAN.getBlue(), 100));
        g2.drawString("REPSYNC // BIOMECH", w - 145, 16);
        g2.drawString(String.format("PHASE: %.0f%%", animPhase * 100), w - 100, 28);
    }

    // ══════════════════════════════════════════════════
    //  FLOATING PARTICLE SYSTEM
    // ══════════════════════════════════════════════════

    private static class Particle {
        float x, y, vx, vy, size;
        int alpha;
        Color color;

        Particle(Random rng) {
            reset(rng, 440, 280, true);
        }

        void reset(Random rng, int w, int h, boolean randomPos) {
            x = randomPos ? rng.nextFloat() * w : (rng.nextBoolean() ? -5 : w + 5);
            y = rng.nextFloat() * h;
            vx = (rng.nextFloat() - 0.5f) * 0.8f;
            vy = (rng.nextFloat() - 0.5f) * 0.6f;
            size = 1.5f + rng.nextFloat() * 2.5f;
            alpha = 30 + rng.nextInt(60);
            color = rng.nextBoolean() ? NEON_CYAN : NEON_BLUE;
        }

        void update(int w, int h) {
            x += vx;
            y += vy;
            if (x < -10 || x > w + 10 || y < -10 || y > h + 10) {
                reset(new Random(), w > 0 ? w : 440, h > 0 ? h : 280, false);
            }
        }

        void draw(Graphics2D g2) {
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
            g2.fillOval((int) x, (int) y, (int) size, (int) size);
        }
    }
}
