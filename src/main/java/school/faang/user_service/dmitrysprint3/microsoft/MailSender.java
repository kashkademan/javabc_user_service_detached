package school.faang.user_service.dmitrysprint3.microsoft;

public class MailSender {

    public static void main(String[] args) {


SenderRunnable senderRunnable = new SenderRunnable();
         Thread secondThread = new Thread(senderRunnable);
         secondThread.start();
    }
}
