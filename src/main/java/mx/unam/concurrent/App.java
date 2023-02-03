package mx.unam.concurrent;

public class App {

    public static void main(String[] args) {
        MyThread t1 = new MyThread("First Thread");
        MyThread t2 = new MyThread("Second Thread");

        try {
            Thread.sleep(500); // Sleeping for 500ms
            t1.stop();
            t2.stop();
            Thread.sleep(500);
        } catch (InterruptedException e) {
            System.out.format("Interrupted Exception: %s\n",
                    e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Exiting the main thread");
    }
}

class MyThread implements Runnable {

    private boolean exit;
    private String name;
    Thread t;

    public MyThread(String threadName) {
        name = threadName;
        t = new Thread(this, name);
        System.out.format("New Thread: %s\n", t.toString());
        exit = false;
        t.start(); // Starting the thread
    }

    @Override
    public void run() {
        int i = 0;
        while (!exit) {
            System.out.format("%s: %d\n", name, i);
            i++;
            try {
                Thread.sleep(100); // Sleeping for 100ms
            }
            catch (InterruptedException e) {
                System.out.format("Interrupted Exception:  %s\n",
                                  e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        exit = true;

    }
}
