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
        System.out.println("Thread 1 is running");
    }
}

class MyThread2 extends Thread {
    @Override
    public void run() {
        System.out.println("Thread 2 is running");
    }
}

class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("My runnable object is running");
    }
}
