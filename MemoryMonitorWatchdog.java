import java.lang.reflect.Field;
import java.util.logging.Logger;

public class MemoryMonitorWatchdog{
    private static final int CHECK_INTERVAL = 20 * 1000; // 10 minutes = 10*60*1000
    private static final Logger logger = MemoryMonitorLogger.getLogger();

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
                        logger.warning("ALERT: MemoryMonitorAgent has stopped running!");
                        MemoryMonitorAgent.sendEmail("kati.sarajarvi@digia.com",
                            "Memory Agent Failure Alert",
                            "Warning: Memory monitoring thread is NOT running!");
                    }else{
                        logger.info("Memory Monitor Agent is running normally");
                    }

                }catch (Exception e){
                    logger.info("Error in watchdog: " + e.getMessage());
                }
            }
        });
        watchdogThread.setDaemon(true); // Make it a daemon so it stops when JVM exits
        watchdogThread.start();
    }
}