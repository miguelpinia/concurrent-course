package mx.unam.concurrent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class App {
    public static void main(String args[]) throws ExecutionException {
        try {
            ExecutorService executor = Executors.newFixedThreadPool(1);

            List<Callable<String>> callables = Arrays
                .asList(
                        () -> {
                            TimeUnit.SECONDS.sleep(2);
                            return "task 1";
                        },
                        () -> {
                            TimeUnit.SECONDS.sleep(2);
                            return "task 2";
                        },
                        () -> {
                            TimeUnit.SECONDS.sleep(2);
                            return "task 3";
                        });

            executor.invokeAll(callables)
                .stream()
                .map(future -> {
                        try {
                            return future.get();
                        } catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    })
                .forEach(System.out::println);
            executor.shutdown();
        }
        catch (InterruptedException e) {
            System.out.println("Error " + e.getMessage());
            e.printStackTrace();
        }
    }
}
