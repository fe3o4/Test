package ThreadTest;

import java.util.Random;

public class SynchronizedTest implements Runnable{

	public static int i = 0;

	private int ticket = 5;  //5’≈∆±
	
	public void run() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        for (int i=0; i<=20; i++) {
            if (this.ticket > 0) {
            	
                System.out.println(Thread.currentThread().getName()+ " ’˝‘⁄¬Ù∆± "+this.ticket--);
            }
        }
	}
	
	public static void main(String[] args){
		SynchronizedTest st1 = new SynchronizedTest();
		SynchronizedTest st2 = new SynchronizedTest();
		Thread t1 = new Thread(st1);
		Thread t2 = new Thread(st2);
		try {
			t1.start();
			t2.start();
			t1.join();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
