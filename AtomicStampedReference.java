
import java.util.concurrent.atomic.AtomicStampedReference;

public class ABAProblem {

    public static void main(String[] args) {

        AtomicStampedReference<Integer> reference =
                new AtomicStampedReference<>(100, 0);

        int[] stampHolder = new int[1];

        Integer value = reference.get(stampHolder);

        int oldStamp = stampHolder[0];

        System.out.println("Initial Value: " + value);
        System.out.println("Initial Stamp: " + oldStamp);

        reference.compareAndSet(100, 200, 0, 1);

        reference.compareAndSet(200, 100, 1, 2);

        boolean result = reference.compareAndSet(
                100, 300, oldStamp, oldStamp + 1);

        System.out.println("CAS Successful: " + result);

        System.out.println("Final Value: " +
                reference.getReference());

        System.out.println("Final Stamp: " +
                reference.getStamp());
    }
}
