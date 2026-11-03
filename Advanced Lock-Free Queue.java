
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeQueue<T> {

    private static class Node<T> {
        final T value;
        final AtomicReference<Node<T>> next;

        Node(T value) {
            this.value = value;
            this.next = new AtomicReference<>(null);
        }
    }

    private final AtomicReference<Node<T>> head;
    private final AtomicReference<Node<T>> tail;

    public LockFreeQueue() {
        Node<T> dummy = new Node<>(null);
        head = new AtomicReference<>(dummy);
        tail = new AtomicReference<>(dummy);
    }

    // Enqueue operation
    public void enqueue(T value) {
        Node<T> newNode = new Node<>(value);

        while (true) {
            Node<T> currentTail = tail.get();
            Node<T> next = currentTail.next.get();

            if (currentTail == tail.get()) {

                if (next == null) {

                    if (currentTail.next.compareAndSet(null, newNode)) {
                        tail.compareAndSet(currentTail, newNode);
                        return;
                    }

                } else {
                    tail.compareAndSet(currentTail, next);
                }
            }
        }
    }

    // Dequeue operation
    public T dequeue() {

        while (true) {
            Node<T> currentHead = head.get();
            Node<T> currentTail = tail.get();
            Node<T> next = currentHead.next.get();

            if (currentHead == head.get()) {

                if (currentHead == currentTail) {

                    if (next == null) {
                        return null;
                    }

                    tail.compareAndSet(currentTail, next);

                } else {

                    T value = next.value;

                    if (head.compareAndSet(currentHead, next)) {
                        return value;
                    }
                }
            }
        }
    }

    public static void main(String[] args)
            throws InterruptedException {

        LockFreeQueue<Integer> queue = new LockFreeQueue<>();

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                queue.enqueue(i);
                System.out.println("Inserted: " + i);
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                Integer value;

                while ((value = queue.dequeue()) == null) {
                    Thread.yield();
                }

                System.out.println("Removed: " + value);
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();
    }
}
