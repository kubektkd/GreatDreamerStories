package atomiccode.greatDreamerStories.character;

/**
 * Call of Cthulhu 7th edition derived values from characteristics (percentile scale).
 */
public final class CthulhuDerivedStats {

    /** Lower inclusive bound for each STR+SIZ damage tier (7e investigator rules). */
    private static final int[] STR_SIZ_TIER_MIN = {
            2, 65, 85, 125, 165, 205, 285, 365, 445, 525, 605, 685, 766, 846
    };
    private static final String[] DAMAGE_BONUS = {
            "-2", "-1", "0", "+1d4", "+1d6", "+1d8", "+1d10",
            "+2d6", "+2d8", "+2d10", "+3d6", "+3d8", "+3d10"
    };
    private static final int[] BUILD = {
            -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
    };

    private CthulhuDerivedStats() {
    }

    public static int maxHitPoints(Character c) {
        return maxHitPoints(c.getConstitution(), c.getSize());
    }

    public static int maxHitPoints(int constitution, int size) {
        return (constitution + size) / 10;
    }

    public static int maxMagicPoints(Character c) {
        return maxMagicPoints(c.getPower());
    }

    public static int maxMagicPoints(int power) {
        return power / 5;
    }

    /** Maximum (and typical starting) Sanity equals POW in 7e. */
    public static int maxSanityPoints(Character c) {
        return maxSanityPoints(c.getPower());
    }

    public static int maxSanityPoints(int power) {
        return power;
    }

    /**
     * Movement rate from STR, DEX, SIZ, and age (7e): base 7/8/9 then age modifiers.
     */
    public static int moveRate(Character c) {
        return moveRate(c.getStrength(), c.getDexterity(), c.getSize(), c.getAge());
    }

    public static int moveRate(int strength, int dexterity, int size, int age) {
        int base = baseMoveRate(strength, dexterity, size);
        return Math.max(1, base + ageMoveRateModifier(age));
    }

    private static int baseMoveRate(int strength, int dexterity, int size) {
        boolean strBelow = strength < size;
        boolean dexBelow = dexterity < size;
        if (strBelow && dexBelow) {
            return 7;
        }
        if (!strBelow && !dexBelow) {
            return 9;
        }
        return 8;
    }

    private static int ageMoveRateModifier(int age) {
        if (age <= 19) {
            return 1;
        }
        if (age <= 39) {
            return 0;
        }
        if (age <= 49) {
            return -1;
        }
        if (age <= 59) {
            return -2;
        }
        if (age <= 69) {
            return -3;
        }
        if (age <= 79) {
            return -4;
        }
        if (age <= 89) {
            return -5;
        }
        return -6;
    }

    public static String damageBonus(Character c) {
        return damageBonus(c.getStrength(), c.getSize());
    }

    public static String damageBonus(int strength, int size) {
        int sum = strength + size;
        int tier = damageTierIndex(sum);
        if (tier < DAMAGE_BONUS.length) {
            return DAMAGE_BONUS[tier];
        }
        return "+4d6";
    }

    public static int build(Character c) {
        return build(c.getStrength(), c.getSize());
    }

    public static int build(int strength, int size) {
        int sum = strength + size;
        int tier = damageTierIndex(sum);
        if (tier < BUILD.length) {
            return BUILD[tier];
        }
        return 11;
    }

    private static int damageTierIndex(int strSizSum) {
        int best = 0;
        for (int i = 0; i < STR_SIZ_TIER_MIN.length; i++) {
            if (strSizSum >= STR_SIZ_TIER_MIN[i]) {
                best = i;
            }
        }
        return best;
    }
}
