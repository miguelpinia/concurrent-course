package mx.unam.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Semaphore;
import java.util.stream.IntStream;

public class App {

    public static void main(String[] args) throws InterruptedException {
        int numProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numProcessors);
        Semaphore semaphore = new Semaphore(numProcessors / 2);

        Runnable longRunningTask = () -> {
            boolean permit = false;
            try {
                permit = semaphore.tryAcquire(1, TimeUnit.SECONDS);
                if (permit) {
                    System.out.println("Semaphore acquired");
                    sleep(5);
                } else {
                    System.out.println("Could not acquire semaphore");
                }
            }
            catch (InterruptedException e) {
                System.out.println("Error " + e.getMessage());
                e.printStackTrace();
            } finally {
                if (permit) {
                    semaphore.release();
                }
            }
        };

        IntStream.range(0, numProcessors)
            .forEach(i -> executor.submit(longRunningTask));

        stop(executor);
        System.out.println("Finishing program");
    }

    public static void stop(ExecutorService executor) {
        try {
            executor.shutdown();
            // give it time to finish
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            if (!executor.isTerminated()) {
                System.out.println("Termination interrupted");
            }
            executor.shutdown();
        }
    }

    public static void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
