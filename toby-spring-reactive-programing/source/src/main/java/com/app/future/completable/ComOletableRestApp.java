package com.app.future.completable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

@SpringBootApplication
@EnableAsync
public class ComOletableRestApp {
	@RestController
	public static class MyController{
		@Autowired MyService myService;
		AsyncRestTemplate rt = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory());
		@GetMapping("/rest")
		public DeferredResult<String> rest(int idx){

			DeferredResult<String> dr = new DeferredResult<>();

			ListenableFuture<ResponseEntity<String>> res1 = rt.getForEntity("http://localhost:8081/service1?req={req}",String.class,"hello "+idx);
			ListenableFuture<ResponseEntity<String>> res2 = rt.getForEntity("http://localhost:8081/service2?req={req}",String.class,"hello "+idx);

			toCF(res1).thenCompose(s->{
				return toCF(res2);
			}).thenCompose(s->{
				return toCF(myService.work(s.getBody()));
			}).thenAccept(s->{
				dr.setResult(s);
			}).exceptionally(e->{
				dr.setErrorResult(e.getMessage());
				return (Void)null;
			});

			return dr;
		}
	}


	static <T> CompletableFuture<T> toCF(ListenableFuture<T> lf){
		CompletableFuture cf = new CompletableFuture();
		lf.addCallback(s->{
			cf.complete(s);
		},e->{
			cf.completeExceptionally(e);
		});
		return cf;
	}

	@Service
	public static class MyService{
		@Async
		public ListenableFuture<String> work(String req){
			return new AsyncResult<>(req+"/asyncwork");
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(ComOletableRestApp.class, args);
	}
}
