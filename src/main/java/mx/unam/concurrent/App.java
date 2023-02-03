package mx.unam.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class App {

    public static void main(String[] args) throws InterruptedException {
        Counter c = new Counter();
        c.test();
    }
}
class Counter {
    int count = 0;
    synchronized void increment() {
        count = count + 1;
    }
    public void test() throws InterruptedException {
        int numProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors
            .newFixedThreadPool(numProcessors);
        IntStream.range(0, 10000)
            .forEach(i -> executor.submit(this::increment));

        stop(executor);
        System.out.println(count);
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
}
