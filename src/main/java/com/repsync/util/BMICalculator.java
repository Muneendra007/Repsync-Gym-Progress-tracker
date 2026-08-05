package com.repsync.util;

/**
 * Utility class for BMI and BMR calculations.
 * 
 * BMI = Body Mass Index (weight in kg / height in meters squared)
 * BMR = Basal Metabolic Rate (calories burned at rest per day)
 */
public class BMICalculator {

    /**
     * Calculate BMI (Body Mass Index).
     * 
     * @param weightKg body weight in kilograms
     * @param heightCm height in centimeters
     * @return BMI value, or 0 if inputs are invalid
     */
    public static double calculateBMI(double weightKg, double heightCm) {
        if (weightKg <= 0 || heightCm <= 0) {
            return 0;
        }
        double heightM = heightCm / 100.0;  // Convert cm to meters
        return weightKg / (heightM * heightM);
    }

    /**
     * Get the BMI category based on the BMI value.
     * 
     * @param bmi the BMI value
     * @return category string (Underweight, Normal, Overweight, Obese)
     */
    public static String getBMICategory(double bmi) {
        if (bmi <= 0) return "N/A";
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25.0) return "Normal Weight";
        if (bmi < 30.0) return "Overweight";
        return "Obese";
    }

    /**
     * Calculate BMR using the Harris-Benedict Equation.
     * BMR = calories your body burns at rest per day.
     * 
     * @param weightKg body weight in kg
     * @param heightCm height in cm
     * @param age age in years
     * @param gender "MALE" or "FEMALE"
     * @return BMR in calories per day, or 0 if inputs are invalid
     */
    public static double calculateBMR(double weightKg, double heightCm, int age, String gender) {
        if (weightKg <= 0 || heightCm <= 0 || age <= 0) {
            return 0;
        }

        if ("MALE".equalsIgnoreCase(gender)) {
            // Men: BMR = 88.362 + (13.397 × weight) + (4.799 × height) - (5.677 × age)
            return 88.362 + (13.397 * weightKg) + (4.799 * heightCm) - (5.677 * age);
        } else {
            // Women: BMR = 447.593 + (9.247 × weight) + (3.098 × height) - (4.330 × age)
            return 447.593 + (9.247 * weightKg) + (3.098 * heightCm) - (4.330 * age);
        }
    }

    /**
     * Calculate Total Daily Energy Expenditure (TDEE) based on activity level.
     * 
     * @param bmr the base metabolic rate
     * @param activityLevel 1=Sedentary, 2=Light, 3=Moderate, 4=Active, 5=Very Active
     * @return estimated daily calorie needs
     */
    public static double calculateTDEE(double bmr, int activityLevel) {
        double multiplier = switch (activityLevel) {
            case 1 -> 1.2;    // Sedentary (little/no exercise)
            case 2 -> 1.375;  // Lightly active (1-3 days/week)
            case 3 -> 1.55;   // Moderately active (3-5 days/week)
            case 4 -> 1.725;  // Very active (6-7 days/week)
            case 5 -> 1.9;    // Extra active (athlete)
            default -> 1.2;
        };
        return bmr * multiplier;
    }
}
