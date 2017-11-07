package com;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FluxScEx {
	public static void main(String[] args) throws InterruptedException {
//		pubSubOn();

		Flux.interval(Duration.ofMillis(500))
				.take(5)
				.subscribe(s->log.debug("onNext:{}",s));
		TimeUnit.SECONDS.sleep(10);
		log.debug("exit");
	}

	private static void pubSubOn() {
		Flux.range(1,10)
				.publishOn(Schedulers.newSingle("pub"))
				.log()
				.subscribeOn(Schedulers.newSingle("sub"))
				.subscribe(System.out::println);
	}
}
