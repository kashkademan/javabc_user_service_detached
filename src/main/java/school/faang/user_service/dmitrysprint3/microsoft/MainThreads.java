package school.faang.user_service.dmitrysprint3.microsoft;

public class MainThreads {
    public static void main(String[] args) {
        Thread workerThread = new Thread(() -> {
            try {
                System.out.println("Thread started");
                Thread.sleep(10000); // Имитируем работу на 10 секунд
                System.out.println("Main thread finished.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Interrupted in sleep mode.");
            }
        });

        System.out.println("Main thread");
        workerThread.start();

        System.out.println("No more 5 seconds");
        try {
            workerThread.join(3000); // Ждем максимум 5000 мс (5 секунд)

            if (workerThread.isAlive()) {
                // Если мы здесь, значит, 5 секунд прошло, а поток еще жив
                System.out.println("Mot finished");
                // Здесь можно предпринять альтернативные действия,
                // например, попытаться прервать рабочий поток: workerThread.interrupt();
            } else {
                // Если мы здесь, значит, поток успел завершиться за 5 секунд
                System.out.println("Finished.");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Waiting was interrupted.");
        }

        System.out.println("Proceed working.");

    }
}
