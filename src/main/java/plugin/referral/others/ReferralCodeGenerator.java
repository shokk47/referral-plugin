package plugin.referral.others;

import java.util.Random;

/**
 * This class is responsible for generating unique referral codes,
 * consisting of a random combination of uppercase letters and digits.
 * The generated referral code has a fixed length of 8 characters.
 */
public final class ReferralCodeGenerator {

    // The generated referral code
    private final String code;

    /**
     * Constructor for the ReferralCodeGenerator class.
     * Generates a random 8-character alphanumeric referral code.
     * The characters used are uppercase letters (A-Z) and digits (0-9).
     */
    public ReferralCodeGenerator() {
        int length = 8;  // Length of the referral code
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  // Valid characters
        StringBuilder referralCode = new StringBuilder();  // StringBuilder for the code
        Random random = new Random();  // Random number generator

        // Loop to generate 8 random characters
        for (int i = 0; i < length; i++) {
            referralCode.append(characters.charAt(random.nextInt(characters.length())));
        }

        // Assign the generated code to the instance variable
        this.code = referralCode.toString();
    }

    /**
     * Returns the generated referral code.
     *
     * @return The generated referral code, an 8-character alphanumeric string.
     */
    public String getCode() {
        return this.code;
    }
}
