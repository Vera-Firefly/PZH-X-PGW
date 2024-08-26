package android.util;

import android.util.Log;

public class Log {
    public Log() {
        super();
    }
    public static int i(String tag, String message) {
        System.out.println(tag + ": " + message);
        return 0;
    }
}
