package mx.unam.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class App {

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

    public static void main(String[] args) throws InterruptedException {
        int numProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors
            .newFixedThreadPool(numProcessors);
        AtomicInteger atomicInt = new AtomicInteger(0);

        IntStream.range(0, 10000)
            .forEach(i -> executor.submit(atomicInt::incrementAndGet));

        stop(executor);
        System.out.println("Value is: " + atomicInt.get());
    }
}
