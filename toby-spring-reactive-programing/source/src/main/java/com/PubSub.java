//package com;
//
//import org.reactivestreams.Publisher;
//import org.reactivestreams.Subscriber;
//import org.reactivestreams.Subscription;
//
//import java.util.Arrays;
//import java.util.Iterator;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//public class PubSub {
//	public static void main(String[] args) throws InterruptedException {
//		Iterable<Integer> iter = Stream.iterate(1,a->a+1).limit(10).collect(Collectors.toList());
//		ExecutorService es = Executors.newSingleThreadExecutor();
//		Publisher p = new Publisher() {
//			@Override
//			public void subscribe(Subscriber subscriber) {
//				Iterator it = iter.iterator();
//				subscriber.onSubscribe(new Subscription() { //Subscription통해 SubScripber에 전달
//					@Override
//					public void request(long n) {
//							es.execute(()->{
//								try {
//									int i=0;
//									while (i++<n) {
//										if (it.hasNext()) {
//											subscriber.onNext(it.next());
//										}else{
//											subscriber.onComplete();
//											break;
//										}
//									}
//								}catch (Throwable t){
//									subscriber.onError(t);
//								}
//							});
//					}
//
//					@Override
//					public void cancel() {
//
//					}
//				});
//			}
//		};
//
//		Subscriber s = new Subscriber() {
//			private Subscription subscription;
//
//			@Override
//			public void onSubscribe(Subscription subscription) {
//				System.out.println(Thread.currentThread().getName()+" onSubscribe");
//				this.subscription = subscription;
//				this.subscription.request(1);
//
//			}
//
//			@Override
//			public void onNext(Object o) {
//				System.out.println(Thread.currentThread().getName()+" onNext "+o);
//				this.subscription.request(1);
//			}
//
//			@Override
//			public void onError(Throwable t) {
//				System.out.println(Thread.currentThread().getName()+" onError "+t);
//			}
//
//			@Override
//			public void onComplete() {
//				System.out.println(Thread.currentThread().getName()+" onComplete");
//			}
//		};
//
//		p.subscribe(s); //Publisher에 <- Subscriber 등록
//		es.awaitTermination(10, TimeUnit.HOURS);
//		es.shutdown();
//	}
//}
