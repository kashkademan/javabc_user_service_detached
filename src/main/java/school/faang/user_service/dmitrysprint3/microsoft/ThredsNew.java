package school.faang.user_service.dmitrysprint3.microsoft;

import java.util.concurrent.atomic.AtomicInteger;

public class ThredsNew  {
    public static void main(String[] args) {
        AtomicInteger sum = new AtomicInteger();
        Thread calculateThread = new Thread(() -> {
            try {
                System.out.println("thread 1");
                Thread.sleep(1);
                while(sum.get() <100){
                    sum.getAndIncrement();
                    System.out.println("1");
                }
            }
            catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        });
        Thread calculateThread2 = new Thread(() -> {
            try {
                System.out.println("thread 2");
                Thread.sleep(2);
                while(sum.get() <100){
                    sum.getAndIncrement();
                    sum.getAndIncrement();
                    sum.getAndIncrement();
                    System.out.println("2");
                }
            }
            catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        });
        calculateThread.start();
        calculateThread2.start();
        try {
            calculateThread.join(1000);
            calculateThread2.join(1000);
            if(sum.get()==96){

                calculateThread2.interrupt();
                System.out.println("i");
            }

            if (calculateThread.isAlive()) {
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

        System.out.println(sum);
    }
}
