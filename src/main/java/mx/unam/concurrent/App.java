package mx.unam.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.Map;
import java.util.HashMap;

public class App {

    public static void main(String[] args) throws InterruptedException {
        int numProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.
            newFixedThreadPool(numProcessors);
        Map<String, String> map = new HashMap<>();
        ReadWriteLock lock = new ReentrantReadWriteLock();
        executor.submit(() -> {
                lock.writeLock().lock();
                try {
                    System.out.println("Putting information into the map");
                    TimeUnit.SECONDS.sleep(4);
                    map.put("foo", "bar");
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } finally {
                    lock.writeLock().unlock();
                }
            });

        Runnable readTask = () -> {
            lock.readLock().lock();
            try {
                String threadName = Thread.currentThread().getName();
                System.out.printf("Name %s, value: %s\n",
                                  threadName, map.get("foo"));
                TimeUnit.SECONDS.sleep(1);
            } catch(InterruptedException ex) {
                ex.printStackTrace();
            } finally {
                lock.readLock().unlock();
            }
        };

        executor.submit(readTask);
        executor.submit(readTask);

        executor.shutdown();
    }
}
