//package com.app;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.AsyncResult;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.stereotype.Component;
//import org.springframework.util.concurrent.ListenableFuture;
//
//import java.util.concurrent.Future;
//
//@Slf4j
//@SpringBootApplication
//@EnableAsync
//public class BApplication {
//
//	@Component
//	public static class MyService{
//		@Async
//		public Future<String> hello() throws InterruptedException{
//			log.info("hello()");
//			Thread.sleep(1000);
//			return new AsyncResult<>("hello");
//		}
//		@Async //쓰레드풀 만들어논거 없을땐 SimpleThreadpool 사용하지만 구현해놓은거 있으면 그걸쓴다  여기서는 아래 ThreadPoolTaskExecutor
//		public ListenableFuture<String> helloCallback() throws InterruptedException{
//			log.info("hello()");
//			Thread.sleep(1000);
//			return new AsyncResult<>("hello2222");
//		}
//	}
//
//	@Bean
//	ThreadPoolTaskExecutor tp(){
//		ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
//		te.setCorePoolSize(10);     // 처음 풀 사이즈
//		te.setMaxPoolSize(100);     // 큐에 다차면 100개 더 넣는다.  <-큐를 꽉차면 더 늘려줄께
//		te.setQueueCapacity(200);   // 10개 꽉차면 200개까지 쌓는다
//		te.setThreadNamePrefix("myThread");
//		te.initialize();
//		return te;
//	}
//
//	public static void main(String[] args) {
//		try(ConfigurableApplicationContext c = SpringApplication.run(BApplication.class, args)){
//
//		}
//	}
//
//	@Autowired MyService myService;
//	//모든 빈들이 준비가 되면 실행된다.
//	@Bean
//	ApplicationRunner run(){
//		return args -> {
//			log.info("run()");
//			Future<String> f = myService.hello();
//			log.info("exit {}",f.isDone());
//			log.info("result {}",f.get());  //blocking
//
////			ListenableFuture<String> lf = myService.helloCallback();
////			lf.addCallback(s->{
////				log.info("Success:{}",s);
////			},e->{
////				log.info("fail:{}",e);
////			});
////			log.info("exit exit");
//		};
//	}
//}
