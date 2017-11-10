package com.app.publisher;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class BootAppController {
	@RequestMapping("/hello")
	Publisher<String> home(String name) {
		return new Publisher<String>(){
			@Override
			public void subscribe(Subscriber<? super String> s) {
				s.onSubscribe(new Subscription() {
					@Override
					public void request(long n) {
						s.onNext("hello "+name);
						s.onComplete();
					}

					@Override
					public void cancel() {

					}
				});
			}
		};
//		return "Hello World!";
	}

	@RequestMapping("/")
	String idx(String name) {
		return "Hello World!";
	}
	public static void main(String[] args) throws Exception {
		SpringApplication.run(BootAppController.class, args);
	}
}
