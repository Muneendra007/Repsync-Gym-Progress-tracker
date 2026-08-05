package com.repsync.database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * Creates the repsync database and all tables on first run.
 * Also seeds default exercises so the app is ready to use immediately.
 */
public class SchemaInitializer {

    /**
     * Initialize the database: create it if it doesn't exist,
     * then create all tables, then seed default exercises.
     */
    public static void initialize() {
        createDatabase();
        createTables();
        seedDefaultExercises();
        seedDefaultUsers();
        System.out.println("Database initialized successfully!");
    }

    /**
     * Create the repsync database if it doesn't exist.
     */
    private static void createDatabase() {
        try (Connection conn = DatabaseConnection.getInstance().getServerConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS RepSync_db");
            System.out.println("Database 'RepSync_db' is ready.");

        } catch (SQLException e) {
            // Pre-existing cloud databases like Aiven defaultdb don't allow CREATE DATABASE - safe to skip
        }
    }

    /**
     * Create all 7 tables if they don't already exist.
     */
    private static void createTables() {
        String[] tableSQL = {
            // 1. Users table
            """
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(100) UNIQUE NOT NULL,
                role ENUM('USER', 'ADMIN', 'TRAINER') DEFAULT 'USER',
                age INT,
                gender ENUM('MALE', 'FEMALE', 'OTHER'),
                height_cm DOUBLE,
                weight_kg DOUBLE,
                fitness_goal ENUM('STRENGTH', 'MUSCLE_GAIN', 'FAT_LOSS', 'ENDURANCE'),
                experience_level ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED'),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """,

            // 2. Exercises table
            """
            CREATE TABLE IF NOT EXISTS exercises (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                exercise_type ENUM('STRENGTH', 'CARDIO') NOT NULL,
                muscle_group VARCHAR(50),
                equipment VARCHAR(50),
                difficulty ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED'),
                description TEXT,
                default_sets INT DEFAULT 3,
                default_reps INT DEFAULT 10,
                default_weight_kg DOUBLE DEFAULT 0,
                default_duration_seconds INT DEFAULT 0
            )
            """,

            // 3. Workout Plans table
            """
            CREATE TABLE IF NOT EXISTS workout_plans (
                id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                plan_name VARCHAR(100),
                fitness_goal ENUM('STRENGTH', 'MUSCLE_GAIN', 'FAT_LOSS', 'ENDURANCE'),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            )
            """,

            // 4. Workout Sessions table
            """
            CREATE TABLE IF NOT EXISTS workout_sessions (
                id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                plan_id INT,
                session_date DATE NOT NULL,
                duration_minutes INT,
                notes TEXT,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY (plan_id) REFERENCES workout_plans(id) ON DELETE SET NULL
            )
            """,

            // 5. Workout Exercises (exercises logged in a session)
            """
            CREATE TABLE IF NOT EXISTS workout_exercises (
                id INT AUTO_INCREMENT PRIMARY KEY,
                session_id INT NOT NULL,
                exercise_id INT NOT NULL,
                sets INT,
                reps INT,
                weight_kg DOUBLE,
                duration_seconds INT,
                FOREIGN KEY (session_id) REFERENCES workout_sessions(id) ON DELETE CASCADE,
                FOREIGN KEY (exercise_id) REFERENCES exercises(id) ON DELETE CASCADE
            )
            """,

            // 6. Personal Records
            """
            CREATE TABLE IF NOT EXISTS personal_records (
                id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                exercise_id INT NOT NULL,
                record_value DOUBLE NOT NULL,
                record_type ENUM('MAX_WEIGHT', 'MAX_REPS', 'MAX_DURATION'),
                achieved_date DATE NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY (exercise_id) REFERENCES exercises(id) ON DELETE CASCADE
            )
            """,

            // 7. Body Progress
            """
            CREATE TABLE IF NOT EXISTS body_progress (
                id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                weight_kg DOUBLE,
                bmi DOUBLE,
                record_date DATE NOT NULL,
                notes TEXT,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            )
            """
        };

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {

            for (String sql : tableSQL) {
                stmt.executeUpdate(sql);
            }
            System.out.println("All tables created successfully.");

            // Migration: add experience_level column if it doesn't exist (for existing DBs)
            try {
                stmt.executeUpdate("ALTER TABLE users ADD COLUMN experience_level ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED') AFTER fitness_goal");
                System.out.println("Migration: added experience_level column.");
            } catch (SQLException ignored) {
                // Column already exists — safe to ignore
            }

        } catch (SQLException e) {
            System.err.println("ERROR creating tables: " + e.getMessage());
        }
    }

