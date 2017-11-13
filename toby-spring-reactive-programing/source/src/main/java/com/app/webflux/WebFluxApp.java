package com.app.webflux;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@SpringBootApplication
@RestController
@Slf4j
@EnableAsync
public class WebFluxApp {

	WebClient client = WebClient.create();
	@Autowired MyService myService;

	@GetMapping("/rest")
	public Mono<String> index(int idx){
		Mono<ClientResponse> res = client.get().uri("http://localhost:8081/service?req={req}",idx).exchange();
		Mono<String> body = res
				.flatMap(clientResponse -> clientResponse.bodyToMono(String.class))
				.doOnNext(it -> log.info(it.toString()))
				.flatMap(res1->client.get().uri("http://localhost:8081/service2?req={req}",idx).exchange())
				.doOnNext(it -> log.info(it.toString()))
				.flatMap(clientResponse -> clientResponse.bodyToMono(String.class))
				.doOnNext(it -> log.info(it.toString()))
				.flatMap(res2->Mono.fromCompletionStage(myService.work(res2)));
		return body;
	}
	@GetMapping("/flux")
	public Flux<User> user(int idx){
		return Flux.just(
				new User(1,"1val"),
				new User(2,"2val"),
				new User(3,"3val")
		);
	}
	@GetMapping(value = "/flux_stream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<User> stream(int idx){
		List<User> list = new ArrayList<>();
		list.add(new User(1,"1val"));
		list.add(new User(2,"2val"));
		list.add(new User(3,"3val"));
		return Flux.fromIterable(list);
	}
	@GetMapping(value = "/flux_stream1",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<User> stream1(int idx){
//		List<User> list = new ArrayList<>();
//		list.add(new User(1,"1val"));
//		list.add(new User(2,"2val"));
//		list.add(new User(3,"3val"));
//		Stream<User> userStream = list.stream();

		Stream<User> userStream = Stream.generate(()->new User(1,"value"));
		return Flux.fromStream(userStream).delayElements(Duration.ofSeconds(1)).take(10);
	}
	@GetMapping(value = "/flux_stream2",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<User> stream2(int idx){
//		return Flux.<User>generate(sink->sink.next(new User(2,"value"))).delayElements(Duration.ofSeconds(1)).take(10);

//		Flux<User> es =  Flux.<User,Integer>generate(()->1,(id,sink)->{
//			sink.next(new User(id,"value"+id));
//			return id+1;
//		}).delayElements(Duration.ofSeconds(1)).take(10);
//		return es;
//
//		Flux<User> es =  Flux.<User,Integer>generate(()->1,(id,sink)->{
//			sink.next(new User(id,"value"+id));
//			return id+1;
//		}).delayElements(Duration.ofSeconds(1));
//
//		Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
//		return Flux.zip(es, interval).map(tu->tu.getT1()).take(10);

		Flux<String> es = Flux.<String>generate(sink->sink.next("value"));
		Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
		return Flux.zip(es,interval).map(tu->new User(tu.getT2(),tu.getT1())).take(10);

	}
	@GetMapping("/mono")
	public Mono<List<User>> mono(int idx){
		List<User> list = new ArrayList<>();
		list.add(new User(1,"1val"));
		list.add(new User(2,"2val"));
		list.add(new User(3,"3val"));
		Mono<List<User>> mono = Mono.just(list);
		return mono;
	}


	@GetMapping("/")
	Mono<String> hello(){
		log.info("position1");
		String g = generateHello();
//		Mono<String> mono =  Mono.just(g).doOnNext(c->log.info(c)).log();
		Mono<String> mono =  Mono.fromSupplier(()->generateHello()).doOnNext(c->log.info(c)).log();
		log.info("position2");
		return mono;
	}

	private String generateHello() {
		log.info("generateHello");
		return "just";
	}


	@AllArgsConstructor @Data
	public static class User{
		long id;
		String value;
	}
	@Service
	public static class MyService {
		@Async
		public CompletableFuture<String> work(String req){
			return CompletableFuture.completedFuture(req+"/work");
		}
	}
	public static void main(String[] args) {
		SpringApplication.run(WebFluxApp.class,args);
	}
}
