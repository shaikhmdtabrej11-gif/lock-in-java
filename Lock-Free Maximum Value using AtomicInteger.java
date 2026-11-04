import java.util.concurrent.atomic.AtomicInteger;

public class LockFreeMaximum {

    private final AtomicInteger maximum =
            new AtomicInteger(Integer.MIN_VALUE);

    public void updateMaximum(int number) {

        int current;

        do {
            current = maximum.get();

            if (number <= current) {
                return;
            }

        } while (!maximum.compareAndSet(current, number));
    }

    public int getMaximum() {
        return maximum.get();
    }

    public static void main(String[] args)
            throws InterruptedException {

        LockFreeMaximum obj = new LockFreeMaximum();

        int[] numbers = {10, 50, 90, 30, 100, 70};

        Thread[] threads = new Thread[numbers.length];

        for (int i = 0; i < numbers.length; i++) {

            final int number = numbers[i];

            threads[i] = new Thread(() ->
                    obj.updateMaximum(number));

            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Maximum: " + obj.getMaximum());
    }
}
