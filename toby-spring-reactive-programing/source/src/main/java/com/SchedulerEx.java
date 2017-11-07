package com;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
public class SchedulerEx {
	public static void main(String[] args) {

		Publisher<Integer> pub = sub->{
			sub.onSubscribe(new Subscription() {
				@Override
				public void request(long n) {
					log.debug("request {}",n);
					sub.onNext(1);
					sub.onNext(2);
					sub.onNext(3);
					sub.onNext(4);
					sub.onNext(5);
					sub.onComplete();
				}
				@Override
				public void cancel() {
				}
			});
		};


		Publisher subOnPub = sub->{
			ExecutorService es = Executors.newSingleThreadExecutor(); //쓰레드풀  하나의 쓰레드만 처리가능한 풀
			es.execute(()->pub.subscribe(sub));
		};


		Publisher pubOnSub = sub->{
			subOnPub.subscribe(new Subscriber<Integer>() {
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
