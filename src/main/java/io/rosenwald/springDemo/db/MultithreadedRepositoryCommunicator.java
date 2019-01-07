package io.rosenwald.springDemo.db;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.util.Assert;

import io.rosenwald.springDemo.DemoApplication;

/**
 * Provides multi-threaded and batched persisting to a JpaRepository of any entity type.
 * 
 * This was created to fix performance issues regarding JpaRepository's saveAll() and deleteAll() methods. These 
 * methods would attempt to persist/delete an entire iterable in a single batch statement no matter the size of the 
 * iterable. The vehicle CSV data provided ~40000 rows of data, so data import could take 30-60 minutes. Now, I'm 
 * observing deletes of 35000+ rows in less than 30 seconds and persists of 35000+ rows in less than 2 minutes with a 
 * Google Cloud Platform MySQL server. Performance has degraded a bit more now that I am running my own MariaDB server 
 * on a $7/month VPS.
 * 
 * This would not be possible without increasing the pool size of the repository. Depending on the batch size set, a 
 * different number of repository connections is required until some threads will begin to time out. See the 
 * application.properties file on how to increase the maximum size of the repository connection pool. The maximum size 
 * is currently set to 50.
 * 
 * This is NOT a stable production sample and should not entirely be used as so as it has not been fully tested.
 * 
 * @author Nathaniel Rosenwald
 * 
 * TODO: Improve debug logging stability and error handling.
 *
 * @param <T> The type of entity to be persisted/deleted.
 */
public class MultithreadedRepositoryCommunicator<T> {
	
	private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);
	
	private List<Thread> threads;
	private JpaRepository<T, String> repo;
	private int batchSize;
	private int batchesComplete;
	private int batchesToBeCompleted;

	/**
	 * @param repo The repository to delete/persist data.
	 * @param batchSize The maximum size of the batch statements.
	 */
	public MultithreadedRepositoryCommunicator(JpaRepository<T, String> repo, int batchSize) {
		Assert.notNull(repo, "The provided repository must not be null.");
		Assert.state(batchSize > 0, "The provided batch size must not less than or equal to 0.");
		this.repo = repo;
		this.batchSize = batchSize;
		threads = new ArrayList<Thread>();
		batchesComplete = 0;
	}
	
	/**
	 * Creates and starts the threads.
	 * 
	 * @param entities The entities to be persisted/deleted.
	 * @param action The action to perform to the provided entities. 
	 * 		{@link io.rosenwald.springDemo.db.MultithreadedRepositoryCommunicator.BatchAction}
	 * @throws IllegalArgumentException The provided batch action or list of entities is null.
	 * @throws IllegalStateException The list of entities is empty.
	 * @throws InterruptedException 
	 */
	public void start(List<T> entities, BatchAction action) throws IllegalArgumentException, IllegalStateException, InterruptedException {
		Assert.notNull(action, "The batch action must not be null.");
		Assert.notNull(entities, "The list of entities must not be null.");
		Assert.state(!entities.isEmpty(), "The list must contain at least one entity");
		
		batchesToBeCompleted = (int) Math.ceil((double)(entities.size() / batchSize));
		ExecutorService execService = Executors.newCachedThreadPool();
		for (int i = 0; i <= entities.size() + batchSize; i += batchSize) {
			final int index = i;
			String threadName = "batch-" + action.getDescriptor() + "-" + i;
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					List<T> batch;
					if (index + batchSize > entities.size()) {
						if (entities.size() - index < 1) return;
						batch = entities.subList(index, entities.size());
					} else {
						batch = entities.subList(index, index + batchSize);
					}
					
					if (batch.contains(null)) {
						batch = batch.subList(0, batch.indexOf(null));
					}
					if (action == BatchAction.SAVE) {
						repo.saveAll(batch);
					} else {
						repo.deleteInBatch(batch);
					}
					logSingleBatchComplete(threadName);
				}
			};
			execService.submit(runnable);
		}
		
		execService.shutdown();
		execService.awaitTermination(20, TimeUnit.MINUTES);
	}
	
	private synchronized void logSingleBatchComplete(String threadName) {
		batchesComplete++;
		if (batchesToBeCompleted == batchesComplete) {
			threads.clear();
			batchesComplete = 0;
			logger.debug("Finished batches.");
		} else {
			logger.debug("Completed " + threadName + ". \tCompleted " + batchesComplete + " batches. ");
		}
	}
	
	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
	
	/**
	 * Possible actions to perform for a batch operation. 
	 * 
	 * @author Nathaniel Rosenwald
	 *
	 */
	public static enum BatchAction {
		SAVE("SAVE"),
		DELETE("DELETE");
		
		private String descriptor;
		
		BatchAction(String descriptor) {
			this.descriptor = descriptor;
		}
		
		public String getDescriptor() {
			return descriptor;
		}
	}
}
