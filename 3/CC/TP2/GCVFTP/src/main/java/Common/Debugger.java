package Common;

public class Debugger {
    private static boolean enabled = false;
    private static boolean appLevel = false;

    public static void setEnabled(boolean enabled) {
        Debugger.enabled = enabled;
    }

    public static void log(String str) {
        if(enabled)
            System.out.println(str);
    }

    public static void logAppLevel(String str) {
        if(appLevel)
            System.out.println(str);
    }
}
