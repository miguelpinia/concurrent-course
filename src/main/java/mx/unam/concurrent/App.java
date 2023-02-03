package mx.unam.concurrent;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class App {

    public static void main(String[] argv){
        int numProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService consumerExecutors = Executors.newFixedThreadPool(numProcessors);
        List<Message> queue = Collections.synchronizedList(new LinkedList<Message>());
        CountDownLatch doneSignal = new CountDownLatch(1);
        CountDownLatch doneProducingSignal = new CountDownLatch(1);
        CountDownLatch doneConsumingSignal = new CountDownLatch(numProcessors);
        IntStream.range(0, numProcessors)
            .forEach(i -> {
                    String name = String.format("%d", i);
                    consumerExecutors.execute(new Consumer(name, queue,
                                                           doneProducingSignal,
                                                           doneConsumingSignal));
                });
        queue.add(new Message( "1", 15000, doneSignal));
        queue.add(new Message( "2", 15000, new CountDownLatch(1)));
        doneProducingSignal.countDown();
        boolean doneProcessing = false;
        try {
            doneProcessing = doneSignal.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        if ( doneProcessing ){
            System.out.println( "Processing is done.");
        } else {
            System.out.println( "Processing is still running.");
        }

        System.out.println( "Shutting down the consumerExecutors");
        doneProducingSignal.countDown();
        try {
            doneConsumingSignal.await();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        consumerExecutors.shutdown();
        System.out.println( "Done");
    }
}

class Consumer implements Runnable {
    private String id;
    private List<Message> queue;
    private CountDownLatch doneProducing;
    private CountDownLatch doneConsuming;

    Consumer(String id, List<Message> queue,
             CountDownLatch doneProducing,
             CountDownLatch doneConsuming){
        this.id = id;
        this.queue = queue;
        this.doneProducing = doneProducing;
        this.doneConsuming = doneConsuming;
    }

    @Override
    public void run() {
        while(doneProducing.getCount() != 0 || !queue.isEmpty()){
            Message m = null;
            synchronized(queue){
                if(!queue.isEmpty()) m = queue.remove(0);
            }
            if(m != null) consume(m);
        }
        System.out.printf("Consumer %s done\n", id);
        doneConsuming.countDown();
    }
    public void consume(Message m ){
        System.out.printf("Consumer %s consuming message %s\n",
                          id, m.getId());
        try {
            Thread.sleep(m.getTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Consumer %s done consumming msssage %s\n",
                          id, m.getId());
        m.getLatch().countDown();
    }
}

class Message {
    private String id;
    private int time;
    private CountDownLatch latch;
    Message(String id, int time, CountDownLatch latch){
        this.id = id;
        this.time = time;
        this.latch = latch;
    }
    public String getId() {
        return id;
    }
    public int getTime() {
        return time;
    }
    public CountDownLatch getLatch() {
        return latch;
    }
}
