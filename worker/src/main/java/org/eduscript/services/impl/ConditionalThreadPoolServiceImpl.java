package org.eduscript.services.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ConditionalThreadPoolServiceImpl {  //TODO: create an interface

    private final static Logger logger = LoggerFactory.getLogger(ConditionalThreadPoolServiceImpl.class);
    private final ExecutorService executorService;

    public ConditionalThreadPoolServiceImpl(
            @Qualifier("cachedTPool") ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * Executes a task while monitoring a using a custom condition supplier
     * 
     * @param task            The task to execute
     * @param redisKey        The Redis key to monitor
     * @param cancelValue     The value that triggers cancellation (e.g., "CANCEL",
     *                        "STOP")
     * @param checkIntervalMs How often to check Redis (milliseconds)
     * @param timeoutMs       Maximum time to wait before giving up (milliseconds)
     * @return TaskResult containing the result or cancellation info
     */
    public <T> CompletableFuture<TaskResult<T>> executeWithCondition(
            Supplier<T> task,
            BooleanSupplier cancelCondition,
            long checkIntervalMs,
            long timeoutMs) {

        CompletableFuture<TaskResult<T>> resultFuture = new CompletableFuture<>();
        CancellationTracker tracker = new CancellationTracker();

        Future<T> processingTask = executorService.submit(() -> {
            try {
                if (tracker.isCancelled())
                    return null;
                T result = task.get();
                return tracker.isCancelled() ? null : result;
            
            }
            catch (Exception e) {
                if (!tracker.isCancelled()) {
                    throw new RuntimeException("Task failed", e);
                }
                return null;
            }
        });

        Future<Void> monitoringTask = executorService.submit(() -> {
            try {
                while (!processingTask.isDone() && !tracker.isCancelled()) {
                    if (cancelCondition.getAsBoolean()) {
                        tracker.cancel();
                        processingTask.cancel(true);
                        break;
                    }
                    Thread.sleep(checkIntervalMs);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                System.err.println("Error checking condition: " + e.getMessage());
            }
            return null;
        });

        try {
            T result = processingTask.get(timeoutMs, TimeUnit.MILLISECONDS);
            resultFuture.complete(tracker.isCancelled() ? TaskResult.cancelled() : TaskResult.success(result));
        } catch (Exception e) {
            resultFuture.complete(TaskResult.error(e));
        } finally {
            monitoringTask.cancel(true);
        }

        return resultFuture;
    }

    private static class CancellationTracker {
        private volatile boolean cancelled = false;

        public void cancel() {
            this.cancelled = true;
        }

        public boolean isCancelled() {
            return cancelled;
        }
    }

    public static class TaskResult<T> {
        private final T result;
        private final TaskStatus status;
        private final Exception error;

        private TaskResult(T result, TaskStatus status, Exception error) {
            this.result = result;
            this.status = status;
            this.error = error;
        }

        public static <T> TaskResult<T> success(T result) {
            return new TaskResult<>(result, TaskStatus.SUCCESS, null);
        }

        public static <T> TaskResult<T> cancelled() {
            return new TaskResult<>(null, TaskStatus.CANCELLED, null);
        }

        public static <T> TaskResult<T> timeout() {
            return new TaskResult<>(null, TaskStatus.TIMEOUT, null);
        }

        public static <T> TaskResult<T> error(Exception error) {
            return new TaskResult<>(null, TaskStatus.ERROR, error);
        }

        public T getResult() {
            return result;
        }

        public TaskStatus getStatus() {
            return status;
        }

        public Exception getError() {
            return error;
        }

        public boolean isSuccess() {
            return status == TaskStatus.SUCCESS;
        }

        public boolean isCancelled() {
            return status == TaskStatus.CANCELLED;
        }

        public boolean isTimeout() {
            return status == TaskStatus.TIMEOUT;
        }

        public boolean isError() {
            return status == TaskStatus.ERROR;
        }
    }

    public enum TaskStatus {
        SUCCESS, CANCELLED, TIMEOUT, ERROR
    }
}
