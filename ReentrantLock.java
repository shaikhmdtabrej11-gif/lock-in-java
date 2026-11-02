import java.util.concurrent.locks.ReentrantLock;

class BankAccount {

    private int balance = 1000;

    private final ReentrantLock lock = new ReentrantLock();

    public void withdraw(int amount) {

        lock.lock();

        try {

            if (balance >= amount) {

                System.out.println(
                    Thread.currentThread().getName()
                    + " withdrawing " + amount
                );

                balance -= amount;

                System.out.println("Remaining Balance: " + balance);

            } else {

                System.out.println("Insufficient Balance");
            }

        } finally {

            lock.unlock();
        }
    }
}

public class Main {

    public static void main(String[] args) {

        BankAccount account = new BankAccount();

        Thread t1 = new Thread(
            () -> account.withdraw(500), "Thread-1"
        );

        Thread t2 = new Thread(
            () -> account.withdraw(700), "Thread-2"
        );

        t1.start();
        t2.start();
    }
}
