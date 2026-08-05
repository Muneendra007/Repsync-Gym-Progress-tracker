package com.repsync.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for hashing and verifying passwords using BCrypt.
 * Never store passwords as plain text!
 */
public class PasswordHasher {

    /**
     * Hash a plain-text password using BCrypt.
     * 
     * @param plainPassword the password to hash
     * @return the BCrypt hash string
     */
    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    /**
     * Verify a plain-text password against a BCrypt hash.
     * 
     * @param plainPassword the password to check
     * @param hashedPassword the stored hash to check against
     * @return true if the password matches
     */
    public static boolean verify(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
