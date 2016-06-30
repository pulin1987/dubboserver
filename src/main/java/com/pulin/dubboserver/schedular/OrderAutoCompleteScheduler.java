/**
 * 
 */
package com.pulin.dubboserver.schedular;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pulin.dubboserver.to.MongoDBOrderAutoCompleteTO;



//@Service
//@Scope(BeanDefinition.SCOPE_SINGLETON)
public class OrderAutoCompleteScheduler {
	
	private transient final Logger logger=LoggerFactory.getLogger(getClass());
    /**
     * 是否启动
     */
	private static boolean isStart = false;
    /**
     * 是否停止
     */
    private volatile boolean stopPublish = false;
	
	private static final byte[] object = new byte[1];
	/**
	 * 等待队列
	 */
	private final PriorityBlockingQueue<MongoDBOrderAutoCompleteTO> waitingQueue;
	/**
	 * 待分发队列，到达自动完成时间的数据，都放入该队列中
	 */
	private final BlockingQueue<MongoDBOrderAutoCompleteTO> publishingQueue;
	/**
	 * 等待队列waitingQueue周期检查（每秒）线程，到自动完成时间的数据都放入到publishingQueue中
	 */
	private final ScheduledExecutorService waitingQueueChecker;	
	/**
	 * 自动完成工作线程的调度线程
	 */
	private final ExecutorService publishWorkerScheduler;
	/**
	 * 自动完成的工作线程
	 */
	private final ExecutorService publishWorkers;
	/**
	 * 工作线程数
	 */
	private int nWorkers = 2; //默认2个线程发送评论



	public OrderAutoCompleteScheduler(){
		this.waitingQueue = new PriorityBlockingQueue<MongoDBOrderAutoCompleteTO>();
		this.publishingQueue = new LinkedBlockingQueue<MongoDBOrderAutoCompleteTO>();

		this.waitingQueueChecker = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("waitingQueueChecker");
				return t;
			}
		});		
		this.publishWorkerScheduler = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("publishWorkerScheduler");
				return t;
			}
		});		

		this.publishWorkers = Executors.newFixedThreadPool(nWorkers, new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("publishWorkers_"+t.getId());
				return t;
			}
		});

        start();
	}



	/**
	 * 启动调度器
	 */
	public void start(){
		synchronized (object) {
			if(!isStart){
			   //每秒检查publishingQueue是否有需要发送的评论
				waitingQueueChecker.scheduleAtFixedRate(new Runnable() {
					@Override
					public void run() {
						try {
							MongoDBOrderAutoCompleteTO to = waitingQueue.take();
							//评论的发送时间点小于当前时间，则将评论放入发送队列中
							if(to.getExpireTime().getTime() <= System.currentTimeMillis()){
									publishingQueue.add(to);
							}else{
								//如果还没有到自动完成时间，将取出的数据又添加回待发送队列中，等待下次调度的时候继续判断是否该放入发送队列
								waitingQueue.add(to);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}, 0, 1, TimeUnit.SECONDS);

				/**
				 * 自动完成调度器
				 */
				publishWorkerScheduler.execute(new Runnable() {
					@Override
					public void run() {
						while(!stopPublish){
							try {
								MongoDBOrderAutoCompleteTO to = publishingQueue.take();
								publishingQueue.add(to);
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				});
				isStart = true;
				//启动完成
			}else{
				logger.warn("===scheduler aleary started");
			}
		}
	}	

	
	/**
	 * 停止
	 */
	public void stop(boolean now){
		stopPublish = true;
		if(now){
			waitingQueueChecker.shutdownNow();
			publishWorkers.shutdownNow();
			publishWorkerScheduler.shutdownNow();
		}else{
			waitingQueueChecker.shutdown();
			publishWorkers.shutdown();
			publishWorkerScheduler.shutdown();
		}
	}
	/**
	 * 添加需要自动完成的数据到队列
	 */
	public void addToWaitingQueue(MongoDBOrderAutoCompleteTO to){
		if(to != null){
			waitingQueue.add(to);
		}
	}	
	/**
	 * 已到自动完成时间点的数据可直接添加到publishingQueue中进
	 */
	public void addToPublishingQueue(MongoDBOrderAutoCompleteTO to){
		if(to != null){
			publishingQueue.add(to);
		}
	}
	/**
	 * 自动完成Job
	 */
	public class PublishJob implements Runnable{	
		
		private final MongoDBOrderAutoCompleteTO to;//发送的评论数据
		
		public PublishJob(MongoDBOrderAutoCompleteTO to){
			this.to = to;
		}

		@Override
		public void run() {
			exec();
		}
		//处理逻辑
		private void exec(){
			try{
                //
				//
				//
				//
            }catch(Exception e){
                logger.error("e:{}",e);
            }
		}
	}

	

	
}