    /**
     * Seed default exercises so the app has data to work with.
     * Uses INSERT IGNORE to avoid duplicates on re-run.
     */
    private static void seedDefaultExercises() {
        String[] exercises = {
            // Strength exercises - Chest (Upper, Middle, Lower, Inner/Outer)
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Bench Press', 'STRENGTH', 'CHEST', 'BARBELL', 'INTERMEDIATE', 'Lie on bench, press barbell up from chest', 4, 8, 60)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Incline Dumbbell Press', 'STRENGTH', 'CHEST', 'DUMBBELL', 'INTERMEDIATE', 'Press dumbbells on incline bench', 3, 10, 20)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Decline Bench Press', 'STRENGTH', 'CHEST', 'BARBELL', 'INTERMEDIATE', 'Press barbell from decline angle targeting lower chest', 3, 10, 55)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Cable Chest Fly', 'STRENGTH', 'CHEST', 'MACHINE', 'INTERMEDIATE', 'Perform standing flyes on dual cable pulley machine', 3, 12, 15)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Chest Dips', 'STRENGTH', 'CHEST', 'BODYWEIGHT', 'ADVANCED', 'Dip body on parallel bars with forward torso lean', 3, 10, 0)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Push Ups', 'STRENGTH', 'CHEST', 'BODYWEIGHT', 'BEGINNER', 'Classic bodyweight push up', 3, 15, 0)",

            // Strength exercises - Back (Upper, Lats, Rhomboids/Traps, Lower)
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Deadlift', 'STRENGTH', 'BACK', 'BARBELL', 'ADVANCED', 'Lift barbell from ground to hip level', 4, 5, 100)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Barbell Row', 'STRENGTH', 'BACK', 'BARBELL', 'INTERMEDIATE', 'Bend over and row barbell to chest', 4, 8, 50)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Seated Cable Row', 'STRENGTH', 'BACK', 'MACHINE', 'BEGINNER', 'Row low pulley cable handle to lower ribs', 3, 12, 45)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Pull Ups', 'STRENGTH', 'BACK', 'BODYWEIGHT', 'INTERMEDIATE', 'Pull body up to bar', 3, 8, 0)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Lat Pulldown', 'STRENGTH', 'BACK', 'MACHINE', 'BEGINNER', 'Pull cable bar down to chest', 3, 12, 40)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Face Pulls', 'STRENGTH', 'BACK', 'MACHINE', 'BEGINNER', 'Pull rope attachment towards face targeting rear delts and traps', 3, 15, 20)",

            // Strength exercises - Legs (Quads, Hamstrings, Glutes, Calves)
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Barbell Squat', 'STRENGTH', 'LEGS', 'BARBELL', 'INTERMEDIATE', 'Squat with barbell on shoulders', 4, 8, 80)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Leg Press', 'STRENGTH', 'LEGS', 'MACHINE', 'BEGINNER', 'Press weight away on leg press machine', 3, 12, 100)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Leg Extension', 'STRENGTH', 'LEGS', 'MACHINE', 'BEGINNER', 'Extend legs on seated quad machine', 3, 15, 40)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Romanian Deadlift', 'STRENGTH', 'LEGS', 'BARBELL', 'INTERMEDIATE', 'Hinge at hips with barbell', 3, 10, 60)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Lying Leg Curl', 'STRENGTH', 'LEGS', 'MACHINE', 'BEGINNER', 'Curl legs upward on lying hamstring bench', 3, 12, 35)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Barbell Hip Thrust', 'STRENGTH', 'LEGS', 'BARBELL', 'INTERMEDIATE', 'Drive hips upward with barbell across pelvis', 3, 10, 70)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Standing Calf Raises', 'STRENGTH', 'LEGS', 'MACHINE', 'BEGINNER', 'Raise heels upward on calf machine', 3, 15, 50)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Lunges', 'STRENGTH', 'LEGS', 'DUMBBELL', 'BEGINNER', 'Step forward into lunge position', 3, 12, 15)",

            // Strength exercises - Shoulders (Anterior, Lateral, Posterior Delts)
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Overhead Press', 'STRENGTH', 'SHOULDERS', 'BARBELL', 'INTERMEDIATE', 'Press barbell overhead from shoulders', 4, 8, 40)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Dumbbell Shoulder Press', 'STRENGTH', 'SHOULDERS', 'DUMBBELL', 'BEGINNER', 'Press dumbbells overhead from seated bench', 3, 10, 18)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Lateral Raises', 'STRENGTH', 'SHOULDERS', 'DUMBBELL', 'BEGINNER', 'Raise dumbbells to sides', 3, 15, 8)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Rear Delt Cable Fly', 'STRENGTH', 'SHOULDERS', 'MACHINE', 'INTERMEDIATE', 'Perform high cable reverse fly targeting posterior deltoid', 3, 15, 10)",

            // Strength exercises - Arms (Tricep Long, Lateral, Medial Heads & Bicep Long/Short Heads)
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('EZ-Bar Skull Crushers', 'STRENGTH', 'ARMS', 'BARBELL', 'INTERMEDIATE', 'Lower EZ-bar toward forehead on flat bench targeting long head', 3, 10, 25)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Cable Tricep Pushdown', 'STRENGTH', 'ARMS', 'MACHINE', 'BEGINNER', 'Push cable bar down targeting lateral tricep head', 3, 12, 25)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Rope Pushdown', 'STRENGTH', 'ARMS', 'MACHINE', 'BEGINNER', 'Spread rope attachment at lockout targeting medial tricep head', 3, 12, 20)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Overhead Dumbbell Extension', 'STRENGTH', 'ARMS', 'DUMBBELL', 'INTERMEDIATE', 'Extend dumbbell behind head targeting tricep long head stretch', 3, 12, 16)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Incline Dumbbell Curl', 'STRENGTH', 'ARMS', 'DUMBBELL', 'INTERMEDIATE', 'Curl dumbbells on 45 degree incline bench targeting bicep long head', 3, 10, 12)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Preacher Curl', 'STRENGTH', 'ARMS', 'BARBELL', 'INTERMEDIATE', 'Curl EZ-bar on preacher pad targeting short head and lower bicep', 3, 10, 20)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Barbell Curl', 'STRENGTH', 'ARMS', 'BARBELL', 'BEGINNER', 'Curl barbell up to shoulders', 3, 12, 25)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Tricep Dips', 'STRENGTH', 'ARMS', 'BODYWEIGHT', 'INTERMEDIATE', 'Dip body between parallel bars', 3, 10, 0)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Hammer Curls', 'STRENGTH', 'ARMS', 'DUMBBELL', 'BEGINNER', 'Curl dumbbells with neutral grip', 3, 12, 12)",

            // Strength exercises - Core
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Plank', 'STRENGTH', 'CORE', 'BODYWEIGHT', 'BEGINNER', 'Hold plank position', 3, 1, 0)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_weight_kg) VALUES ('Cable Crunches', 'STRENGTH', 'CORE', 'MACHINE', 'INTERMEDIATE', 'Crunch with cable resistance', 3, 15, 30)",

            // Cardio exercises
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_duration_seconds) VALUES ('Treadmill Running', 'CARDIO', 'CARDIO', 'MACHINE', 'BEGINNER', 'Run on treadmill at steady pace', 1, 1, 1800)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_duration_seconds) VALUES ('Jump Rope', 'CARDIO', 'CARDIO', 'BODYWEIGHT', 'BEGINNER', 'Skip rope at moderate intensity', 3, 1, 180)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_duration_seconds) VALUES ('Cycling', 'CARDIO', 'CARDIO', 'MACHINE', 'BEGINNER', 'Stationary bike cardio', 1, 1, 1800)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_duration_seconds) VALUES ('Burpees', 'CARDIO', 'CARDIO', 'BODYWEIGHT', 'ADVANCED', 'Full body cardio exercise', 3, 15, 0)",
            "INSERT IGNORE INTO exercises (name, exercise_type, muscle_group, equipment, difficulty, description, default_sets, default_reps, default_duration_seconds) VALUES ('Mountain Climbers', 'CARDIO', 'CARDIO', 'BODYWEIGHT', 'INTERMEDIATE', 'Alternate knee drives in plank', 3, 20, 0)",
        };

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {

            for (String sql : exercises) {
                stmt.executeUpdate(sql);
            }
            System.out.println("Default exercises seeded.");

        } catch (SQLException e) {
            // Ignore duplicate errors - exercises already exist
            if (!e.getMessage().contains("Duplicate")) {
                System.err.println("ERROR seeding exercises: " + e.getMessage());
            }
        }
    }

    /**
     * Seed default demo users so 1-click quick login buttons work immediately.
     */
    private static void seedDefaultUsers() {
        String userHash = com.repsync.util.PasswordHasher.hash("123456");
        String adminHash = com.repsync.util.PasswordHasher.hash("admin123");

        String sqlUser = String.format(
            "INSERT IGNORE INTO users (username, password, email, role, age, gender, height_cm, weight_kg, fitness_goal, experience_level) " +
            "VALUES ('hello', '%s', 'hello@repsync.com', 'USER', 26, 'MALE', 178.0, 75.0, 'MUSCLE_GAIN', 'INTERMEDIATE')",
            userHash
        );
        String sqlAdmin = String.format(
            "INSERT IGNORE INTO users (username, password, email, role, age, gender, height_cm, weight_kg, fitness_goal, experience_level) " +
            "VALUES ('admin', '%s', 'admin@repsync.com', 'ADMIN', 30, 'MALE', 182.0, 82.0, 'STRENGTH', 'ADVANCED')",
            adminHash
        );

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sqlUser);
            stmt.executeUpdate(sqlAdmin);
            System.out.println("Default demo users ('hello' & 'admin') seeded.");

        } catch (SQLException e) {
            System.err.println("ERROR seeding default users: " + e.getMessage());
        }
    }
}
