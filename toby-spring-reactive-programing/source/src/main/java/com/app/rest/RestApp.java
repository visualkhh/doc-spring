package com.app.rest;

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

import java.util.function.Consumer;
import java.util.function.Function;

@SpringBootApplication
@EnableAsync
public class RestApp {
	@RestController
	public static class MyController{
		@Autowired MyService myService;
//		RestTemplate rt = new RestTemplate();
		AsyncRestTemplate rt = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory());
		@GetMapping("/rest")
		public DeferredResult<String> rest(int idx){
//		public String rest(int idx){
//			String res = rt.getForObject("http://localhost:8081/service?req={req}",String.class,"hello "+idx);

			DeferredResult<String> dr = new DeferredResult<>();
//			ListenableFuture<ResponseEntity<String>> res = rt.getForEntity("http://localhost:8081/service?req={req}",String.class,"hello "+idx);
//			res.addCallback(s->{
//				ListenableFuture<String> f2 = myService.work(s.getBody());
//				f2.addCallback(s2->{
//					dr.setResult(s2);
//				},e->{
//					dr.setErrorResult(e.getMessage());
//				});
//
//			}, e->{
//				dr.setErrorResult(e.getMessage());
//			});

			ListenableFuture<ResponseEntity<String>> res1 = rt.getForEntity("http://localhost:8081/service1?req={req}",String.class,"hello "+idx);
			ListenableFuture<ResponseEntity<String>> res2 = rt.getForEntity("http://localhost:8081/service2?req={req}",String.class,"hello "+idx);
			Completion
					.from(res1)
					.andApply(s->res2)
//					.andApply(s->myService.work(s))
					.andError(e->dr.setErrorResult(e.getMessage()))
					.andAccept(s->dr.setResult(s.getBody()));
			return dr;
		}
	}



	public static class AcceptCompletion extends Completion{
		Consumer<ResponseEntity<String>> con;
		public AcceptCompletion(Consumer<ResponseEntity<String>> con) {
			this.con = con;
		}

		@Override
		void run(ResponseEntity<String> value) {
			con.accept(value);
		}
	}
	public static class ErrorCompletion extends Completion{
		Consumer<Throwable> econ;
		public ErrorCompletion(Consumer<Throwable> econ) {
			this.econ = econ;
		}

		@Override
		void run(ResponseEntity<String> value) {
			if(next!=null) next.run(value);
		}

		@Override
		void error(Throwable e) {
			econ.accept(e);
		}
	}
	public static class ApplyCompletion extends Completion{
		Function<ResponseEntity<String>, ListenableFuture<ResponseEntity<String>>> fn;
		public ApplyCompletion(Function<ResponseEntity<String>, ListenableFuture<ResponseEntity<String>>> fn) {
			this.fn = fn;
		}

		@Override
		void run(ResponseEntity<String> value) {
			ListenableFuture<ResponseEntity<String>> lf = fn.apply(value);
			lf.addCallback(s->complate(s), e->error(e));
		}
	}

	public static class Completion{
		Completion next;
		public Completion() {}




		public static Completion from(ListenableFuture<ResponseEntity<String>> lf){
			Completion c = new Completion();
			lf.addCallback(s->{
				c.complate(s);
			},e->{
				c.error(e);
			});

			return c;

		}
		public void andAccept(Consumer<ResponseEntity<String>> con){
			Completion c = new AcceptCompletion(con);
			this.next = c;

		}
		public Completion andError(Consumer<Throwable> econ){
			Completion c = new ErrorCompletion(econ);
			this.next = c;
			return c;
		}
		public Completion andApply(Function<ResponseEntity<String>, ListenableFuture<ResponseEntity<String>>> fn){
			Completion c = new ApplyCompletion(fn);
			this.next = c;
			return c;
		}
		void error(Throwable e) {
			if(next!=null) next.error(e);
		}

		void complate(ResponseEntity<String> s) {
			if(next!=null)next.run(s);
		}

		void run(ResponseEntity<String> value) {

		}


	}

	@Service
	public static class MyService{
		@Async
		public ListenableFuture<String> work(String req){
			return new AsyncResult<>(req+"/asyncwork");
		}
	}

	@Bean
	public ThreadPoolTaskExecutor myThreadPool(){
		ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
		te.setCorePoolSize(1);
		te.setMaxPoolSize(1);
		te.initialize();
		return te;
	}

	public static void main(String[] args) {
		SpringApplication.run(RestApp.class, args);
	}
}
