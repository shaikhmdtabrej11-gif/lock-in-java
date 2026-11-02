import java.util.concurrent.locks.ReentrantLock;

public class TryLockExample {

    static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {

            if (lock.tryLock()) {

                try {
                    System.out.println("Thread 1 acquired lock");

                    Thread.sleep(2000);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                } finally {
                    lock.unlock();
                    System.out.println("Thread 1 released lock");
                }

            } else {
                System.out.println("Thread 1 could not acquire lock");
            }
        });

        Thread t2 = new Thread(() -> {

            if (lock.tryLock()) {

                try {
                    System.out.println("Thread 2 acquired lock");

                } finally {
                    lock.unlock();
                }

            } else {
                System.out.println("Thread 2 could not acquire lock");
            }
        });

        t1.start();
        t2.start();
    }
}
