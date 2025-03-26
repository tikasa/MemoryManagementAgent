import java.io.IOException;
import java.util.logging.*;

public class MemoryMonitorLogger {
    private static final Logger logger = Logger.getLogger(MemoryMonitorLogger.class.getName());

    static {
        try {
            // Määritetään lokitiedoston nimi ja sijainti, maksimikoko ja varakopioiden määrä
            FileHandler fileHandler = new FileHandler("C:/iSuite/logs/memory_monitor.log", 1024 * 1024, 2, true);
            fileHandler.setFormatter(new SimpleFormatter()); // Yksinkertainen muotoilu
            logger.addHandler(fileHandler);
            logger.setLevel(Level.INFO); // INFO-tason logitus
        } catch (IOException e) {
            System.err.println("Logger initialization failed: " + e.getMessage());
        }
    }

    public static Logger getLogger() {
        return logger;
    }
}