package com;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class IntervalEx {
	public static void main(String[] args) {

		Publisher<Integer> pub = sub->{
			sub.onSubscribe(new Subscription() {
				int no = 0;
				boolean cancelled=false;
				@Override
				public void request(long n) {
					ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
					exec.scheduleAtFixedRate(()->{
						if(cancelled){
							exec.shutdown();
							return;
						}
						sub.onNext(no++);
					}, 0, 300, TimeUnit.MICROSECONDS);
				}
				@Override
				public void cancel() {
					cancelled=true;
				}
			});
		};


		Publisher taskPub = sub->{
			pub.subscribe(new Subscriber<Integer>() {
				int count = 0;
				public Subscription subsc;

				@Override
				public void onSubscribe(Subscription s) {
					this.subsc = s;
					sub.onSubscribe(s);

				}

				@Override
				public void onNext(Integer integer) {
					sub.onNext(integer);
					if(++count > 5){
						subsc.cancel();
					}
				}

				@Override
				public void onError(Throwable t) {

				}

				@Override
				public void onComplete() {

				}
			});
		};


		Publisher pubOnSub = sub->{
			taskPub.subscribe(new Subscriber<Integer>() {
				ExecutorService es = Executors.newSingleThreadExecutor(); //쓰레드풀  하나의 쓰레드만 처리가능한 풀
				@Override
				public void onSubscribe(Subscription s) {
					sub.onSubscribe(s);
				}

				@Override
				public void onNext(Integer integer) {
					es.execute(()->sub.onNext(integer));
				}

				@Override
				public void onError(Throwable t) {
					es.execute(()->sub.onError(t));
					es.shutdown();
				}

				@Override
				public void onComplete() {
					es.execute(()->sub.onComplete());
					es.shutdown();
				}
			});
		};


		pubOnSub.subscribe(new Subscriber<Integer>() {
			@Override
			public void onSubscribe(Subscription sub) {
				log.debug("onSubscribe {}",sub);
				sub.request(Long.MAX_VALUE);
			}
			@Override
			public void onNext(Integer integer) {
				log.debug("onNext {}",integer);
			}
			@Override
			public void onError(Throwable t) {
				log.debug("onError {}",t);
			}
			@Override
			public void onComplete() {
				log.debug("onComplete");
			}
		});

		log.debug("exit");
	}
}
