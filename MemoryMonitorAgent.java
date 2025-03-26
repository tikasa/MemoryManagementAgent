import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.logging.Logger;
import java.util.Properties; 
import javax.mail.*;
import javax.mail.internet.*;

public class MemoryMonitorAgent {
    
    private static final Logger logger = MemoryMonitorLogger.getLogger();
    private static Thread uusiSaie;

    // Tämä metodi käynnistyy, kun agentti liitetään JVM:ään
    public static void premain(String agentArgs, Instrumentation inst) {
        logger.info("Memory Monitor Agent started...");

    
        // Käynnistä uusi säie, joka valvoo muistinkäyttöä
        uusiSaie = new Thread(() -> {

            try {
                //voi poistaa lopuksi
                logger.info("Memory monitoring thread started...");
   
                 MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
                 while (true) {
                    logger.info("Memory monitoring loop running...");
                    MemoryUsage heapMemory = memoryMXBean.getHeapMemoryUsage();
                    long maxMemory = heapMemory.getMax();
                    long usedMemory = heapMemory.getUsed();
                    long freeMemory = maxMemory - usedMemory;

                    logger.info("Heap Memory Usage: Used = " + (usedMemory / (1024 * 1024)) + " MB, Free = " + (freeMemory / (1024 * 1024)) + " MB, Max = " + (maxMemory / (1024 * 1024)) + " MB");


                    if (freeMemory < 150 * 1024 * 1024) { // 3686 MB raja sitten lopulta
                        logger.warning("Warning: Low memory! Free memory: " + (freeMemory / (1024 * 1024)) + " MB");
                        sendEmail("kati.sarajarvi@digia.com", "Low Memory Alert", 
                        "Warning: Free memory is low (" + (freeMemory / (1024 * 1024)) + " MB)");
                    }

                    Thread.sleep(10*1000); // 10min =10*60*1000
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); 
            }
        });
        uusiSaie.start();
        // Start watchdog to monitor the memory monitor itself
        MemoryMonitorWatchdog.startWatchdog();
    }
    static void sendEmail(String to, String subject, String body) {
        final String from = "isuite@etra.fi"; // Lähettäjän osoite
        // final String password = ""; Lähettäjän sähköpostin salasana

        Properties props = new Properties();
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "mcx.mpynet.fi"); // Käytä oman palveluntarjoajan SMTP-palvelinta
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {
            System.out.println("Failed to send email: " + e.getMessage());
        }
    }
}
