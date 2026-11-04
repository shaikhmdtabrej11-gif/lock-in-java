import java.util.concurrent.atomic.AtomicReference;

public class LockFreeLinkedList<T> {

    static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    private final AtomicReference<Node<T>> head =
            new AtomicReference<>(null);

    public void add(T value) {

        Node<T> newNode = new Node<>(value);

        while (true) {
            Node<T> current = head.get();

            newNode.next = current;

            if (head.compareAndSet(current, newNode)) {
                return;
            }
        }
    }

    public void display() {

        Node<T> current = head.get();

        while (current != null) {
            System.out.print(current.data + " -> ");
            current = current.next;
        }

        System.out.println("NULL");
    }

    public static void main(String[] args) {

        LockFreeLinkedList<Integer> list =
                new LockFreeLinkedList<>();

        list.add(10);
        list.add(20);
        list.add(30);

        list.display();
    }
}
