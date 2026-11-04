import java.util.concurrent.atomic.AtomicInteger;

public class LockFreeCounter {

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

        LockFreeCounter obj = new LockFreeCounter();

        int threads = 10;
        int iterations = 100000;

        Thread[] workers = new Thread[threads];

        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    obj.increment();
                }
            });

            workers[i].start();
        }

        for (Thread t : workers) {
            t.join();
        }

        System.out.println("Final Counter: " + obj.getValue());
    }
}
