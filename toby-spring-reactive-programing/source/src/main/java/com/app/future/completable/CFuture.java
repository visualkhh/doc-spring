package com.app.future.completable;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class CFuture {
	public static void main(String[] args) throws ExecutionException, InterruptedException {
//		CompletableFuture.runAsync(()->{
//			log.info("RunAsync");
//		}).thenRun(()->{
//			log.info("thenRun 1");
//		}).thenRun(()->{
//			log.info("thenRun 2");
//		});

//		CompletableFuture.supplyAsync(()->{
//			log.info("supplyAsync");
//			return 1;
//		}).thenApply(s->{
//			log.info("thenApply {}",s);
//			return s+1;
//		}).thenAccept(s->{
//			log.info("thenAccept {}",s);
//		});

//		CompletableFuture.supplyAsync(()->{
//			log.info("supplyAsync");
//			return 1;
//		}).thenCompose(s->{
//			log.info("thenCompose {}",s);
//			if(1==1)throw new RuntimeException();
//			return CompletableFuture.completedFuture(s+1);
//		}).thenApply(s->{
//			log.info("thenApply {}",s);
//			return s*3;
//		})
//		.exceptionally(s-> -10)
//		.thenAccept(s->{
//			log.info("thenAccept {}",s);
//		});


		ExecutorService es = Executors.newFixedThreadPool(10);

		CompletableFuture.supplyAsync(()->{
			log.info("supplyAsync");
			return 1;
		},es)
		.thenCompose(s->{
			log.info("thenCompose {}",s);
			return CompletableFuture.completedFuture(s+1);
		})
		.thenApplyAsync(s->{
			log.info("thenApply {}",s);
			return s*3;
		},es)
		.exceptionally(s-> -10)
		.thenAcceptAsync(s->{
			log.info("thenAccept {}",s);
		},es);

		log.info("EXIT");

		ForkJoinPool.commonPool().shutdown();
		ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);
	}
}
