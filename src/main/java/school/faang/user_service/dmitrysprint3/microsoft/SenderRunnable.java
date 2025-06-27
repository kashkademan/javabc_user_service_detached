package school.faang.user_service.dmitrysprint3.microsoft;

public class SenderRunnable implements Runnable{
    int startIndex;

    int endIndex;

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public SenderRunnable() {
    }

    public SenderRunnable(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public void run() {

            try {

                      System.out.println("Mail has been sent");
                Thread.sleep(100); //
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



