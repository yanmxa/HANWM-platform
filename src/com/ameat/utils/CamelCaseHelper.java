package com.ameat.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CamelCaseHelper {
	/**
	 * convert a camelcase str to snakecase str eg: camelCaseHelper -> camel_case_helper
	 * @param str
	 * @return
	 */
	public static String camelToSnake(String str) {
        String regexStr = "[A-Z]";
        Matcher matcher = Pattern.compile(regexStr).matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String g = matcher.group();
            matcher.appendReplacement(sb, "_" + g.toLowerCase());
        }
        matcher.appendTail(sb);
        if (sb.charAt(0) == '_') {
            sb.delete(0, 1);
        }

        return sb.toString();
	}

	/**
	 * convert a sakecase str to camelcase str  eg: camel_case_helper -> camelCaseHelper
	 * @param str
	 * @return
	 */
	public static String SnakeToCamel(String str) {
        String regexStr = "_(\\w)";
        Matcher matcher = Pattern.compile(regexStr).matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String g = matcher.group();
            matcher.appendReplacement(sb, g.split("_")[1].toUpperCase());
        }
        matcher.appendTail(sb);

        return sb.toString();
	}
}
