package mx.unam.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class App {

    private static void doLongWork(String name) {
        String message = String.format("Hello %s, how is going?", name);
        System.out.println(message);
        try {
            Thread.sleep(1001);
        }
        catch (InterruptedException e) {
            System.out.println("Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int numProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors
            .newFixedThreadPool(numProcessors);
        for (int i = 0; i < numProcessors; i++) {
            final int name = i;
            executor.execute(() -> doLongWork(String.format("thread %d",
                                                            name)));
        }
        executor.shutdown();
    }
}
