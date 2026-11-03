
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

public class LockFreeMPMCQueue {

    private final int capacity;
    private final int mask;

    private final AtomicLongArray sequence;
    private final AtomicLongArray values;

    private final AtomicLong producerIndex =
            new AtomicLong(0);

    private final AtomicLong consumerIndex =
            new AtomicLong(0);

    public LockFreeMPMCQueue(int capacity) {

        if (capacity <= 0 ||
            (capacity & (capacity - 1)) != 0) {
            throw new IllegalArgumentException(
                    "Capacity must be a power of 2");
        }

        this.capacity = capacity;
        this.mask = capacity - 1;

        sequence = new AtomicLongArray(capacity);
        values = new AtomicLongArray(capacity);

        for (int i = 0; i < capacity; i++) {
            sequence.set(i, i);
        }
    }

    public boolean offer(long value) {

        long position;

        while (true) {

            position = producerIndex.get();

            int slot = (int) (position & mask);

            long seq = sequence.get(slot);

            long difference = seq - position;

            if (difference == 0) {

                if (producerIndex.compareAndSet(
                        position, position + 1)) {

                    values.set(slot, value);

                    sequence.set(slot, position + 1);

                    return true;
                }

            } else if (difference < 0) {

                return false;

            } else {
                Thread.yield();
            }
        }
    }

    public Long poll() {

        long position;

        while (true) {

            position = consumerIndex.get();

            int slot = (int) (position & mask);

            long seq = sequence.get(slot);

            long difference = seq - (position + 1);

            if (difference == 0) {

                if (consumerIndex.compareAndSet(
                        position, position + 1)) {

                    long value = values.get(slot);

                    sequence.set(
                            slot, position + capacity);

                    return value;
                }

            } else if (difference < 0) {

                return null;

            } else {
                Thread.yield();
            }
        }
    }

    public static void main(String[] args)
            throws InterruptedException {

        LockFreeMPMCQueue queue =
                new LockFreeMPMCQueue(1024);

        Thread producer1 = new Thread(() -> {
            for (int i = 1; i <= 1000; i++) {
                while (!queue.offer(i)) {
                    Thread.yield();
                }
            }
        });

        Thread producer2 = new Thread(() -> {
            for (int i = 1001; i <= 2000; i++) {
                while (!queue.offer(i)) {
                    Thread.yield();
                }
            }
        });

        Thread consumer = new Thread(() -> {

            int count = 0;

            while (count < 2000) {

                Long value = queue.poll();

                if (value != null) {
                    count++;
                } else {
                    Thread.yield();
                }
            }

            System.out.println(
                    "Consumed elements: " + count);
        });

        producer1.start();
        producer2.start();
        consumer.start();

        producer1.join();
        producer2.join();
        consumer.join();
    }
}
