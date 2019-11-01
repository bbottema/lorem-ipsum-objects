package org.bbottema.loremipsumobjects.typefactories.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@UtilityClass
public class TimeLimitedCodeBlock {

	public static void runWithTimeout(long timeout, TimeUnit timeUnit, final Runnable runnable) throws Exception {
		runWithTimeout(timeout, timeUnit, new Callable<Object>() {
			@Override @Nullable
			public Object call() {
				runnable.run();
				return null;
			}
		});
	}

	public static void runWithTimeout(long timeout, TimeUnit timeUnit, final RunnableWithException runnable) throws Exception {
		runWithTimeout(timeout, timeUnit, new Callable<Object>() {
			@Override @Nullable
			public Object call() throws Exception {
				runnable.run();
				return null;
			}
		});
	}

	@SuppressWarnings({"UnusedReturnValue"})
	@Nullable
	public static <T> T runWithTimeout(final long timeout, final TimeUnit timeUnit, final Callable<T> callable) throws Exception {
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<T> future = executor.submit(callable);
		executor.shutdown(); // This does not cancel the already-scheduled task.
		try {
			return future.get(timeout, timeUnit);
		} catch (TimeoutException e) {
			future.cancel(true);
			throw e;
		} catch (ExecutionException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public interface RunnableWithException {
		void run() throws Exception;
	}
}