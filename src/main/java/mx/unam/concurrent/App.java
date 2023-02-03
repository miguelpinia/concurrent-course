package mx.unam.concurrent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Phaser;
import java.util.stream.IntStream;

public class App {
    public static void main(String[] args) {
        int numProcessors = Runtime.getRuntime().availableProcessors();
        Phaser phaser = new Phaser();
        ExecutorService executor = Executors.newFixedThreadPool(numProcessors);
        System.out.println("Spawning Threads");
        phaser.register(); // registering main thread
        IntStream.range(0, numProcessors)
            .forEach(i -> {
                    String name = String.format("Thread-%d", i);
                    executor.execute(new WorkerThread(phaser, name));
                });
        System.out.println("Spawning Finished");
        phaser.arriveAndDeregister();
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
    private Phaser phaser;
    private String name;
    public WorkerThread(Phaser phaser, String name) {
        this.name = name;
        this.phaser = phaser;
        this.phaser.register();
    }
    public void run() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        System.out.printf("%s:[%s] Doing Step 1 Work\n",
                          getFormattedDate(sdf), name);
        sleep(getRandomWaitTime());
        System.out.printf("%s:[%s] Doing Step 1 more work\n",
                          getFormattedDate(sdf), name);
        sleep(getRandomWaitTime());
        System.out.printf("%s:[%s] Finished Step 1 work\n",
                          getFormattedDate(sdf), name);
        phaser.arriveAndAwaitAdvance();
        System.out.printf("%s:[%s] Past the barrier.\n",
                          getFormattedDate(sdf), name);
        int phase = phaser.getPhase();
        // here we had a reset with CyclicBarrier
        System.out.printf("%s:[%s] Phaser count on %d\n",
                          getFormattedDate(sdf), name, phase);
        System.out.printf("%s:[%s] Doing Step 2 Batch of Work\n",
                          getFormattedDate(sdf), name);
        sleep(getRandomWaitTime());
        System.out.printf("%s:[%s] Doing Some more Step 2 Batch of work\n",
                          getFormattedDate(sdf), name);
        sleep(getRandomWaitTime());
        System.out.printf("%s:[%s] Finished Step 2 Batch of work\n",
                          getFormattedDate(sdf), name);
        phaser.arriveAndAwaitAdvance();
        phase = phaser.getPhase();
        System.out.printf("%s:[%s] Phaser finish on: %d\n",
                          getFormattedDate(sdf), name, phase);
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
