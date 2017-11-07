package com;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Ob {
	public static class IntObservable extends Observable implements Runnable{
		@Override
		public void run() {
			for (int i = 0; i < 10; i++) {
				setChanged();
				notifyObservers(i);
			}
		}
	}
	public static void main(String[] args) {
		Observer observer = new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				System.out.println(Thread.currentThread().getName()+"   "+arg);
			}
		};
		IntObservable io = new IntObservable();
		io.addObserver(observer);
		ExecutorService es = Executors.newSingleThreadExecutor();
		es.execute(io);

		System.out.println(Thread.currentThread().getName()+" EXIT");
		es.shutdown();
	}
}
