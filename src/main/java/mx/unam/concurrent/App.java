package mx.unam.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class App {
    public static void main(String[] args) {
        Counter c = new Counter();
        c.test();
    }
}

class Counter {
    int count = 0;
    void increment() {
        count = count + 1;
    }
    public void test() {
        int numProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numProcessors);
        IntStream.range(0, 10000)
            .forEach(i -> executor.submit(this::increment));
        executor.shutdown();
        System.out.println(count);
    }
}
