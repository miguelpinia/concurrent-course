package mx.unam.concurrent;

public class App {

    public static void main(String[] args) {
        MyThread1 obj1 = new MyThread1();
        MyThread2 obj2 = new MyThread2();
        Thread t = new Thread(new MyRunnable());

        obj1.start();
        obj2.start();
        t.start();
    }
}

class MyThread1 extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println(String.format("Thread 1 is running. Iter: %d", i));
        }
    }
}

class MyThread2 extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println(String.format("Thread 2 is running. Iter: %d", i));
        }
    }
}

class MyRunnable implements Runnable {
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println(String.format("My runnable object is running. Iter: %d", i));
        }
    }
}
