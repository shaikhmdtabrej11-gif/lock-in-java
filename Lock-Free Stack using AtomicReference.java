import java.util.concurrent.atomic.AtomicReference;

public class LockFreeStack<T> {

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    private final AtomicReference<Node<T>> top =
            new AtomicReference<>();

    public void push(T value) {

        Node<T> newNode = new Node<>(value);

        do {
            newNode.next = top.get();

        } while (!top.compareAndSet(
                newNode.next, newNode));
    }

    public T pop() {

        Node<T> oldTop;

        do {
            oldTop = top.get();

            if (oldTop == null) {
                return null;
            }

        } while (!top.compareAndSet(
                oldTop, oldTop.next));

        return oldTop.data;
    }

    public static void main(String[] args) {

        LockFreeStack<Integer> stack =
                new LockFreeStack<>();

        stack.push(10);
        stack.push(20);
        stack.push(30);

        System.out.println(stack.pop());
        System.out.println(stack.pop());
        System.out.println(stack.pop());
    }
}
