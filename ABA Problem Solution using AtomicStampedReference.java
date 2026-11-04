import java.util.concurrent.atomic.AtomicStampedReference;

public class ABAProblem {

    public static void main(String[] args)
            throws InterruptedException {

        AtomicStampedReference<Integer> value =
                new AtomicStampedReference<>(100, 0);

        int[] stampHolder = new int[1];

        Integer oldValue = value.get(stampHolder);
        int oldStamp = stampHolder[0];

        Thread t1 = new Thread(() -> {

            value.compareAndSet(100, 200, 0, 1);

            value.compareAndSet(200, 100, 1, 2);

            System.out.println("Value changed A -> B -> A");
        });

        t1.start();
        t1.join();

        boolean result = value.compareAndSet(
                oldValue, 300, oldStamp, oldStamp + 1
        );

        System.out.println("CAS Successful: " + result);
        System.out.println("Current Value: " + value.getReference());
        System.out.println("Current Stamp: " + value.getStamp());
    }
}
