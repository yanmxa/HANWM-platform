package com.ameat.utils;

import java.util.regex.Pattern;

public class TypeHelper {

    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            if (value.contains("."))
                return true;
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNumber(String value) {
        return isInteger(value) || isDouble(value);
    }
    
    public static boolean isDate(String value) {
    		return Pattern.matches("\\d{4}(\\-|\\/|.)\\d{1,2}\\1\\d{1,2}", value);
    }

    public static boolean isDateTime(String value) {
		return Pattern.matches("\\d{4}(\\-|\\/|.)\\d{1,2}\\1\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}(.+)?", value);
    }
}
