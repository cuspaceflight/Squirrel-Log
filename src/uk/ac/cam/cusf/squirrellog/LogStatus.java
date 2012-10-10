package uk.ac.cam.cusf.squirrellog;

public class LogStatus {

    private static boolean running = false;
    private static long time = System.currentTimeMillis();
    
    public static void setRunning(boolean run) {
        running = run;
        time = System.currentTimeMillis();
    }
    
    public static boolean isRunning() {
        return running;
    }
    
    public static long getTime() {
        return time;
    }
    
}
