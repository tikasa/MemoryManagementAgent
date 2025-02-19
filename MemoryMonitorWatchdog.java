import java.lang.reflect.Field;

public class MemoryMonitorWatchdog{
    private static final int CHECK_INTERVAL = 10 * 60 * 1000; // 10 minutes 

    public static void startWatchdog() {
        Thread watchdogThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(CHECK_INTERVAL); // Wait for the defined interval before checking again
                    // Access the monitoring thread
                    Field threadField = MemoryMonitorAgent.class.getDeclaredField("uusiSaie");
                    threadField.setAccessible(true);
                    Thread monitoringThread = (Thread) threadField.get(null);

                    if(monitoringThread == null || !monitoringThread.isAlive()) {
                        System.out.println("ALERT: MemoryMonitorAgent has stopped running!");
                        MemoryMonitorAgent.sendEmail("kati.sarajarvi@digia.com",
                            "Memory Agent Failure Alert",
                            "Warning: Memory monitoring thread is NOT running!");
                    }else{
                        System.out.println("Memory Monitor Agent is running normally");
                    }

                }catch (Exception e){
                    System.err.println("Error in watchdog: " + e.getMessage());
                }
            }
        });
        watchdogThread.setDaemon(true); // Make it a daemon so it stops when JVM exits
        watchdogThread.start();
    }
}