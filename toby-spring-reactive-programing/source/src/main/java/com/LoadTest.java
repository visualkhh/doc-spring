package com;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class LoadTest {
	static AtomicInteger counter = new AtomicInteger(0);

	public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
		ExecutorService es = Executors.newFixedThreadPool(100);
		CyclicBarrier barrier = new CyclicBarrier(100);


		RestTemplate rt = new RestTemplate();
//		String url = "http://localhost:8080/async";
//		String url = "http://localhost:8080/callable";
		String url = "http://localhost:8080/rest";

		StopWatch main = new StopWatch();
		main.start();


		for (int i = 0; i < 100; i++) {
			es.execute(()->{
				try {
					barrier.await();    //100개될때까지 blocking 100넘으면 동시 실행된다.
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}
				int idx = counter.addAndGet(1);
				log.info("Thread {}",idx);

				StopWatch sw = new StopWatch();
				sw.start();
				rt.getForObject(url, String.class);

				sw.stop();
				log.info("Elapsed : {} {}",idx,sw.getTotalTimeSeconds());
			});
		}
//		barrier.await();
		es.shutdown();
		es.awaitTermination(100, TimeUnit.SECONDS);
		main.stop();
		log.info("Total :{}",main.getTotalTimeSeconds());
	}
}
