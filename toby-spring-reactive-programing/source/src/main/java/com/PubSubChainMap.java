package com;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class PubSubChainMap {
	public static void main(String[] args) throws InterruptedException {
		Iterable<Integer> iter = Stream.iterate(1,a->a+1).limit(10).collect(Collectors.toList());
		Publisher pub = iterPub(iter);
		Publisher mapPub1 = mapPub(pub, (Function<Integer,Integer>) s->s*10);
		Publisher mapPub2 = mapPub(mapPub1, (Function<Integer,Integer>) s->-s);
//		Publisher mapPub3 = sumPub(pub);
		Subscriber s = logSub();
		mapPub2.subscribe(s); //Publisher에 <- Subscriber 등록
	}

	private static Publisher sumPub(Publisher pub) {
		return new Publisher() {
			int sum = 0;
			@Override
			public void subscribe(Subscriber sub) {
				pub.subscribe(new DelegateSub(sub){
					@Override
					public void onNext(Integer o) {
						sum+=o;
						log.debug("-->{}",sum);
					}

					@Override
					public void onComplete() {
						log.debug("-c->{}",sum);
						sub.onNext(sum);
						sub.onComplete();
					}
				});
			}
		};
	}

	private static Publisher<Integer> mapPub(Publisher pub, Function<Integer, Integer> integerIntegerFunction) {
		return new Publisher<Integer>(){
			@Override
			public void subscribe(Subscriber<? super Integer> sub) {
				pub.subscribe(new DelegateSub(sub) {
					@Override
					public void onNext(Integer o) {
						super.onNext(integerIntegerFunction.apply(o));
					}
				});
			}
		};
	}

	private static Publisher iterPub(Iterable<Integer> iter) {
		return new Publisher() {
			@Override
			public void subscribe(Subscriber subscriber) {
				Iterator it = iter.iterator();
				subscriber.onSubscribe(new Subscription() { //Subscription통해 SubScripber에 전달
					@Override
					public void request(long n) {
						try {
							log.debug("request {}",n);
							int i=0;
							while (i++<n) {
								if (it.hasNext()) {
									subscriber.onNext(it.next());
								}else{
									subscriber.onComplete();
									break;
								}
							}
						}catch (Throwable t){
							subscriber.onError(t);
						}
					}

					@Override
					public void cancel() {

					}
				});
			}
		};
	}

	private static Subscriber logSub() {
		return new Subscriber() {
			private Subscription subscription;

			@Override
			public void onSubscribe(Subscription subscription) {
				log.debug(Thread.currentThread().getName()+" onSubscribe");
				this.subscription = subscription;
				this.subscription.request(1);

			}

			@Override
			public void onNext(Object o) {
				log.debug(Thread.currentThread().getName()+" onNext "+o);
				this.subscription.request(1);
			}

			@Override
			public void onError(Throwable t) {
				log.debug(Thread.currentThread().getName()+" onError "+t);
			}

			@Override
			public void onComplete() {
				log.debug(Thread.currentThread().getName()+" onComplete");
			}
		};
	}
}
