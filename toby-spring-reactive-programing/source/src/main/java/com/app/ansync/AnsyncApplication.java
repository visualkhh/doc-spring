package com.app.ansync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;

@Slf4j
@SpringBootApplication
@EnableAsync
public class AnsyncApplication {

	@RestController
	public static class MyController{


		@GetMapping("/emitter")
		public ResponseBodyEmitter   emitter() throws InterruptedException {
			ResponseBodyEmitter emitter = new ResponseBodyEmitter();
			Executors.newSingleThreadExecutor().submit(()->{
				try {
					for (int i = 0; i < 50; i++) {
						emitter.send("<p>a"+i+"+sd</p>");
						Thread.sleep(500);
					}
				}catch (Exception e){
					log.error("ee",e);
				}
			});

			return emitter;
		}






//		Queue<DeferredResult<String>> results = new ConcurrentLinkedDeque<>();
//		@GetMapping("/dr")
//		public DeferredResult<String> dr() throws InterruptedException {
//			log.info("dr");
//			DeferredResult dr = new DeferredResult<>(600000L);
//			results.add(dr);
//			return dr;
//		}
//
//		@GetMapping("/dr/count")
//		public String drcount() throws InterruptedException {
//			return String.valueOf(results.size());
//		}
//		@GetMapping("/dr/event")
//		public String drevent(String msg) throws InterruptedException {
//			for (DeferredResult<String> dr : results){
//				dr.setResult("Hello "+msg);
//				results.remove(dr);
//			}
//			return "OK";
//		}
//
//
//		@GetMapping("/async")
//		public String async() throws InterruptedException {
//			Thread.sleep(2000);
//			return "hello";
//		}
//
//
//		@GetMapping("/callable")
//		public Callable<String> callable() throws InterruptedException {
//			log.info("callable");
//			return ()->{
//				log.info("async");
//				Thread.sleep(2000);
//				return "hello";
//			};
//		}
	}


	public static void main(String[] args) {
		SpringApplication.run(AnsyncApplication.class, args);
	}

}
