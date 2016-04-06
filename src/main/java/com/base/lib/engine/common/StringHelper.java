package com.base.lib.engine.common;

import java.util.Locale;

public class StringHelper {

    public static final String EMPTY_PLACEHOLDER = "-";

    public static String build(Object... args) {

        if (args == null) {
            return EMPTY_PLACEHOLDER;
        }

        StringBuilder builder = new StringBuilder();
        for (Object arg : args) {
            if (arg != null) {
                builder.append(arg.toString()).append(" ");
            }
        }

        String content = builder.toString();

        if (StringHelper.isEmpty(content)) {
            return EMPTY_PLACEHOLDER;
        } else {
            return content;
        }
    }

    public static String buildStruct(String separator, Object... args) {

        if (args == null) {
            return EMPTY_PLACEHOLDER;
        }

        if (separator == null || args.length < 2) {
            return build(args);
        }

        int subCount = args.length - 1;

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < subCount; i++) {
            if (args[i] != null) {
                builder.append(args[i]).append(separator);
            }
        }

        if (args[subCount] != null) {
            builder.append(args[subCount]);
        } else {
            if (builder.length() > separator.length()) {
                builder.delete(builder.length() - separator.length(), builder.length());
            }
        }

        String content = builder.toString();

        if (StringHelper.isEmpty(content)) {
            return EMPTY_PLACEHOLDER;
        } else {
            return content;
        }
    }

    public static String parseDateFromDB(String dateTime) {

        if (StringHelper.isEmpty(dateTime)) {
            return EMPTY_PLACEHOLDER;
        }

        String[] date = dateTime.split(" ")[0].split("-");

        return String.format(Locale.US, "%s.%s.%s", date[2], date[1], date[0]);
    }

    public static String parseTimeFromDB(String dateTime) {

        if (StringHelper.isEmpty(dateTime)) {
            return EMPTY_PLACEHOLDER;
        }

        String[] time = dateTime.split(" ")[1].split(":");

        return String.format(Locale.US, "%s:%s", time[0], time[1]);
    }

    public static boolean isNullTime(String time) {

        return time == null || time.equals("00:00");
    }

    public static boolean isEmpty(String o) {

        return o == null || o.isEmpty();
    }

    public static boolean isEmpty(Object o) {

        return o == null || isEmpty(o.toString());
    }
}
