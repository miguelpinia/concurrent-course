package mx.unam.concurrent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class App {
    public static void main(String[] args) {
        int numProcessors = Runtime.getRuntime().availableProcessors();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(numProcessors,
                                                        new CyclicTask());
        ExecutorService executor = Executors.newFixedThreadPool(numProcessors);
        System.out.println("Spawning Threads");
        IntStream.range(0, numProcessors)
            .forEach(i -> {
                    String name = String.format("Thread-%d", i);
                    executor.execute(new WorkerThread(cyclicBarrier, name));
                });
        System.out.println("Spawning Finished");
        stop(executor);
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

class WorkerThread implements Runnable {
    private CyclicBarrier cyclicBarrier;
    private String name;

    public WorkerThread(CyclicBarrier cyclicBarrier, String name) {
        this.name = name;
        this.cyclicBarrier = cyclicBarrier;
    }

    public void run() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            System.out.printf("%s: Doing Step 1 Work on %s\n",
                              getFormattedDate(sdf), name);
            sleep(getRandomWaitTime());
            System.out.printf("%s: Doing Step 1 more work on %s\n",
                              getFormattedDate(sdf), name);
            sleep(getRandomWaitTime());
            System.out.printf("%s: Finished Step 1 work on %s\n",
                              getFormattedDate(sdf), name);
            // Await returns for the other threads
            int count = cyclicBarrier.await();
            System.out.printf("%s: Cyclic Barrier count on %s is %d\n",
                              getFormattedDate(sdf), name, count);
            // If all threads have arrived 2 lines above, reset the barrier
            if(count == 0) {
                cyclicBarrier.reset();
            }
            System.out.printf("%s: Doing Step 2 Batch of Work on %s\n",
                              getFormattedDate(sdf), name);
            sleep(getRandomWaitTime());
            System.out.printf("%s: Doing Some more Step 2 Batch of work on %s\n",
                              getFormattedDate(sdf), name);
            sleep(getRandomWaitTime());
            System.out.printf("%s: Finished Step 2 Batch of work on %s\n",
                              getFormattedDate(sdf), name);
            count = cyclicBarrier.await();
            String template = "%s: Cyclic Barrier count end of " +
                "Step 2 Batch of work on %s is %d\n";
            System.out.printf(template, getFormattedDate(sdf), name, count);
        } catch(InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
    public static void sleep(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
    private String getFormattedDate(SimpleDateFormat sdf) {
        return sdf.format(new Date());
    }
    private int getRandomWaitTime() {
        return (int) ((Math.random() + 1) * 1000);
    }
}
class CyclicTask implements Runnable {
    private int count = 1;

    @Override
    public void run() {
        System.out.printf("Cyclic Barrier Finished %d\n", count++);
    }
}
