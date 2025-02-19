import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Properties; 
import javax.mail.*;
import javax.mail.internet.*;

public class MemoryMonitorAgent {
    
    private static Thread uusiSaie;
    // Tämä metodi käynnistyy, kun agentti liitetään JVM:ään
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("Memory Monitor Agent started...");

    
        // Käynnistä uusi säie, joka valvoo muistinkäyttöä
        uusiSaie = new Thread(() -> {

            try {
                //voi poistaa lopuksi
                System.out.println("Memory monitoring thread started...");
   
                 MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
                 while (true) {
                    System.out.println("Memory monitoring loop running...");
                    MemoryUsage heapMemory = memoryMXBean.getHeapMemoryUsage();
                    long maxMemory = heapMemory.getMax();
                    long usedMemory = heapMemory.getUsed();
                    long freeMemory = maxMemory - usedMemory;

                    System.out.println("Heap Memory Usage: Used = " + (usedMemory / (1024 * 1024)) + " MB, Free = " + (freeMemory / (1024 * 1024)) + " MB, Max = " + (maxMemory / (1024 * 1024)) + " MB");


                    if (freeMemory < 3686 * 1024 * 1024) { // 3686 MB raja
                        System.out.println("Warning: Low memory! Free memory: " + (freeMemory / (1024 * 1024)) + " MB");
                        sendEmail("kati.sarajarvi@digia.com", "Low Memory Alert", 
                        "Warning: Free memory is low (" + (freeMemory / (1024 * 1024)) + " MB)");
                    }

                    Thread.sleep(10*60*1000); // Tarkista muisti 10min välein
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); 
            }
        });
        uusiSaie.start();
    }
    static void sendEmail(String to, String subject, String body) {
        final String from = "isuite@etra.fi"; // Lähettäjän osoite
        // final String password = ""; Lähettäjän sähköpostin salasana

        Properties props = new Properties();
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "mcx.mpynet.fi"); // Käytä oman palveluntarjoajan SMTP-palvelinta
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

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
