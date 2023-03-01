package fr.moribus.imageonmap.util;

public final class StringUtils {
    private static final int LIMIT_PER_LINE = 16;

    /**
     * Utility to convert regular string to Sign-editable
     *
     * @param string String that needs to be converted
     * @return Array of strings for SignGUI
     */
    public static String[] toSign(String string) {
        // Split string into words
        String[] words = string.split("\\s+");
        // Index when looping through words
        int i = 0;

        // Line position in sign
        int pos = 0;
        // Result
        String[] sign = new String[4];

        do {
            // Null check all strings, initiate if true
            if (sign[pos] == null)
                sign[pos] = "";
            // If line length is 0, put words instantly
            if (sign[pos].length() == 0) {
                sign[pos] = words[i];
            }
            // If total words is more than 1, append next word
            if (words.length > 1) {
                // If current word + next word + extra space is less than LIMIT_PER_LINE, put into single line
                // Else put into new line
                if (sign[pos].length() + 1 + words[i + 1].length() <= LIMIT_PER_LINE) {
                    sign[pos] = sign[pos] + " " + words[i + 1];
                } else {
                    if (pos <= 2) pos++;
                    if (i == words.length - 2)
                        sign[pos] = words[i + 1];
                }
            }
            // Increment index
            i++;
        } while (i < words.length - 1);

        return sign;
    }

    /**
     * Utility to convert array of strings from Sign to regular string
     *
     * @param strings Array of strings
     * @return String
     */
    public static String toString(String[] strings) {
        StringBuilder result = new StringBuilder();
        for (String string : strings) {
            result.append(string);
            if (result.length() > 0)
                result.append(" ");
        }
        return String.valueOf(result);
    }
}
