
import java.util.concurrent.atomic.AtomicInteger;

public class AdvancedLockFreeCounter {

    private final AtomicInteger counter = new AtomicInteger(0);

    public void increment() {
        int oldValue;
        int newValue;

        do {
            oldValue = counter.get();
            newValue = oldValue + 1;

        } while (!counter.compareAndSet(oldValue, newValue));
    }

    public int getValue() {
        return counter.get();
    }

    public static void main(String[] args)
            throws InterruptedException {

        AdvancedLockFreeCounter obj =
                new AdvancedLockFreeCounter();

        Runnable task = () -> {
            for (int i = 0; i < 100000; i++) {
                obj.increment();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        Thread t3 = new Thread(task);
        Thread t4 = new Thread(task);

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        t1.join();
        t2.join();
        t3.join();
        t4.join();

        System.out.println("Final Counter: " + obj.getValue());
    }
}
