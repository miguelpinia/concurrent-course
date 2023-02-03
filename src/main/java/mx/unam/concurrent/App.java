package mx.unam.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class App {

    public static void main(String[] args) throws InterruptedException {
        int numProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors
            .newFixedThreadPool(numProcessors);
        ReentrantLock lock = new ReentrantLock();
        executor.submit(() -> {
                lock.lock();
                try {
                    TimeUnit.SECONDS.sleep(4);
                    System.out.println("wake up");
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } finally {
                    lock.unlock();
                }
            });
        executor.submit(() -> {
                System.out.printf("Locked: %b\n", lock.isLocked());
                System.out.printf("Held by me: %b\n",
                                  lock.isHeldByCurrentThread());
                boolean locked = lock.tryLock();
                System.out.printf("Lock aquired: %b\n", locked);
            });
        executor.shutdown();
    }
}
