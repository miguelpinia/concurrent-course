package mx.unam.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class App {
    public static void main(String args[]) throws ExecutionException {
        Callable<Integer> task = () -> {
            try {
                TimeUnit.SECONDS.sleep(4);
                return 42;
            }
            catch (InterruptedException e) {
                System.out.println("Error " + e.getMessage());
                e.printStackTrace();
            }
            return -1;
        };

        ExecutorService executor = Executors.newFixedThreadPool(1);
        try {
            Future<Integer> future = executor.submit(task);
            System.out.printf("Future done? %b\n", future.isDone());
            Integer result = future.get(1, TimeUnit.SECONDS);
            System.out.printf("Future done? %b\n", future.isDone());
            System.out.printf("Result: %d\n", result);
            executor.shutdown();
        }
        catch (InterruptedException | ExecutionException
               | TimeoutException e) {
            System.out.printf("Error %s\n", e.getLocalizedMessage());
            e.printStackTrace();
            executor.shutdown();
        }
    }
}
