import java.util.concurrent.atomic.AtomicReference;

public class LockFreeBankAccount {

    static class Account {
        final int balance;

        Account(int balance) {
            this.balance = balance;
        }
    }

    private final AtomicReference<Account> account =
            new AtomicReference<>(new Account(10000));

    public void withdraw(int amount) {

        while (true) {

            Account current = account.get();

            if (current.balance < amount) {
                System.out.println("Insufficient Balance");
                return;
            }

            Account updated =
                    new Account(current.balance - amount);

            if (account.compareAndSet(current, updated)) {
                System.out.println("Withdraw Successful");
                return;
            }
        }
    }

    public int getBalance() {
        return account.get().balance;
    }

    public static void main(String[] args) {

        LockFreeBankAccount bank =
                new LockFreeBankAccount();

        Thread t1 = new Thread(() -> bank.withdraw(2000));
        Thread t2 = new Thread(() -> bank.withdraw(3000));

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Final Balance: " + bank.getBalance());
    }
}
