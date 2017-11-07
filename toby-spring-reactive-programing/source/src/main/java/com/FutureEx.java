package com;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.*;

@Slf4j
public class FutureEx {

	interface SuccessCallBack{
		void onSuccess(String result);
	}
	interface ExceptionCallBack{
		void onError(Throwable t);
	}

	public static class CallbackFutureTask extends FutureTask<String>{
		private final SuccessCallBack sc;
		private final ExceptionCallBack ec;

		public CallbackFutureTask(Callable<String> callable, SuccessCallBack sc, ExceptionCallBack ec){
			super(callable);
			this.sc = Objects.requireNonNull(sc);
			this.ec = Objects.requireNonNull(ec);
		}

		@Override
		protected void done() {
			try {
				sc.onSuccess(get());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (ExecutionException e) {
				ec.onError(e.getCause());
			}
		}
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ExecutorService es = Executors.newCachedThreadPool();
//		es.execute(()->{
//			try {
//				Thread.sleep(2000);
//				log.debug("Async hello");
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		});


//		Future f = es.submit(()->{
//				Thread.sleep(2000);
//				log.debug("Async hello");
//				return "hello";
//		});
//		log.debug("thread is done? {}",f.isDone());
//		log.debug("future get {}",f.get()); //blocking
//		log.debug("EXIT");
//		log.debug("thread is done? {}",f.isDone());


//		FutureTask futureTask = new FutureTask(()->{
//			Thread.sleep(2000);
//			log.debug("Async hello");
//			return "hello";
//		}){
//			@Override
//			protected void done() {
//				try {
//					log.debug("doen {}",get());
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				} catch (ExecutionException e) {
//					e.printStackTrace();
//				}
//				super.done();
//			}
//		};
		CallbackFutureTask futureTask = new CallbackFutureTask(()->{
			Thread.sleep(2000);
			if(1==1) throw new RuntimeException("Async Error!!");  //강제로 에러
			log.debug("Async hello");
			return "hello";
		}, res->{
			log.debug("result :: {}",res);
		}, thow->{
			log.debug("thow :: {}",thow);
		});
		es.execute(futureTask);
		log.debug("thread is done? {}",futureTask.isDone());
//		log.debug("future get {}",futureTask.get()); //blocking
		log.debug("EXIT");
		log.debug("thread is done? {}",futureTask.isDone());

		es.shutdown();
	}
}
